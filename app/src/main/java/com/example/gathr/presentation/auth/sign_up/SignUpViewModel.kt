package com.example.gathr.presentation.auth.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.sign

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SignUpEffect>()
    val effect: SharedFlow<SignUpEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: SignUpIntent) {
        when (intent) {
            is SignUpIntent.FirstNameChanged -> _state.update { it.copy(firstName = intent.value) }
            is SignUpIntent.LastNameChanged -> _state.update { it.copy(lastName = intent.value) }
            is SignUpIntent.EmailChanged -> _state.update { it.copy(email = intent.value) }
            is SignUpIntent.UsernameChanged -> _state.update { it.copy(username = intent.value) }
            is SignUpIntent.PasswordChanged -> _state.update { it.copy(password = intent.value) }
            is SignUpIntent.ConfirmPasswordChanged -> _state.update { it.copy(confirmPassword = intent.value) }

            is SignUpIntent.IsUmakChanged -> _state.update { it.copy(isUmak = intent.value) }
            is SignUpIntent.DepartmentChanged -> _state.update { it.copy(department = intent.value) }
            is SignUpIntent.SchoolChanged -> _state.update { it.copy(school = intent.value) }
            is SignUpIntent.IsAlumniChanged -> _state.update { it.copy(isAlumni = intent.value) }

            is SignUpIntent.BackClicked -> sendEffect(SignUpEffect.NavigateBack)
            is SignUpIntent.LoginClicked -> sendEffect(SignUpEffect.NavigateToLogin)
            is SignUpIntent.NextOfBasicInfoClicked -> validateBasicInfo()
            is SignUpIntent.NextOfSchoolVerificationClicked -> validateSchoolVerification()
            is SignUpIntent.NextOfTermsOfServiceClicked -> sendEffect(SignUpEffect.NavigateToNext)
            is SignUpIntent.SignUpClicked -> {
                signup()
            }
        }
    }

    private fun signup() {
        val emailCodeError = "12345"
        _state.update {
            it.copy(emailCodeError = emailCodeError)
        }

        if (emailCodeError.isBlank()) {
            viewModelScope.launch {
                delay(300)
                sendEffect(SignUpEffect.NavigateToNext)
            }
        }
    }

    private fun validateSchoolVerification() {
        val currentState = _state.value
        val isUmak = currentState.isUmak
        val department = currentState.department
        val school = currentState.school
        val isAlumni = currentState.isAlumni

        if ((isUmak == true && department.isNotBlank() && isAlumni != null) ||
            (isUmak == false && school.isNotBlank())
        )
            sendEffect(SignUpEffect.NavigateToNext)
    }

    private fun validateBasicInfo() {
        val currentState = _state.value
        val firstNameError = Utils.validateInput("firstname", currentState.firstName)
        val lastNameError = Utils.validateInput("lastname", currentState.lastName)
        val emailError = Utils.validateInput("email", currentState.email)
        val usernameError = Utils.validateInput("username", currentState.username)
        val passwordValidationErrors =
            Utils.validatePassword(currentState.password, currentState.confirmPassword)

        _state.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                emailError = emailError,
                usernameError = usernameError,
                passwordValidation = passwordValidationErrors
            )
        }

        if (firstNameError.isBlank() && lastNameError.isBlank()
            && emailError.isBlank() && usernameError.isBlank()
            && !passwordValidationErrors.hasValidationErrors && !passwordValidationErrors.hasConfirmPasswordError
        ) {
            viewModelScope.launch {
                delay(300)
                sendEffect(SignUpEffect.NavigateToNext)
            }
        }
    }

    private fun sendEffect(effect: SignUpEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}