package com.example.gathr.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen : NavKey {
    @Serializable
    data object Splash : Screen()

    @Serializable
    data object Auth : Screen()

    @Serializable
    data object SignUp : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Main : Screen()
}