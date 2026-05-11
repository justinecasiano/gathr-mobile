package com.example.gathr.navigation.main

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class MainScreen : NavKey {
    @Serializable
    data object User : MainScreen()

    @Serializable
    data object ParticipantViewEvent : MainScreen()

    @Serializable
    data object CreateEvent : MainScreen()

    @Serializable
    data object UpdateEvent: MainScreen()

    @Serializable
    data object ViewAttendance : MainScreen()

    @Serializable
    data object Feedback : MainScreen()

    @Serializable
    data object EditProfile : MainScreen()

    @Serializable
    data object Staff : MainScreen()

    @Serializable
    data object QrCode : MainScreen()

    @Serializable
    data object QrScanner : MainScreen()
}
