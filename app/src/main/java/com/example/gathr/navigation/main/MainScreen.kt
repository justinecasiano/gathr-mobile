package com.example.gathr.navigation.main

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class MainScreen : NavKey {
    @Serializable
    data object Main: MainScreen()

    @Serializable
    data object ViewEvent : MainScreen()

    @Serializable
    data object ModifyEvent: MainScreen()

    @Serializable
    data object AddStaff: MainScreen()

    @Serializable
    data object QrCode: MainScreen()

    @Serializable
    data object QrScanner: MainScreen()
}
