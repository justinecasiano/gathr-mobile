package com.example.gathr.presentation.auth.sign_up

sealed interface SignUpIntent {
    data class FirstNameChanged(val value: String) : SignUpIntent
    data class LastNameChanged(val value: String) : SignUpIntent
    data class EmailChanged(val value: String) : SignUpIntent
    data class UsernameChanged(val value: String) : SignUpIntent
    data class PasswordChanged(val value: String) : SignUpIntent
    data class ConfirmPasswordChanged(val value: String) : SignUpIntent

    data class IsUmakChanged(val value: Boolean) : SignUpIntent
    data class DepartmentChanged(val value: String) : SignUpIntent
    data class SchoolChanged(val value: String) : SignUpIntent
    data class IsAlumniChanged(val value: Boolean) : SignUpIntent

    data class EmailCodeChanged(val value: String) : SignUpIntent
    data class EmailCodeErrorChanged(val value: String) : SignUpIntent
    data class SignUpErrorChanged(val value: String) : SignUpIntent

    data class IsLoadingChanged(val value: Boolean) : SignUpIntent

    data object NextClicked: SignUpIntent
    data object BackClicked : SignUpIntent
    data object LoginClicked : SignUpIntent
    data object NextOfBasicInfoClicked : SignUpIntent
    data object NextOfSchoolVerificationClicked : SignUpIntent
    data object SignUpClicked : SignUpIntent
    data object ResendCode : SignUpIntent
    data object VerifyOtp : SignUpIntent
}

sealed interface SignUpEffect {
    data object NavigateBack : SignUpEffect
    data object NavigateToLogin : SignUpEffect
    data object NavigateToNext : SignUpEffect
}