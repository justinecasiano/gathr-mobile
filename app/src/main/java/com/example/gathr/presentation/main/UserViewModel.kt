package com.example.gathr.presentation.main

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.User
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.data.repository.EventParticipantRepository
import com.example.gathr.data.repository.UserRepository
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
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val eventParticipantRepository: EventParticipantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val _userEffect = MutableSharedFlow<UserEffect>()
    val userEffect: SharedFlow<UserEffect> = _userEffect.asSharedFlow()

    private val _mainEffect = MutableSharedFlow<MainEffect>()
    val mainEffect: SharedFlow<MainEffect> = _mainEffect.asSharedFlow()

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


            is UserIntent.FetchAttendance -> fetchAttendance()
            is UserIntent.FetchEvents -> fetchEvents()

            is UserIntent.CancelEvent -> cancelEvent()
            is UserIntent.RegisterEvent -> registerEvent()

            is UserIntent.UpdateEvent -> updateEvent()
            is UserIntent.DeleteEvent -> deleteEvent()
            is UserIntent.DoCreateEvent -> createEvent()
            is UserIntent.ValidateAddStaff -> validateAddStaff()
            is UserIntent.ValidateCreateEvent -> validateCreateEvent()

            is UserIntent.LogoutClicked -> logout()
            is UserIntent.BackClicked -> sendUserEffect(UserEffect.NavigateBack)
        }
    }

    private fun fetchAttendance() {
        val currentState = _state.value
        val currentEvent = currentState.currentEvent!!

        viewModelScope.launch {
            val result = eventParticipantRepository.fetchAttendance(currentEvent.id)
            var currentParticipants: List<Participant> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> currentParticipants = result.data
                is ApiResult.Error -> actionError = result.message
            }

            _state.update { it.copy(currentParticipants = currentParticipants) }

            Log.d("FETCH_ATTENDANCE", currentParticipants.toString())
            Log.d("FETCH_ATTENDANCE_ERROR", actionError)
        }
    }

    private fun fetchEvents() {
        viewModelScope.launch {
            val result = eventParticipantRepository.fetchEvents()
            var currentMyEvents: List<Event> = emptyList()
            var actionError = ""

            when (result) {
                is ApiResult.Success -> currentMyEvents = result.data
                is ApiResult.Error -> actionError = result.message
            }

            _state.update { it.copy(currentEvents = currentMyEvents) }

            Log.d("FETCH_EVENTS", currentMyEvents.toString())
            Log.d("FETCH_EVENTS_ERROR", actionError)
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
                        handleIntent(UserIntent.FetchEvents)
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
                        handleIntent(UserIntent.FetchEvents)
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
                handleIntent(UserIntent.FetchEvents)
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
        val currentState = _state.value
        val user = currentState.currentUser
        val event = currentState.createEvent
        val staffs = currentState.addStaffs
        val imageFile = currentState.createEventImageFile

        viewModelScope.launch(Dispatchers.IO) {
            val result =
                eventParticipantRepository.createEvent(user!!, event, staffs, imageFile)
            var currentEvent: Event? = null
            var actionError = ""

            when (result) {
                is ApiResult.Success -> currentEvent = result.data
                is ApiResult.Error -> actionError = result.message
            }

            _state.update {
                it.copy(
                    currentEvent = currentEvent?.copy(isOrganizer = true),
                    actionError = actionError
                )
            }

            Log.d("CREATE_EVENT", "CURRENT EVENT: ${_state.value.currentEvent}")

            if (actionError.isBlank()) {
                handleIntent(UserIntent.ActionTitleChanged("Event Created"))
                handleIntent(UserIntent.ActionErrorChanged("Please wait for the moderator's approval"))
            }
            handleIntent(UserIntent.IsLoadingChanged(false))
        }
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

    suspend fun getCurrentUser() {
        val result = userRepository.getCurrentUserProfile()

        when (result) {
            is ApiResult.Success -> _state.update { it.copy(currentUser = result.data) }
            is ApiResult.Error -> Log.d("MAIN", result.message)
        }
        Log.d("USER", _state.value.currentUser.toString())
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
