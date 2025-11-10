package com.example.gathr.presentation.auth.forgot_password

sealed interface ForgotPasswordIntent {
    data class EmailAddressChanged(val value: String) : ForgotPasswordIntent
    data class NewPasswordChanged(val value: String) : ForgotPasswordIntent
    data class ConfirmNewPasswordChanged(val value: String) : ForgotPasswordIntent
    data object BackClicked : ForgotPasswordIntent
    data object NextClicked : ForgotPasswordIntent
    data object SubmitClicked : ForgotPasswordIntent
}

sealed interface ForgotPasswordEffect {
    data object NavigateToBack: ForgotPasswordEffect
    data object NavigateToNext: ForgotPasswordEffect
}