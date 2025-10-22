package com.example.gathr.app.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gathr.presentation.auth.AuthScreen
import com.example.gathr.presentation.auth.ForgotPasswordScreen
import com.example.gathr.presentation.auth.LoginScreen
import com.example.gathr.presentation.auth.SignUpScreen
import com.example.gathr.ui.theme.slideInRight
import com.example.gathr.ui.theme.slideOutLeft

object AuthRoutes {
    const val SPLASH_SCREEN = "splash_screen"
    const val AUTH_SCREEN = "auth_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val SIGNUP_SCREEN = "login_screen"
    const val FORGOT_PASSWORD_SCREEN = "forgot_pass_screen"
}

@Composable
fun AuthNavHost(paddingValues: PaddingValues, appNavController: NavHostController) {
    val navController = rememberNavController()

    Scaffold() { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AuthRoutes.AUTH_SCREEN,
        ) {
            composable(
                AuthRoutes.AUTH_SCREEN,
                enterTransition = { slideInRight() },
                popEnterTransition = { slideInRight() },
                popExitTransition = { slideOutLeft() }
            ) {
                AuthScreen(
                    paddingValues,
                    onNavigateSignUp = {
                        navController.navigate(AuthRoutes.SIGNUP_SCREEN) { launchSingleTop = true }
                    }, onNavigateLogin = {
                        navController.navigate(AuthRoutes.LOGIN_SCREEN) { launchSingleTop = true }
                    })
            }
            composable(
                AuthRoutes.SIGNUP_SCREEN,
                enterTransition = { slideInRight() },
                popEnterTransition = { slideInRight() },
                popExitTransition = { slideOutLeft() }
            ) {
                SignUpScreen(
                    paddingValues = paddingValues,
                    onNavigateBack = {
                        navController.navigate(AuthRoutes.AUTH_SCREEN) { launchSingleTop = true }
                    },
                    onNavigateLogin = {
                        navController.navigate(AuthRoutes.LOGIN_SCREEN) { launchSingleTop = true }
                    },
                    onNavigateAfterSignUp = {
                        navController.navigate(AuthRoutes.AUTH_SCREEN) { launchSingleTop = true }
                    },
                )
            }
            composable(
                AuthRoutes.LOGIN_SCREEN,
                enterTransition = { slideInRight() },
                popEnterTransition = { slideInRight() },
                popExitTransition = { slideOutLeft() }
            ) {
                LoginScreen(
                    paddingValues = paddingValues,
                    onNavigateBack = {
                        navController.navigate(AuthRoutes.AUTH_SCREEN) { launchSingleTop = true }
                    },
                    onNavigateForgotPassword = {
                        navController.navigate(AuthRoutes.FORGOT_PASSWORD_SCREEN) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateAfterLogin = {
                        appNavController.navigate(AppRoutes.SPLASH_SCREEN) {
                            popUpTo(appNavController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }
            composable(
                AuthRoutes.FORGOT_PASSWORD_SCREEN,
                enterTransition = { slideInRight() },
                popEnterTransition = { slideInRight() },
                popExitTransition = { slideOutLeft() }
            ) {
                ForgotPasswordScreen(
                    paddingValues = paddingValues,
                    onNavigateBack = {
                        navController.navigate(AuthRoutes.LOGIN_SCREEN) { launchSingleTop = true }
                    },
                )
            }
        }
    }
}