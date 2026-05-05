package com.example.gathr.presentation.main

import LocalCacheManager
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.ManagedEvent
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.User
import com.example.gathr.data.model.UserRole
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.data.repository.EventParticipantRepository
import com.example.gathr.data.repository.UserRepository
import com.example.gathr.utils.NetworkConnectivityService
import com.example.gathr.utils.Utils
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
import java.util.UUID

class UserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val eventParticipantRepository: EventParticipantRepository,
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
                    joinableEvents = cachedState.joinableEvents,
                    joinedEvents = cachedState.joinedEvents,
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
                    joinableEvents = cachedState.joinableEvents,
                    joinedEvents = cachedState.joinedEvents,
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

    fun startPolling() {
        pollingJob?.cancel()
        val currentUser = _state.value.currentUser!!

        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    Log.d("POLL", "CURRENTLY POLLING")
                    if (!authRepository.isLoggedIn()) {
                        Log.d("POLL", "STOPPED POLLING")
                        pollingJob?.cancel()
                    }

                    if (currentUser.role == UserRole.PARTICIPANT) {
                        fetchManagedEvents()
                        fetchJoinableEvents()
                        fetchJoinedEvents()
                    } else {
                        // fetch for moderator
                    }
                    fetchNotifications()
                } catch (e: Exception) {
                }
                delay(5_000L)
            }
        }
    }

    fun handleIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.CurrentEventChanged -> _state.update { it.copy(currentEvent = intent.value) }
            is UserIntent.CurrentParticipantChanged -> _state.update { it.copy(currentParticipant = intent.value) }
            is UserIntent.AddStaffsChanged -> _state.update { it.copy(addStaffs = intent.value) }

            is UserIntent.CreateEventChanged -> _state.update { it.copy(createEvent = intent.value) }
            is UserIntent.CreateEventImageFileChanged -> _state.update {
                it.copy(
                    createEventImageFile = intent.value
                )
            }

            is UserIntent.CreateEventOnClear -> {
                viewModelScope.launch {
                    delay(500)
                    _state.update {
                        it.copy(
                            createEvent = CreateEvent(),
                            isUpdateEvent = false,
                            searchStaff = "",
                            addStaffs = emptyList()
                        )

                    }
                }
            }

            is UserIntent.ScanParticipantChanged -> {
                _state.update { it.copy(scanParticipant = intent.value) }
            }

            is UserIntent.MarkAttendance -> markAttendance()

            is UserIntent.SearchStaffChanged -> {
                _state.update {
                    it.copy(searchStaff = intent.value)
                }
            }

            is UserIntent.SearchStaffErrorChanged -> _state.update { it.copy(searchStaffError = intent.value) }

            is UserIntent.ActionTitleChanged -> _state.update { it.copy(actionTitle = intent.value) }
            is UserIntent.ActionErrorChanged -> _state.update { it.copy(actionError = intent.value) }
            is UserIntent.ActionOnConfirmClicked -> _state.update { it.copy(actionOnConfirm = intent.value) }
            is UserIntent.ActionOnClear -> _state.update {
                it.copy(actionTitle = "", actionError = "", actionOnConfirm = {})
            }

            is UserIntent.IsLoadingChanged -> _state.update { it.copy(isLoading = intent.value) }

            is UserIntent.IsUpdateEventChanged -> _state.update { it.copy(isUpdateEvent = intent.value) }

            is UserIntent.FetchManagedEvents -> fetchManagedEvents()
            is UserIntent.FetchJoinableEvents -> fetchJoinableEvents()
            is UserIntent.FetchJoinedEvents -> fetchJoinedEvents()
            is UserIntent.FetchAvailableStaff -> fetchAvailableStaff()
            is UserIntent.FetchNotifications -> fetchNotifications()
            is UserIntent.FetchAttendance -> fetchAttendance()

            is UserIntent.CancelEvent -> cancelEvent()
            is UserIntent.RegisterEvent -> registerEvent()

            is UserIntent.UpdateEvent -> updateEvent()
            is UserIntent.DeleteEvent -> deleteEvent()
            is UserIntent.DoCreateEvent -> createEvent()
            is UserIntent.ValidateAddStaff -> validateAddStaff()
            is UserIntent.ValidateCreateEvent -> validateCreateEvent()

            is UserIntent.LogoutClicked -> logout()
            is UserIntent.BackClicked -> sendUserEffect(UserEffect.NavigateBack)

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
        }
    }

    private fun fetchManagedEvents() {
        viewModelScope.launch {
            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(managedEvents = FetchStatus.LOADING)) }

            val result = eventParticipantRepository.fetchManagedEvents()
            var managedEvents: List<ManagedEvent> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> managedEvents = result.data
                is ApiResult.Error -> actionError = result.message
            }

            if (managedEvents.isNotEmpty()) {
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
            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(joinableEvents = FetchStatus.LOADING)) }

            val result = eventParticipantRepository.fetchJoinableEvents()
            var joinableEvents: List<Event> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> joinableEvents = result.data
                is ApiResult.Error -> actionError = result.message
            }

            if (joinableEvents.isNotEmpty()) {
                _state.update {
                    it.copy(
                        joinableEvents = joinableEvents,
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
            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(joinedEvents = FetchStatus.LOADING)) }

            val result = eventParticipantRepository.fetchJoinedEvents()
            var joinedEvents: List<Event> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> joinedEvents = result.data
                is ApiResult.Error -> actionError = result.message
            }

            if (joinedEvents.isNotEmpty()) {
                _state.update {
                    it.copy(
                        joinedEvents = joinedEvents,
                        dataFetchStatus = it.dataFetchStatus.copy(joinedEvents = FetchStatus.DONE)
                    )
                }
                onFetchSuccess()
            }

            Log.d("FETCH_JOINED_EVENTS", joinedEvents.toString())
            Log.d("FETCH_JOINED_EVENTS_ERROR", actionError)
        }
    }

    private fun fetchAvailableStaff() {
        val currentState = _state.value
        val currentEvent = currentState.currentEvent!!

        viewModelScope.launch {
            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(availableStaff = FetchStatus.LOADING)) }

            val result = eventParticipantRepository.fetchAvailableStaff(currentEvent.id)
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
    }

    private fun fetchNotifications() {
        _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(notifications = FetchStatus.LOADING)) }
