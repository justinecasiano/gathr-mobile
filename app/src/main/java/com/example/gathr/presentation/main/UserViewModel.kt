package com.example.gathr.presentation.main

import LocalCacheManager
import android.net.Uri
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateEventStep
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.ManagedEvent
import com.example.gathr.data.model.Notification
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.User
import com.example.gathr.data.model.UserRole
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.data.repository.EventParticipantRepository
import com.example.gathr.data.repository.NotificationRepository
import com.example.gathr.data.repository.UserRepository
import com.example.gathr.presentation.participant.CreateEventScreen
import com.example.gathr.utils.NetworkConnectivityService
import com.example.gathr.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.UUID

class UserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val eventParticipantRepository: EventParticipantRepository,
    private val notificationRepository: NotificationRepository,
    private val cacheManager: LocalCacheManager,
    private val networkService: NetworkConnectivityService
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val _userEffect = MutableSharedFlow<UserEffect>()
    val userEffect: SharedFlow<UserEffect> = _userEffect.asSharedFlow()

    private val _mainEffect = MutableSharedFlow<MainEffect>()
    val mainEffect: SharedFlow<MainEffect> = _mainEffect.asSharedFlow()

    private var pollingJob: Job? = null
    private var attendanceRealtimeJob: Job? = null
    private var globalRealtimeJob: Job? = null

    init {
        observeNetwork()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkService.observeNetworkStatus().collect { isOnline ->
                if (!isOnline) loadFromCache()
            }
        }
    }

    private suspend fun loadFromCache() {
        cacheManager.getCache()?.let { cachedState ->
            _state.update {
                it.copy(
                    currentUser = cachedState.currentUser,
                    managedEvents = cachedState.managedEvents,
                    joinableEvents = cachedState.joinableEvents.filter { event -> event.status == EventApprovalStatus.APPROVED },
                    joinedEvents = cachedState.joinedEvents.filter { event -> event.status == EventApprovalStatus.APPROVED },
                    notifications = cachedState.notifications,
                    dataFetchStatus = cachedState.dataFetchStatus
                )
            }
        }
    }

    suspend fun loadCacheImmediately(): Boolean {
        val cachedState = cacheManager.getCache()
        return if (cachedState != null) {
            _state.update { currentState ->
                currentState.copy(
                    currentUser = cachedState.currentUser,
                    managedEvents = cachedState.managedEvents,
                    joinableEvents = cachedState.joinableEvents.filter { event -> event.status == EventApprovalStatus.APPROVED },
                    joinedEvents = cachedState.joinedEvents.filter { event -> event.status == EventApprovalStatus.APPROVED },
                    notifications = cachedState.notifications,
                    dataFetchStatus = currentState.dataFetchStatus.copy(
                        managedEvents = FetchStatus.DONE,
                        joinableEvents = FetchStatus.DONE,
                        joinedEvents = FetchStatus.DONE,
                        notifications = FetchStatus.DONE,
                        currentUser = FetchStatus.DONE
                    )
                )
            }
            true
        } else {
            false
        }
    }

    fun onFetchSuccess() {
        val currentState = _state.value
        viewModelScope.launch {
            val cache = UserCache(
                managedEvents = currentState.managedEvents,
                joinableEvents = currentState.joinableEvents,
                joinedEvents = currentState.joinedEvents,
                notifications = currentState.notifications,
                currentUser = currentState.currentUser,
                dataFetchStatus = currentState.dataFetchStatus
            )
            cacheManager.saveCache(cache)
        }
    }

    fun startGlobalRealtime() {
        val currentUser = _state.value.currentUser ?: return
        globalRealtimeJob?.cancel()

        globalRealtimeJob = viewModelScope.launch {
            eventParticipantRepository.observeEventsAndParticipants().collect { updatedEventId ->
                Log.d("GLOBAL_REALTIME", "Event $updatedEventId changed. Refreshing...")
                refreshSpecificEvent(updatedEventId)
                fetchNotifications()
            }
        }
    }

    private fun refreshSpecificEvent(eventId: Long) {
        val user = _state.value.currentUser ?: return

        viewModelScope.launch {
            val result = eventParticipantRepository.fetchSingleEvent(eventId, user.id)

            if (result is ApiResult.Success) {
                val freshEvent = result.data
                Log.d("FRESH_EVENT", freshEvent.toString())
                Log.d("FRESH_EVENT_USER", freshEvent.userRole.toString())

                _state.update { currentState ->
                    val newCurrent =
                        if (currentState.currentEvent?.id == eventId) freshEvent else currentState.currentEvent

                    val isJoinedEligible =
                        freshEvent.isRegistered && freshEvent.userRole == ParticipantType.ATTENDEE
                    Log.d("FRESH_EVENT_IS_JOINED", isJoinedEligible.toString())

                    val newJoined =
                        updateListWithEvent(currentState.joinedEvents, freshEvent, isJoinedEligible)

                    val isJoinableEligible =
                        !freshEvent.isRegistered && freshEvent.userRole == ParticipantType.ATTENDEE
                    Log.d("FRESH_EVENT_IS_JOINABLE", isJoinableEligible.toString())

                    val newJoinable = updateListWithEvent(
                        currentState.joinableEvents, freshEvent, isJoinableEligible
                    )

                    val isManagedEligible =
                        freshEvent.userRole == ParticipantType.ORGANIZER || freshEvent.userRole == ParticipantType.STAFF
                    Log.d("FRESH_EVENT_IS_MANAGED", isManagedEligible.toString())

                    val newManaged = if (isManagedEligible) {
                        if (currentState.managedEvents.any { it.event.id == eventId }) {
                            currentState.managedEvents.map {
                                if (it.event.id == eventId) it.copy(
                                    event = freshEvent, userParticipantType = freshEvent.userRole
                                ) else it
                            }
                        } else {
                            currentState.managedEvents + ManagedEvent(
                                freshEvent, freshEvent.userRole, emptyList(), emptyList()
                            )
                        }
                    } else {
                        currentState.managedEvents.filter { it.event.id != eventId }
                    }

                    currentState.copy(
                        currentEvent = newCurrent,
                        joinedEvents = newJoined.filter { event -> event.status == EventApprovalStatus.APPROVED },
                        joinableEvents = newJoinable.filter { event -> event.status == EventApprovalStatus.APPROVED },
                        managedEvents = newManaged
                    )
                }
                onFetchSuccess()
            }
        }
    }

    private fun updateListWithEvent(
        list: List<Event>, event: Event, eligible: Boolean
    ): List<Event> {
        return if (eligible) {
            if (list.any { it.id == event.id }) {
                list.map { if (it.id == event.id) event else it }
            } else {
                list + event
            }
        } else {
            list.filter { it.id != event.id }
        }
    }

    fun startPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(120_000L)
                fetchManagedEvents()
                fetchJoinableEvents()
                fetchJoinedEvents()
                fetchNotifications()
            }
        }
    }

    fun handleIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.CurrentEventChanged -> {
                attendanceRealtimeJob?.cancel()
                _state.update { it.copy(currentEvent = intent.value) }
                if (intent.value != null) {
                    fetchAttendance(true)
                    startAttendanceRealtime(intent.value.id)
                }
            }

            is UserIntent.UpdateUserProfile -> {
                updateUserProfile(
                    intent.firstName,
                    intent.lastName,
                    intent.displayName,
                    intent.newAvatarUri
                )
            }

            is UserIntent.CurrentCreateEventChanged -> {
                _state.update { it.copy(currentCreateEvent = intent.value) }
                viewModelScope.launch {
                    fetchAvailableStaff()
                }
            }

            is UserIntent.ScanParticipantChanged -> {
                _state.update { it.copy(scanParticipant = intent.value) }
            }

            is UserIntent.MarkAttendance -> markAttendance()
            is UserIntent.MarkNotificationAsRead -> markNotificationAsRead(intent.value)
            is UserIntent.MarkAllNotificationsAsRead -> markAllNotificationsAsRead()

            is UserIntent.SearchStaffChanged -> {
                _state.update {
                    it.copy(searchStaff = intent.value)
                }
            }

            is UserIntent.ActionTitleChanged -> _state.update { it.copy(actionTitle = intent.value) }
            is UserIntent.ActionErrorChanged -> _state.update { it.copy(actionError = intent.value) }
            is UserIntent.ActionOnConfirmClicked -> _state.update { it.copy(actionOnConfirm = intent.value) }
            is UserIntent.ActionOnClear -> _state.update {
                it.copy(actionTitle = "", actionError = "", actionOnConfirm = {})
            }

            is UserIntent.IsLoadingChanged -> _state.update { it.copy(isLoading = intent.value) }

            is UserIntent.FetchManagedEvents -> fetchManagedEvents()
            is UserIntent.FetchJoinableEvents -> fetchJoinableEvents()
            is UserIntent.FetchJoinedEvents -> fetchJoinedEvents()
            is UserIntent.FetchEventToUpdate -> fetchEventToUpdate()
            is UserIntent.FetchAvailableStaff -> viewModelScope.launch { fetchAvailableStaff() }
            is UserIntent.FetchNotifications -> fetchNotifications()

            is UserIntent.CancelEvent -> cancelEvent()
            is UserIntent.RegisterEvent -> registerEvent()

            is UserIntent.DeleteEvent -> deleteEvent()
            is UserIntent.VerifyCreateEvent -> verifyCreateEvent()
            is UserIntent.DoCreateEvent -> createEvent()
            is UserIntent.UpdateEvent -> updateEvent()
            is UserIntent.CreateEventNextStepClicked -> _state.update {
                val createEvent =
                    it.currentCreateEvent.copy(createEventStep = CreateEventStep.ADD_STAFF)
                it.copy(currentCreateEvent = createEvent)
            }

            is UserIntent.CreateEventPreviousStepClicked -> {
                val createEvent = _state.value.currentCreateEvent

                if (createEvent.createEventStep == CreateEventStep.ADD_STAFF) {
                    val nextStep = createEvent.copy(createEventStep = CreateEventStep.BASIC_INFO)
                    _state.update { it.copy(currentCreateEvent = nextStep) }
                } else {
                    _state.update { it.copy(currentCreateEvent = CreateEvent()) }
                    sendUserEffect(UserEffect.NavigateBack)
                }
            }

            is UserIntent.LogoutClicked -> logout()
            is UserIntent.BackClicked -> sendUserEffect(UserEffect.NavigateBack)

            is UserIntent.ActiveEventsFilterChanged -> _state.update { it.copy(activeEventsFilter = intent.value) }
            is UserIntent.EventsSearchTextChanged -> _state.update { it.copy(eventsSearchText = intent.value) }
            is UserIntent.MyEventsSearchTextChanged -> _state.update { it.copy(myEventsSearchText = intent.value) }
            is UserIntent.MyEventsSelectedTabChanged -> _state.update {
                it.copy(
                    myEventsSelectedTabIndex = intent.value
                )
            }

            is UserIntent.ETicketsSearchTextChanged -> _state.update { it.copy(eTicketsSearchText = intent.value) }
            is UserIntent.ETicketsSelectedTabChanged -> _state.update {
                it.copy(
                    eTicketsSelectedTabIndex = intent.value
                )
            }

            is UserIntent.StaffSearchTextChanged -> _state.update { it.copy(staffSearchText = intent.value) }
            is UserIntent.StaffSelectedTabChanged -> _state.update {
                it.copy(
                    staffSelectedTabIndex = intent.value
                )
            }
        }
    }

    private fun fetchManagedEvents() {
        viewModelScope.launch {
            val state = _state.value

            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(managedEvents = FetchStatus.LOADING)) }

            val result = eventParticipantRepository.fetchManagedEvents()
            var managedEvents: List<ManagedEvent> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> managedEvents = result.data
                is ApiResult.Error -> actionError = result.message
            }

            if (managedEvents.isNotEmpty() || (state.managedEvents.isEmpty() && managedEvents.isEmpty())) {
                _state.update {
                    it.copy(
                        managedEvents = managedEvents,
                        dataFetchStatus = it.dataFetchStatus.copy(managedEvents = FetchStatus.DONE)
                    )
                }
                onFetchSuccess()
            }

            Log.d("FETCH_MANAGED_EVENTS", managedEvents.toString())
            Log.d("FETCH_MANAGED_EVENTS_ERROR", actionError)
        }
    }

    private fun fetchJoinableEvents() {
        viewModelScope.launch {
            val state = _state.value
            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(joinableEvents = FetchStatus.LOADING)) }

            val result = eventParticipantRepository.fetchJoinableEvents()
            var joinableEvents: List<Event> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> joinableEvents = result.data
                is ApiResult.Error -> actionError = result.message
            }

            if (joinableEvents.isNotEmpty() || (state.joinableEvents.isEmpty() && joinableEvents.isEmpty())) {
                _state.update { it ->
                    it.copy(
                        joinableEvents = joinableEvents.filter { event -> event.status == EventApprovalStatus.APPROVED },
                        dataFetchStatus = it.dataFetchStatus.copy(joinableEvents = FetchStatus.DONE)
                    )
                }
                onFetchSuccess()
            }

            Log.d("FETCH_JOINABLE_EVENTS", joinableEvents.toString())
            Log.d("FETCH_JOINABLE_EVENTS_ERROR", actionError)
        }
    }

    private fun fetchJoinedEvents() {
        viewModelScope.launch {
            val state = _state.value
            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(joinedEvents = FetchStatus.LOADING)) }

            val result = eventParticipantRepository.fetchJoinedEvents()
            var joinedEvents: List<Event> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> joinedEvents = result.data
                is ApiResult.Error -> actionError = result.message
            }

            if (joinedEvents.isNotEmpty() || (state.joinedEvents.isEmpty() && joinedEvents.isEmpty())) {
                _state.update { it ->
                    it.copy(
                        joinedEvents = joinedEvents.filter { event -> event.status == EventApprovalStatus.APPROVED },
                        dataFetchStatus = it.dataFetchStatus.copy(joinedEvents = FetchStatus.DONE)
                    )
                }
                onFetchSuccess()
            }

            Log.d("FETCH_JOINED_EVENTS", joinedEvents.toString())
            Log.d("FETCH_JOINED_EVENTS_ERROR", actionError)
        }
    }

    private fun fetchEventToUpdate() {
        val currentState = _state.value
        val currentEvent = currentState.currentEvent!!
        val currentUser = currentState.currentUser!!

        viewModelScope.launch {
            handleIntent(UserIntent.IsLoadingChanged(true))
            val result =
                eventParticipantRepository.fetchUserEventToUpdate(currentEvent.id, currentUser.id)

            var selectedManagedEvent: ManagedEvent? = null
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {
                    selectedManagedEvent = result.data
                    val event = selectedManagedEvent.event

                    val staffs = selectedManagedEvent.staffList.map { participant ->
                        val nameParts = participant.fullName?.split(" ") ?: emptyList()
                        val fName = nameParts.getOrNull(0) ?: ""
                        val lName =
                            if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""

                        CreateStaff(
                            eventId = participant.eventId.toInt(),
                            userId = participant.userId,
                            email = participant.email,
                            displayName = participant.displayName ?: "",
                            avatarUrl = participant.avatarUrl ?: "",
                            firstName = fName,
                            lastName = lName,
                            participantType = participant.participantType
                        )
                    }

                    _state.update {
                        it.copy(
                            currentCreateEvent = CreateEvent(
                                id = event.id,
                                title = event.title,
                                description = event.description,
                                location = event.location,
                                capacity = event.capacity,
                                backgroundImage = event.backgroundImage,
                                oldBackgroundImageUrl = event.backgroundImage,
                                allowedDepartments = event.allowedDepartments ?: emptyList(),
                                allowAlumni = event.allowAlumni,
                                startDateAndTime = event.startTime,
                                endDateAndTime = event.endTime,
                                staffs = staffs
                            ),
                        )
                    }
                    fetchAvailableStaff()
                    handleIntent(UserIntent.IsLoadingChanged(false))
                }

                is ApiResult.Error -> {
                    actionError = result.message
                    _state.update { it.copy(actionError = actionError) }
                    handleIntent(UserIntent.IsLoadingChanged(false))
                }
            }

            Log.d("FETCH_EVENT_TO_UPDATE", selectedManagedEvent.toString())
            Log.d("FETCH_EVENT_TO_UPDATE_ERROR", actionError)
        }
    }

    private suspend fun fetchAvailableStaff() {
        val currentState = _state.value
        val currentUser = currentState.currentUser!!
        val selectedIds = currentState.currentCreateEvent.staffs.map { it.userId.toString() }

        _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(availableStaff = FetchStatus.LOADING)) }

        val result = eventParticipantRepository.fetchAvailableStaff(
            currentUser.id.toString(), selectedIds
        )
        var availableStaff: List<User> = emptyList()
        var actionError = ""

        when (result) {
            is ApiResult.Success -> availableStaff = result.data
            is ApiResult.Error -> actionError = result.message
        }

        _state.update {
            it.copy(
                availableStaff = availableStaff,
                dataFetchStatus = it.dataFetchStatus.copy(availableStaff = FetchStatus.DONE)
            )
        }

        Log.d("FETCH_AVAILABLE_STAFF", availableStaff.toString())
        Log.d("FETCH_AVAILABLE_STAFF_ERROR", actionError)
    }

    private fun fetchNotifications() {
        viewModelScope.launch {
            _state.update {
                it.copy(dataFetchStatus = it.dataFetchStatus.copy(notifications = FetchStatus.LOADING))
            }

            val result = notificationRepository.fetchNotifications()

            var notifications: List<Notification> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {
                    notifications = result.data
                    _state.update {
                        it.copy(
                            notifications = notifications,
                        )
                    }
                }

                is ApiResult.Error -> {
                    Log.d("FETCH_NOTIFICATIONS_ERROR", result.message)
                }
            }

            _state.update {
                it.copy(
                    dataFetchStatus = it.dataFetchStatus.copy(notifications = FetchStatus.DONE),
                )
            }
            if (result is ApiResult.Success) {
                onFetchSuccess()
            }

            Log.d("FETCH_NOTIFICATIONS", notifications.toString())
            if (actionError.isNotEmpty()) Log.e("FETCH_NOTIFICATIONS_ERROR", actionError)
        }
    }

    private fun fetchAttendance(isSilent: Boolean = false) {
        val currentState = _state.value
        val currentEvent = currentState.currentEvent!!

        viewModelScope.launch {
            if (!isSilent) handleIntent(UserIntent.IsLoadingChanged(true))

            val result = eventParticipantRepository.fetchAttendance(currentEvent.id)
            var currentAttendees: List<Participant> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> currentAttendees = result.data
                is ApiResult.Error -> actionError = result.message
            }

            _state.update { it.copy(currentAttendees = currentAttendees) }
            handleIntent(UserIntent.IsLoadingChanged(false))

            Log.d("FETCH_ATTENDANCE", currentAttendees.toString())
            Log.d("FETCH_ATTENDANCE_ERROR", actionError)
        }
    }

    private fun startAttendanceRealtime(eventId: Long) {
        attendanceRealtimeJob?.cancel()
        attendanceRealtimeJob = viewModelScope.launch {
            eventParticipantRepository.observeAttendance(eventId).collect {
                fetchAttendance(true)
            }
        }
    }

    private fun markAttendance() {
        val currentState = _state.value
        val participant = currentState.scanParticipant!!

        viewModelScope.launch {
            val result = eventParticipantRepository.markAttendance(
                participant.eventId, UUID.fromString(participant.userId)
            )
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> actionError = result.message
            }

            _state.update {
                it.copy(actionError = actionError)
            }

            Log.d("MARK_ATTENDANCE", actionError)

            if (actionError.isBlank()) {
                handleIntent(UserIntent.IsLoadingChanged(false))
                handleIntent(UserIntent.ActionTitleChanged("Mark Attendance"))
                handleIntent(UserIntent.ActionErrorChanged("User is marked as present"))
            } else {
                handleIntent(UserIntent.ActionTitleChanged("Mark Attendance Failed"))
                handleIntent(UserIntent.ActionErrorChanged("An unknown error occurred"))
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }

    private fun markNotificationAsRead(notificationId: Long) {
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedNotifications = currentState.notifications.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                }
                currentState.copy(notifications = updatedNotifications)
            }

            val result = notificationRepository.markAsRead(notificationId)

            if (result is ApiResult.Error) {
                fetchNotifications()
                Log.e("MARK_NOTIFICATION", "Failed to mark as read: ${result.message}")
            } else {
                onFetchSuccess()
            }
        }
    }

    private fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedNotifications = currentState.notifications.map {
                    it.copy(isRead = true)
                }
                currentState.copy(notifications = updatedNotifications)
            }

            val result = notificationRepository.markAllAsRead()

            if (result is ApiResult.Error) {
                fetchNotifications()
                Log.e("MARK_ALL_NOTIFICATION", "Failed to mark all as read: ${result.message}")
            } else {
                onFetchSuccess()
            }
        }
    }

    private fun registerEvent() {
        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentEvent!!

        handleIntent(UserIntent.IsLoadingChanged(true))

        if (event.remainingSlots < 1) {
            handleIntent(UserIntent.ActionTitleChanged("Registration Error"))
            handleIntent(UserIntent.ActionErrorChanged("Event is already full"))
            handleIntent(UserIntent.IsLoadingChanged(false))
            return
        }

        viewModelScope.launch {
            val result = eventParticipantRepository.registerEvent(event.id, user.id)
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> actionError = result.message
            }

            _state.update {
                it.copy(actionError = actionError)
            }

            Log.d("REGISTER_EVENT", actionError)

            if (actionError.isBlank()) {
                _state.update { currentState ->
                    val updatedCurrentEvent = currentState.currentEvent?.let { event ->
                        event.copy(remainingSlots = (event.remainingSlots - 1).coerceAtLeast(0))
                    }

                    val updatedJoinedEvents = currentState.currentEvent?.let { event ->
                        if (currentState.joinedEvents.none { it.id == event.id }) {
                            currentState.joinedEvents + event
                        } else {
                            currentState.joinedEvents
                        }
                    } ?: currentState.joinedEvents

                    currentState.copy(
                        currentEvent = updatedCurrentEvent, joinedEvents = updatedJoinedEvents
                    )
                }

                handleIntent(UserIntent.ActionTitleChanged("Registration Successful"))
                handleIntent(UserIntent.ActionErrorChanged("View your QR code in the E-Tickets tab."))
                handleIntent(UserIntent.IsLoadingChanged(false))

                handleIntent(UserIntent.ActionOnConfirmClicked {
                    handleIntent(UserIntent.FetchJoinableEvents)
                    handleIntent(UserIntent.FetchJoinedEvents)
                    handleIntent(UserIntent.ActionOnClear)
                })
            } else {
                handleIntent(UserIntent.ActionTitleChanged("Registration failed"))
                handleIntent(UserIntent.ActionErrorChanged("We can't register you at this moment"))
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }

    private fun cancelEvent() {
        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentEvent!!

        handleIntent(UserIntent.IsLoadingChanged(true))

        viewModelScope.launch {
            val result = eventParticipantRepository.cancelEvent(event.id, user.id)
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> actionError = result.message
            }

            _state.update { it.copy(actionError = actionError) }

            if (actionError.isBlank()) {
                _state.update { state ->
                    val updatedCurrentEvent = state.currentEvent?.let { e ->
                        e.copy(remainingSlots = (e.remainingSlots + 1).coerceAtMost(e.capacity))
                    }

                    val updatedJoinedEvents = state.joinedEvents.filter { it.id != event.id }

                    state.copy(
                        currentEvent = updatedCurrentEvent, joinedEvents = updatedJoinedEvents
                    )
                }

                handleIntent(UserIntent.ActionTitleChanged("Registration Cancelled"))
                handleIntent(UserIntent.ActionErrorChanged("You have cancelled the registration for this event"))
                handleIntent(UserIntent.IsLoadingChanged(false))

                handleIntent(UserIntent.ActionOnConfirmClicked {
                    handleIntent(UserIntent.FetchJoinableEvents)
                    handleIntent(UserIntent.FetchJoinedEvents)
                    handleIntent(UserIntent.ActionOnClear)
                })
            } else {
                handleIntent(UserIntent.ActionTitleChanged("Cancellation failed"))
                handleIntent(UserIntent.ActionErrorChanged("We can't cancel your registration at this moment"))
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }


    private fun deleteEvent() {
        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentEvent!!

        if (event.computedStatus != EventComputedStatus.UPCOMING && event.status == EventApprovalStatus.APPROVED) {
            handleIntent(UserIntent.ActionTitleChanged("Error"))
            handleIntent(UserIntent.ActionErrorChanged("You can not delete an event that has already started"))
            handleIntent(UserIntent.IsLoadingChanged(false))
            return
        }

        viewModelScope.launch {
            handleIntent(UserIntent.IsLoadingChanged(true))
            val result =
                eventParticipantRepository.deleteEvent(event.id, user.id, event.backgroundImage)
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> actionError = result.message
            }

            _state.update {
                it.copy(actionError = actionError)
            }

            Log.d("DELETE_EVENT", actionError)

            if (actionError.isBlank()) {
                _state.update { currentState ->
                    val updatedList = currentState.managedEvents.filter { it.event.id != event.id }
                    currentState.copy(managedEvents = updatedList)
                }
                handleIntent(UserIntent.IsLoadingChanged(false))
                handleIntent(UserIntent.ActionTitleChanged("Event Deleted"))
                handleIntent(UserIntent.ActionErrorChanged("You have deleted an event named \"${event.title}\""))
                handleIntent(
                    UserIntent.ActionOnConfirmClicked {
                        Log.d("DELETED EVENT RAN", "THIS RAN")
                        handleIntent(UserIntent.BackClicked)
                        handleIntent(UserIntent.MyEventsSelectedTabChanged(3))
                        handleIntent(UserIntent.ActionOnClear)
                        viewModelScope.launch {
                            delay(300)
                            _state.update { currentState -> currentState.copy(currentEvent = null) }
                        }
                    })
            } else {
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }

    private fun verifyCreateEvent() {
        val currentState = _state.value
        var currentCreateEvent = currentState.currentCreateEvent
        val createEventValidationState = Utils.validateCreateEvent(currentState.currentCreateEvent)

        _state.update {
            currentCreateEvent =
                currentCreateEvent.copy(createEventValidationState = createEventValidationState)
            it.copy(currentCreateEvent = currentCreateEvent)
        }

        if (!createEventValidationState.hasErrors) {
            viewModelScope.launch {
                delay(300)
                handleIntent(UserIntent.IsLoadingChanged(false))
                handleIntent(UserIntent.CreateEventNextStepClicked)
            }
        } else handleIntent(UserIntent.IsLoadingChanged(false))
    }

    private fun createEvent() {
        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentCreateEvent

        viewModelScope.launch(Dispatchers.IO) {
            handleIntent(UserIntent.IsLoadingChanged(true))

            val result = eventParticipantRepository.createEvent(user = user, event = event)

            var createdEvent: ManagedEvent? = null
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {
                    createdEvent = result.data
                }

                is ApiResult.Error -> {
                    actionError = result.message
                }
            }

            _state.update {
                val newManagedEntry = createdEvent?.let { it ->
                    ManagedEvent(
                        event = it.event,
                        userParticipantType = ParticipantType.ORGANIZER,
                        participantList = it.participantList,
                        staffList = it.staffList
                    )
                }

                it.copy(
                    currentEvent = createdEvent?.event,
                    managedEvents = currentState.managedEvents + listOfNotNull(newManagedEntry),
                    actionError = actionError,
                )
            }

            if (actionError.isBlank()) {
                handleIntent(UserIntent.ActionTitleChanged("Event Created"))
                handleIntent(UserIntent.ActionErrorChanged("Please wait for the moderator's approval"))
                handleIntent(UserIntent.ActionOnConfirmClicked({
                    handleIntent(UserIntent.MyEventsSelectedTabChanged(0))
                    _state.update { it.copy(currentCreateEvent = if (actionError.isBlank()) CreateEvent() else it.currentCreateEvent) }
                }))
            }
            handleIntent(UserIntent.IsLoadingChanged(false))
        }
    }

    private fun updateEvent() {
        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentCreateEvent

        viewModelScope.launch(Dispatchers.IO) {
            handleIntent(UserIntent.IsLoadingChanged(true))

            val result = eventParticipantRepository.updateEvent(user = user, event = event)

            when (result) {
                is ApiResult.Success -> {
                    val updatedEvent = result.data

                    _state.update { state ->
                        val updatedList = state.managedEvents.map {
                            if (it.event.id == updatedEvent.event.id) updatedEvent else it
                        }

                        state.copy(
                            currentEvent = updatedEvent.event,
                            managedEvents = updatedList,
                            actionError = ""
                        )
                    }

                    handleIntent(UserIntent.ActionTitleChanged("Event Updated"))
                    handleIntent(UserIntent.ActionErrorChanged("Changes saved. Your event has been resubmitted for moderator approval."))
                    handleIntent(UserIntent.ActionOnConfirmClicked {
                        handleIntent(UserIntent.MyEventsSelectedTabChanged(0))
                        handleIntent(UserIntent.CurrentCreateEventChanged(CreateEvent()))
                    })
                }

                is ApiResult.Error -> {
                    _state.update { it.copy(actionError = result.message) }
                }
            }
            handleIntent(UserIntent.IsLoadingChanged(false))
        }
    }

    private fun updateUserProfile(
        fName: String,
        lName: String,
        dName: String,
        avatarUri: String?,
    ) {
        val currentUser = _state.value.currentUser ?: return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            if (dName != currentUser.displayName) {
                val usernameResult = authRepository.checkIfDisplayNameExists(dName)

                when (usernameResult) {
                    is ApiResult.Success -> {
                        if (usernameResult.data) {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    actionError = "Username '@$dName' is already taken."
                                )
                            }
                            return@launch
                        }
                    }

                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                actionError = "Error checking username: ${usernameResult.message}"
                            )
                        }
                        return@launch
                    }
                }
            }

            var finalAvatarUrl = currentUser.avatarUrl

            if (avatarUri != null) {
                val uploadResult = userRepository.uploadAvatar(currentUser.id.toString(), avatarUri)

                when (uploadResult) {
                    is ApiResult.Success -> {
                        finalAvatarUrl = "${uploadResult.data}?t=${System.currentTimeMillis()}"
                    }

                    is ApiResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                actionError = uploadResult.message
                            )
                        }
                        return@launch
                    }
                }
            }

            val updates = buildJsonObject {
                put("first_name", fName)
                put("last_name", lName)
                put("display_name", dName)
                put("avatar_url", finalAvatarUrl)
            }

            val result = userRepository.updateUserProfile(currentUser.id.toString(), updates)

            when (result) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(
                            currentUser = result.data,
                            isLoading = false,
                            actionTitle = "Success",
                            actionError = "Profile updated successfully"
                        )
                    }
                    onFetchSuccess()
                }

                is ApiResult.Error -> {
                    _state.update { it.copy(isLoading = false, actionError = result.message) }
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            val logoutResult = authRepository.signOut()
            var actionError = ""

            when (logoutResult) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> {
                    actionError = logoutResult.message
                }
            }

            _state.update {
                it.copy(actionError = actionError)
            }

            if (actionError.isBlank()) {
                sendMainEffect(MainEffect.NavigateLogout)
                delay(200)
                resetState()
            }
            handleIntent(UserIntent.IsLoadingChanged(false))
        }
    }

    private fun resetState() {
        pollingJob?.cancel()
        attendanceRealtimeJob?.cancel()
        globalRealtimeJob?.cancel()

        pollingJob = null
        attendanceRealtimeJob = null

        viewModelScope.launch {
            cacheManager.clearCache()
        }

        _state.value = UserState()
    }

    suspend fun getCurrentUser(): User? {
        val result = userRepository.getCurrentUserProfile()

        _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(currentUser = FetchStatus.LOADING)) }

        when (result) {
            is ApiResult.Success -> {
                _state.update {
                    it.copy(
                        currentUser = result.data,
                        dataFetchStatus = it.dataFetchStatus.copy(currentUser = FetchStatus.DONE)
                    )
                }
                onFetchSuccess()
            }

            is ApiResult.Error -> Log.d("MAIN", result.message)
        }
        Log.d("USER", _state.value.currentUser.toString())
        return _state.value.currentUser
    }

    private fun sendUserEffect(effect: UserEffect) {
        viewModelScope.launch {
            _userEffect.emit(effect)
        }
    }

    fun sendMainEffect(effect: MainEffect) {
        viewModelScope.launch {
            _mainEffect.emit(effect)
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
        attendanceRealtimeJob?.cancel()
        globalRealtimeJob?.cancel()
    }
}
