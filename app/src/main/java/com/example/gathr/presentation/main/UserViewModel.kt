package com.example.gathr.presentation.main

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.User
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.data.repository.EventParticipantRepository
import com.example.gathr.data.repository.UserRepository
import com.example.gathr.presentation.auth.sign_up.SignUpEffect
import com.example.gathr.presentation.auth.sign_up.SignUpIntent
import com.example.gathr.utils.Utils
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import io.github.jan.supabase.storage.upload
import java.util.UUID

class UserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val eventParticipantRepository: EventParticipantRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<UserEffect>()
    val effect: SharedFlow<UserEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.CurrentEventChanged -> _state.update { it.copy(currentEvent = intent.value) }
            is UserIntent.CurrentParticipantChanged -> _state.update { it.copy(currentParticipant = intent.value) }
            is UserIntent.CreateEventChanged -> _state.update { it.copy(createEvent = intent.value) }
            is UserIntent.CreateEventImageFileChanged -> _state.update {
                it.copy(
                    createEventImageFile = intent.value
                )
            }

            is UserIntent.SearchStaffChanged -> {
                _state.update {
                    it.copy(searchStaff = intent.value)
                }
            }

            is UserIntent.SearchStaffErrorChanged -> _state.update { it.copy(searchStaffError = intent.value) }
            is UserIntent.AddStaffsChanged -> _state.update { it.copy(addStaffs = intent.value) }
            is UserIntent.ActionTitleChanged -> _state.update { it.copy(actionTitle = intent.value) }
            is UserIntent.ActionErrorChanged -> _state.update { it.copy(actionError = intent.value) }
            is UserIntent.ActionOnConfirmClicked -> _state.update { it.copy(actionOnConfirm = intent.value) }
            is UserIntent.IsLoadingChanged -> _state.update { it.copy(isLoading = intent.value) }
            is UserIntent.IsUpdateEventChanged -> _state.update { it.copy(isUpdateEvent = intent.value) }

            is UserIntent.FetchEvents -> fetchEvents()
            is UserIntent.SaveModifiedEvent -> saveModifiedEvent()
            is UserIntent.RegisterEvent -> registerEvent()
            is UserIntent.DeleteEvent -> deleteEvent()
            is UserIntent.ViewEvent -> sendEffect(UserEffect.NavigateToNext)
            is UserIntent.DoCreateEvent -> createEvent()
            is UserIntent.ValidateAddStaff -> validateAddStaff()
            is UserIntent.ValidateCreateEvent -> validateCreateEvent()
            is UserIntent.BackClicked -> sendEffect(UserEffect.NavigateBack)
            is UserIntent.LogoutClicked -> logout()
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

            _state.update { it.copy(currentMyEvents = currentMyEvents) }

            Log.d("FETCH_EVENTS", currentMyEvents.toString())
            Log.d("FETCH_EVENTS_ERROR", actionError)
        }
    }

    private fun saveModifiedEvent() {
        TODO("Not yet implemented")
    }

    private fun registerEvent() {
        TODO("Not yet implemented")
    }

    private fun deleteEvent() {
        TODO("Not yet implemented")
    }

    private fun createEvent() {
        val currentState = _state.value
        val user = currentState.currentUser
        val event = currentState.createEvent
        val staffs = currentState.addStaffs
        val imageFile = currentState.createEventImageFile

        viewModelScope.launch(Dispatchers.IO) {
            val result = eventParticipantRepository.createEvent(user!!, event, staffs, imageFile)
            var currentEvent: Event? = null
            var actionError = ""

            when (result) {
                is ApiResult.Success -> currentEvent = result.data
                is ApiResult.Error -> actionError = result.message
            }

            _state.update { it.copy(currentEvent = currentEvent, actionError = actionError) }

            Log.d("CREATE_EVENT", "CURRENT EVENT: ${_state.value.currentEvent}")

            if (actionError.isBlank()) {
                handleIntent(UserIntent.ActionTitleChanged("Created Event"))
                handleIntent(UserIntent.ActionErrorChanged("Created an event successfully"))
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
                        searchStaffError = searchStaffError,
                        actionError = actionError
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
                        searchStaffError = searchStaffError,
                        actionError = actionError
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
                sendEffect(UserEffect.NavigateToNext)
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
                sendEffect(UserEffect.NavigateLogout)
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
        Log.d("MAINVIEW", _state.value.currentUser.toString())
    }

    private fun sendEffect(effect: UserEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}
