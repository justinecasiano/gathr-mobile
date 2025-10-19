package com.example.gathr.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gathr.presentation.SplashScreen

object AppRoutes {
    // General Screens
    const val SPLASH_SCREEN = "splash_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val EVENTS_SCREEN = "events_screen"

    // Participant Screens
    const val MY_EVENTS_SCREEN= "my_events_screen"
    const val ETICKETS_SCREEN = "etickets_screen"
    const val NOTIFICATIONS_SCREEN = "notifications_screen"
    const val PROFILE_SCREEN = "profile_screen"

    // Moderator Screens
    const val PENDINGS_SCREEN = "pendings_screen"
}

@Composable
fun AppNavigation() {
   val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH_SCREEN,
    ) {
        composable(AppRoutes.SPLASH_SCREEN) {
            SplashScreen()
        }
    }
}