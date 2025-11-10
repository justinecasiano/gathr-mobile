package com.example.gathr.presentation.auth.login

sealed interface LoginIntent{
    data class EmailAddressChanged(val value: String) : LoginIntent
    data class NewPasswordChanged(val value: String) : LoginIntent
    data class ConfirmNewPasswordChanged(val value: String) : LoginIntent
    data object BackClicked : LoginIntent
    data object NextClicked : LoginIntent
    data object SubmitClicked : LoginIntent
}

sealed interface LoginEffect {
    data object NavigateToBack: LoginEffect
    data object NavigateToNext: LoginEffect
}