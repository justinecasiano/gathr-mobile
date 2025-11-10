package com.example.gathr.presentation.auth.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state: StateFlow<ForgotPasswordState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ForgotPasswordEffect>()
    val effect: SharedFlow<ForgotPasswordEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: ForgotPasswordIntent) {
        when (intent) {
            is ForgotPasswordIntent.EmailAddressChanged -> _state.update { it.copy(email = intent.value) }
            is ForgotPasswordIntent.NewPasswordChanged -> _state.update { it.copy(newPassword = intent.value) }
            is ForgotPasswordIntent.ConfirmNewPasswordChanged -> _state.update {
                it.copy(confirmNewPassword = intent.value)
            }

            is ForgotPasswordIntent.BackClicked -> sendEffect(ForgotPasswordEffect.NavigateToBack)
            is ForgotPasswordIntent.NextClicked -> sendEffect(ForgotPasswordEffect.NavigateToNext)
            is ForgotPasswordIntent.SubmitClicked -> {}
        }
    }

    fun checkIfInvalidInput(type: String, value: String): Boolean {
        return when (type) {
            "email" -> {
                value.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()
            }

            else -> false
        }
    }

    private fun sendEffect(effect: ForgotPasswordEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}