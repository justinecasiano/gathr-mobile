package com.example.gathr.presentation.auth.login

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

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailAddressChanged -> _state.update { it.copy(email = intent.value) }
            is LoginIntent.NewPasswordChanged -> _state.update { it.copy(newPassword = intent.value) }
            is LoginIntent.ConfirmNewPasswordChanged -> _state.update {
                it.copy(confirmNewPassword = intent.value)
            }

            is LoginIntent.BackClicked -> sendEffect(LoginEffect.NavigateToBack)
            is LoginIntent.NextClicked -> sendEffect(LoginEffect.NavigateToNext)
            is LoginIntent.SubmitClicked -> {}
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

    private fun sendEffect(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}