package com.example.gathr.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gathr.presentation.SplashScreen
import com.example.gathr.presentation.login.LoginScreen

private const val TRANSITION_DURATION = 500

val fadeInTransition: EnterTransition = fadeIn(animationSpec = tween(TRANSITION_DURATION))
val fadeOutTransition: ExitTransition = fadeOut(animationSpec = tween(TRANSITION_DURATION))

val slideInRightTransition: EnterTransition =
    slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(TRANSITION_DURATION))
val slideOutLeftTransition: ExitTransition =
    slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(TRANSITION_DURATION))
val slideInLeftTransition: EnterTransition =
    slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(TRANSITION_DURATION))
val slideOutRightTransition: ExitTransition =
    slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(TRANSITION_DURATION))

val slideInUpTransition: EnterTransition = slideInVertically(
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(TRANSITION_DURATION)
)
val slideOutUpTransition: ExitTransition = slideOutVertically(
    targetOffsetY = { fullHeight -> -fullHeight },
    animationSpec = tween(TRANSITION_DURATION)
)
val slideInDownTransition: EnterTransition = slideInVertically(
    initialOffsetY = { fullHeight -> -fullHeight },
    animationSpec = tween(TRANSITION_DURATION)
)
val slideOutDownTransition: ExitTransition = slideOutVertically(
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(TRANSITION_DURATION)
)

val scaleInTransition: EnterTransition =
    scaleIn(animationSpec = tween(TRANSITION_DURATION), initialScale = 0.8f) + fadeInTransition
val scaleOutTransition: ExitTransition =
    scaleOut(animationSpec = tween(TRANSITION_DURATION), targetScale = 0.8f) + fadeOutTransition

object AppRoutes {
    // General Screens
    const val SPLASH_SCREEN = "splash_screen"
    const val LOGIN_SCREEN = "login_screen"
    const val EVENTS_SCREEN = "events_screen"

    // Participant Screens
    const val MY_EVENTS_SCREEN = "my_events_screen"
    const val ETICKETS_SCREEN = "etickets_screen"
    const val NOTIFICATIONS_SCREEN = "notifications_screen"
    const val PROFILE_SCREEN = "profile_screen"

    // Moderator Screens
    const val PENDINGS_SCREEN = "pendings_screen"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold() { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.SPLASH_SCREEN,
        ) {
            composable(
                AppRoutes.SPLASH_SCREEN,
                enterTransition = { scaleInTransition },
            ) {
                SplashScreen(paddingValues) {
                    navController.navigate(AppRoutes.LOGIN_SCREEN) {
                        popUpTo(AppRoutes.SPLASH_SCREEN) { inclusive = true }
                    }
                }
            }
            composable(
                AppRoutes.LOGIN_SCREEN,
                enterTransition = { slideInRightTransition },
                popEnterTransition = { slideInRightTransition },
                popExitTransition = { slideOutLeftTransition }
            ) {

                LoginScreen(paddingValues) {
                    navController.navigate(AppRoutes.EVENTS_SCREEN) {
                        popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true }
                    }
                }
            }
        }
    }
}