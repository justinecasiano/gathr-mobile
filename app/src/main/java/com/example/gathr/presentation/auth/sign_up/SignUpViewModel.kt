package com.example.gathr.presentation.auth.sign_up

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.model.User
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.data.repository.UserRepository
import com.example.gathr.utils.Utils
import com.google.firebase.messaging.FirebaseMessaging
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.math.sign

class SignUpViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
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

            is SignUpIntent.EmailCodeChanged -> _state.update { it.copy(emailCode = intent.value) }
            is SignUpIntent.EmailCodeErrorChanged -> _state.update { it.copy(emailCodeError = intent.value) }
            is SignUpIntent.SignUpErrorChanged -> _state.update { it.copy(signUpError = intent.value) }

            is SignUpIntent.IsLoadingChanged -> _state.update { it.copy(isLoading = intent.value) }

            is SignUpIntent.BackClicked -> sendEffect(SignUpEffect.NavigateBack)
            is SignUpIntent.LoginClicked -> sendEffect(SignUpEffect.NavigateToLogin)
            is SignUpIntent.NextOfBasicInfoClicked -> validateBasicInfo()
            is SignUpIntent.NextOfSchoolVerificationClicked -> validateSchoolVerification()

            is SignUpIntent.ResendCode -> resendCode()
            is SignUpIntent.VerifyOtp -> verifyOtp()
            is SignUpIntent.SignUpClicked -> signup()
        }
    }

    private fun resendCode() {
        val currentState = _state.value
        viewModelScope.launch {
            authRepository.resendOtp(OtpType.Email.SIGNUP, currentState.email)
        }
    }

    private fun verifyOtp() {
        val currentState = _state.value

        viewModelScope.launch {
            var emailCodeError = ""
            val result = authRepository.verifyOtp(
                OtpType.Email.SIGNUP,
                currentState.email,
                currentState.emailCode
            )

            when (result) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> emailCodeError = result.message
            }

            _state.update { it.copy(emailCodeError = emailCodeError) }

            if (emailCodeError.isBlank()) {
                var signUpError = ""
                val token = try {
                    FirebaseMessaging.getInstance().token.await()
                } catch (e: Exception) {
                    null
                }

                val updates = buildJsonObject {
                    put("display_name", currentState.username)
                    put("first_name", currentState.firstName)
                    put("last_name", currentState.lastName)
                    put("department", currentState.department)
                    if (currentState.school.isNotBlank()) put("school", currentState.school)
                    put("is_umak", currentState.isUmak)
                    put("is_alumni", currentState.isAlumni)
                    put("fcm_token", token)
                }

                val result = userRepository.updateUserProfile(
                    authRepository.getCurrentUserId()!!,
                    updates = updates
                )

                when (result) {
                    is ApiResult.Success -> Log.d("SIGNUP", result.data.toString())
                    is ApiResult.Error -> {
                        Log.d("SIGNUP", result.message)
                        signUpError = result.message
                    }
                }

                _state.update { it.copy(signUpError = signUpError) }
                if (signUpError.isBlank()) {
                    delay(500)
                    sendEffect(SignUpEffect.NavigateToNext)
                }
            }
            handleIntent(SignUpIntent.IsLoadingChanged(false))
        }
    }

    private fun signup() {
        val currentState = _state.value

        viewModelScope.launch {
            var signUpError = ""
            val result = authRepository.signUp(currentState.email, currentState.password)

            when (result) {
                is ApiResult.Success -> {}
                is ApiResult.Error -> signUpError = result.message
            }

            _state.update { it.copy(signUpError = signUpError) }

            if (signUpError.isBlank()) {
                delay(500)
                sendEffect(SignUpEffect.NavigateToNext)
            }
            handleIntent(SignUpIntent.IsLoadingChanged(false))
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
        var emailError = Utils.validateInput("email", currentState.email)
        var usernameError = Utils.validateInput("username", currentState.username)

        val passwordValidationErrors =
            Utils.validatePassword(currentState.password, currentState.confirmPassword)

        _state.update {
            it.copy(
                emailError = emailError,
                usernameError = usernameError,
                passwordValidation = passwordValidationErrors,
                confirmPasswordError = if (passwordValidationErrors.hasConfirmPasswordError) "Passwords don't match" else ""
            )
        }

        if (!(emailError.isBlank() && usernameError.isBlank()
                    && !passwordValidationErrors.hasValidationErrors && !passwordValidationErrors.hasConfirmPasswordError)
        ) {
            handleIntent(SignUpIntent.IsLoadingChanged(false))
            return
        }

        viewModelScope.launch {
            val emailResult = authRepository.checkIfEmailExists(currentState.email)
            var signUpError = ""

            when (emailResult) {
                is ApiResult.Success -> {
                    emailError = if (emailResult.data) "Email already exists" else ""
                }

                is ApiResult.Error -> {
                    signUpError = emailResult.message
                }
            }

            val usernameResult = authRepository.checkIfDisplayNameExists(currentState.username)

            when (usernameResult) {
                is ApiResult.Success -> {
                    usernameError = if (usernameResult.data) "Username already exists" else ""
                }

                is ApiResult.Error -> {
                    signUpError = usernameResult.message
                }
            }

            _state.update {
                it.copy(
                    emailError = emailError,
                    usernameError = usernameError,
                    signUpError = signUpError
                )
            }

            if (emailError.isBlank() && usernameError.isBlank() && signUpError.isBlank()) {
                delay(500)
                sendEffect(SignUpEffect.NavigateToNext)
            }
            handleIntent(SignUpIntent.IsLoadingChanged(false))
        }
    }

    private fun sendEffect(effect: SignUpEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}