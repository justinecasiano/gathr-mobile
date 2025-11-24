package com.example.gathr.presentation.main

import com.example.gathr.data.model.CreateEvent

sealed interface UserIntent {
    data class CreateEventChanged(val value: CreateEvent) : UserIntent
    data class ActionErrorChanged(val value: String) : UserIntent
    data class IsLoadingChanged(val value: Boolean) : UserIntent

    data object ValidateCreateEvent: UserIntent
    data object BackClicked : UserIntent
    data object LogoutClicked : UserIntent
}

sealed interface UserEffect {
    data object NavigateToNext: UserEffect
    data object NavigateBack : UserEffect
    data object NavigateLogout : UserEffect
}