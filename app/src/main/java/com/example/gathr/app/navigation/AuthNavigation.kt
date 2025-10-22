package com.example.gathr.app.navigation

import androidx.compose.animation.scaleIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gathr.presentation.SplashScreen
import com.example.gathr.presentation.auth.AuthScreen
import com.example.gathr.ui.theme.slideInRight
import com.example.gathr.ui.theme.slideOutLeft

object AuthRoutes {
    const val SPLASH_SCREEN = "splash_screen"
    const val AUTH_SCREEN = "auth_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val SIGNUP_SCREEN = "login_screen"
    const val FORGOT_PASS_SCREEN = "forgot_pass_screen"
}

@Composable
fun AuthNavHost() {
    val navController = rememberNavController()

    Scaffold() { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.AUTH_SCREEN,
        ) {
            composable(
                AppRoutes.AUTH_SCREEN,
                enterTransition = { scaleIn() },
            ) {
                SplashScreen(paddingValues) {
                    navController.navigate(AppRoutes.AUTH_SCREEN) {
                        popUpTo(AppRoutes.SPLASH_SCREEN) { inclusive = true }
                    }
                }
            }
            composable(
                AppRoutes.AUTH_SCREEN,
                enterTransition = { slideInRight() },
                popEnterTransition = { slideInRight() },
                popExitTransition = { slideOutLeft() }
            ) {
                AuthScreen(paddingValues) {
                }
            }
        }
    }
}