//        viewModelScope.launch {
//            _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(joinedEvents = FetchStatus.LOADING)) }
//
//            val result = eventParticipantRepository.fetchJoinedEvents()
//            var joinedEvents: List<Event> = emptyList()
//            var actionError = ""
//
//            when (result) {
//                is ApiResult.Success -> joinedEvents = result.data
//                is ApiResult.Error -> actionError = result.message
//            }
//
//            if (joinedEvents.isNotEmpty()) {
//                _state.update {
//                    it.copy(
//                        joinedEvents = joinedEvents,
//                        dataFetchStatus = it.dataFetchStatus.copy(joinedEvents = FetchStatus.DONE)
//                    )
//                }
//        onFetchSuccess()
//    }
//
//            Log.d("FETCH_JOINED_EVENTS", joinedEvents.toString())
//            Log.d("FETCH_JOINED_EVENTS_ERROR", actionError)
//        }
        _state.update { it.copy(dataFetchStatus = it.dataFetchStatus.copy(notifications = FetchStatus.DONE)) }
    }

    private fun fetchAttendance() {
        val currentState = _state.value
        val currentEvent = currentState.currentEvent!!

        viewModelScope.launch {
            handleIntent(UserIntent.IsLoadingChanged(true))
            val result = eventParticipantRepository.fetchAttendance(currentEvent.id)
            var currentParticipants: List<Participant> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> currentParticipants = result.data
                is ApiResult.Error -> actionError = result.message
            }

            _state.update { it.copy(currentParticipants = currentParticipants) }
            handleIntent(UserIntent.IsLoadingChanged(true))

            Log.d("FETCH_ATTENDANCE", currentParticipants.toString())
            Log.d("FETCH_ATTENDANCE_ERROR", actionError)
        }
    }

    private fun markAttendance() {
        val currentState = _state.value
        val participant = currentState.scanParticipant!!

        viewModelScope.launch {
            val result = eventParticipantRepository.markAttendance(
                participant.eventId,
                UUID.fromString(participant.userId)
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
                handleIntent(UserIntent.FetchAttendance)
            } else {
                handleIntent(UserIntent.ActionTitleChanged("Mark Attendance Failed"))
                handleIntent(UserIntent.ActionErrorChanged("An unknown error occurred"))
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }

    private fun cancelEvent() {

        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentEvent!!

        viewModelScope.launch {
            val result = eventParticipantRepository.cancelEvent(event.id, user.id)
            var actionError = ""

            when (result) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> actionError = result.message
            }

            _state.update {
                it.copy(actionError = actionError)
            }

            Log.d("CANCEL_EVENT", actionError)

            if (actionError.isBlank()) {
                handleIntent(UserIntent.IsLoadingChanged(false))
                handleIntent(UserIntent.ActionTitleChanged("Registration Cancelled"))
                handleIntent(UserIntent.ActionErrorChanged("You have cancelled the registration for this event"))
                handleIntent(UserIntent.ActionOnConfirmClicked {
                    viewModelScope.launch {
                        handleIntent(UserIntent.IsLoadingChanged(true))
                        handleIntent(UserIntent.FetchJoinableEvents)
                        delay(500)
                        handleIntent(UserIntent.IsLoadingChanged(false))
                        handleIntent(UserIntent.BackClicked)
                        handleIntent(UserIntent.ActionOnClear)
                    }
                })
            } else {
                handleIntent(UserIntent.ActionTitleChanged("Cancellation failed"))
                handleIntent(UserIntent.ActionErrorChanged("We can't cancel your registration at this moment"))
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }

    private fun registerEvent() {
        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentEvent!!

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
                handleIntent(UserIntent.IsLoadingChanged(false))
                handleIntent(UserIntent.ActionTitleChanged("Registration Successful"))
                handleIntent(UserIntent.ActionErrorChanged("View your QR code in the E-Tickets tab."))
                handleIntent(UserIntent.ActionOnConfirmClicked {
                    viewModelScope.launch {
                        handleIntent(UserIntent.IsLoadingChanged(true))
                        handleIntent(UserIntent.FetchJoinableEvents)
                        delay(500)
                        handleIntent(UserIntent.IsLoadingChanged(false))
                        handleIntent(UserIntent.BackClicked)
                        handleIntent(UserIntent.ActionOnClear)
                    }
                })
            } else {
                handleIntent(UserIntent.ActionTitleChanged("Registration failed"))
                handleIntent(UserIntent.ActionErrorChanged("We can't register you at this moment"))
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }

    private fun deleteEvent() {
        val currentState = _state.value
        val user = currentState.currentUser!!
        val event = currentState.currentEvent!!

        if (event.computedStatus != EventComputedStatus.UPCOMING) {
            handleIntent(UserIntent.ActionTitleChanged("Error"))
            handleIntent(UserIntent.ActionErrorChanged("You can't delete an event that is past upcoming"))
            handleIntent(UserIntent.IsLoadingChanged(false))
            return
        }

        viewModelScope.launch {
            val result = eventParticipantRepository.deleteEvent(event.id, user.id)
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
                handleIntent(UserIntent.ActionTitleChanged("Deleted event"))
                handleIntent(UserIntent.ActionErrorChanged("You have deleted an event named ${event.title}"))
                handleIntent(UserIntent.ActionOnClear)
                handleIntent(UserIntent.FetchJoinableEvents)
                delay(750)
                handleIntent(UserIntent.IsLoadingChanged(false))
                handleIntent(UserIntent.BackClicked)
            } else {
                handleIntent(UserIntent.IsLoadingChanged(false))
            }
        }
    }

    private fun updateEvent() {
        handleIntent(UserIntent.IsUpdateEventChanged(false))
        TODO("Not yet implemented")
    }

    private fun createEvent() {
//        val currentState = _state.value
//        val user = currentState.currentUser
//        val event = currentState.createEvent
//        val staffs = currentState.addStaffs
//        val imageFile = currentState.createEventImageFile
//
//        viewModelScope.launch(Dispatchers.IO) {
//            val result =
//                eventParticipantRepository.createEvent(user!!, event, staffs, imageFile)
//            var currentEvent: Event? = null
//            var actionError = ""
//
//            when (result) {
//                is ApiResult.Success -> currentEvent = result.data
//                is ApiResult.Error -> actionError = result.message
//            }
//
//            _state.update {
//                it.copy(
//                    currentEvent = currentEvent?.copy(isOrganizer = true),
//                    actionError = actionError
//                )
//            }
//
//            Log.d("CREATE_EVENT", "CURRENT EVENT: ${_state.value.currentEvent}")
//
//            if (actionError.isBlank()) {
//                handleIntent(UserIntent.ActionTitleChanged("Event Created"))
//                handleIntent(UserIntent.ActionErrorChanged("Please wait for the moderator's approval"))
//            }
//            handleIntent(UserIntent.IsLoadingChanged(false))
//        }
    }

    private fun validateAddStaff() {
        val currentState = _state.value
        val currentUser = _state.value.currentUser

        val searchStaff = _state.value.searchStaff
        var searchStaffError = ""
        var actionError = ""

        if (searchStaff == currentUser?.email || searchStaff == currentUser?.displayName) {
            searchStaffError = "You are already an organizer"
            _state.update { it.copy(searchStaffError = searchStaffError) }
            handleIntent(UserIntent.IsLoadingChanged(false))
            return
        }

        val isDuplicate = currentState.addStaffs.any { staff ->
            staff.email == searchStaff || staff.displayName == searchStaff
        }

        if (isDuplicate) {
            searchStaffError = "Staff is already present from the list"
            _state.update { it.copy(searchStaffError = searchStaffError) }
            handleIntent(UserIntent.IsLoadingChanged(false))
            return
        }

        viewModelScope.launch {
            if (Patterns.EMAIL_ADDRESS.matcher(searchStaff).matches()) {
                val emailResult = authRepository.checkIfEmailExists(searchStaff)

                when (emailResult) {
                    is ApiResult.Success -> {
                        searchStaffError = if (emailResult.data) "" else "User not found"
                    }

                    is ApiResult.Error -> {
                        actionError = emailResult.message
                    }
                }

                _state.update {
                    it.copy(
                        searchStaffError = searchStaffError, actionError = actionError
                    )
                }

                if (searchStaffError.isNotBlank()) {
                    handleIntent(UserIntent.IsLoadingChanged(false))
                    return@launch
                }
            } else {
                val usernameResult = authRepository.checkIfDisplayNameExists(searchStaff)
                when (usernameResult) {
                    is ApiResult.Success -> {
                        searchStaffError = if (usernameResult.data) "" else "User not found"
                    }

                    is ApiResult.Error -> {
                        actionError = usernameResult.message
                    }
                }

                _state.update {
                    it.copy(
                        searchStaffError = searchStaffError, actionError = actionError
                    )
                }
            }
            if (searchStaffError.isNotBlank()) {
                handleIntent(UserIntent.IsLoadingChanged(false))
                return@launch
            } else {
                val result = userRepository.getUserByEmailOrUsername(searchStaff)
                var user: User? = null

                when (result) {
                    is ApiResult.Success -> user = result.data

                    is ApiResult.Error -> {
                        actionError = result.message
                    }
                }

                if (user != null) {
                    val newStaff = CreateStaff(
                        eventId = null, // TODO: set event id
                        userId = user.id,
                        email = user.email,
                        displayName = user.displayName,
                        firstName = user.firstName,
                        lastName = user.lastName
                    )
                    handleIntent(UserIntent.AddStaffsChanged(_state.value.addStaffs + newStaff))
                }

                _state.update {
                    it.copy(
                        actionError = actionError
                    )
                }

                if (actionError.isNotBlank()) {
                    handleIntent(UserIntent.IsLoadingChanged(false))
                    return@launch
                } else {
                    handleIntent(UserIntent.IsLoadingChanged(false))
                    handleIntent(UserIntent.SearchStaffChanged(""))
                }
            }
        }

    }

    private fun validateCreateEvent() {
        val currentState = _state.value
        val createEventValidationState = Utils.validateCreateEvent(currentState.createEvent)

        _state.update {
            it.copy(createEventValidationState = createEventValidationState)
        }

        if (!createEventValidationState.hasErrors) {
            viewModelScope.launch {
                delay(500)
                handleIntent(UserIntent.IsLoadingChanged(false))
                sendUserEffect(UserEffect.NavigateNext)
            }
        } else handleIntent(UserIntent.IsLoadingChanged(false))
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
                delay(500)
                sendMainEffect(MainEffect.NavigateLogout)
            }
            handleIntent(UserIntent.IsLoadingChanged(false))
        }
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
}
