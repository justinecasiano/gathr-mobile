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
    data object QrCodeScreen: MainScreen()

    @Serializable
    data object QrScanner: MainScreen()
}
