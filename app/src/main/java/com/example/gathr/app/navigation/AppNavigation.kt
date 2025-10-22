package com.example.gathr.app.navigation

import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gathr.presentation.SplashScreen
import com.example.gathr.presentation.auth.AuthScreen
import com.example.gathr.ui.theme.slideInRight
import com.example.gathr.ui.theme.slideOutLeft

object AppRoutes {
    const val SPLASH_SCREEN = "splash_screen"
    const val AUTH_SCREEN = "auth_screen"
    const val MAIN_SCREEN = "main_screen"
}

@Composable
fun AppNavHost(paddingValues: PaddingValues) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.SPLASH_SCREEN,
    ) {
        composable(
            AppRoutes.SPLASH_SCREEN,
            enterTransition = { scaleIn() },
        ) {
            SplashScreen(paddingValues) {
                navController.navigate(AppRoutes.AUTH_SCREEN) {
                    popUpTo(AppRoutes.SPLASH_SCREEN) { inclusive = true }
                }
            }
        }
        composable(AppRoutes.AUTH_SCREEN) {
            AuthNavHost(paddingValues, navController)
        }
    }
}