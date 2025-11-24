package com.example.gathr.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.data.repository.UserRepository
import com.example.gathr.presentation.auth.login.LoginEffect
import com.example.gathr.presentation.auth.login.LoginIntent
import com.example.gathr.utils.Utils
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserViewModel(
    private val authRepository: AuthRepository,
    private val auth: Auth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserState())
    val state: StateFlow<UserState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<UserEffect>()
    val effect: SharedFlow<UserEffect> = _effect.asSharedFlow()

    fun handleIntent(intent: UserIntent) {
        when (intent) {
            is UserIntent.EmailChanged -> {}
            is UserIntent.ActionErrorChanged -> _state.update { it.copy(actionError = intent.value) }
            is UserIntent.IsLoadingChanged -> _state.update { it.copy(isLoading = intent.value) }
            is UserIntent.BackClicked -> {}
            is UserIntent.LogoutClicked -> logout()
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
