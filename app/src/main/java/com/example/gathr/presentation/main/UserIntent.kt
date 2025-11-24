package com.example.gathr.presentation.main

sealed interface UserIntent {
    data class EmailChanged(val value: String) : UserIntent
    data class ActionErrorChanged(val value: String) : UserIntent
    data class IsLoadingChanged(val value: Boolean) : UserIntent

    data object BackClicked : UserIntent
    data object LogoutClicked : UserIntent
}

sealed interface UserEffect {
    data object NavigateBack : UserEffect
    data object NavigateLogout : UserEffect
}