package com.example.gathr.presentation.auth.login

import com.example.gathr.presentation.auth.sign_up.SignUpIntent

sealed interface LoginIntent {
    data class EmailChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data class EmailOrPasswordErrorChanged(val value: String) : LoginIntent
    data class IsLoadingChanged(val value: Boolean) : LoginIntent

    data object BackClicked : LoginIntent
    data object ForgotPasswordClicked : LoginIntent
    data object LoginClicked : LoginIntent
}

sealed interface LoginEffect {
    data object NavigateBack : LoginEffect
    data object NavigateToForgotPassword : LoginEffect
    data object NavigateToNext : LoginEffect
}