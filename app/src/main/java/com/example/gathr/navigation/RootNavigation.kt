package com.example.gathr.navigation

import android.util.Log
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.data.model.User
import com.example.gathr.data.repository.AuthRepository
import com.example.gathr.navigation.auth.AuthNavigation
import com.example.gathr.navigation.main.MainNavigation
import com.example.gathr.presentation.SplashScreen
import com.example.gathr.presentation.auth.NoInternetScreen
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.utils.NetworkConnectivityService
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun RootNavigation() {
    val userViewModel: UserViewModel = koinViewModel()
    val startingDestination = RootScreen.Splash
//    val startingDestination = RootScreen.Main
    val backStack = rememberNavBackStack(startingDestination)

    val networkService: NetworkConnectivityService = koinInject()
    val isOnline by networkService.observeNetworkStatus()
        .collectAsStateWithLifecycle(initialValue = true)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<RootScreen.Splash>
            {
                SplashScreen(
                    userViewModel = userViewModel,
                    onLoaded = { isLoggedIn ->
                        backStack.removeLastOrNull()
                        if (isLoggedIn && isOnline) {
//                        if (isLoggedIn) {
                            backStack.add(RootScreen.Main)
                        } else {
                            backStack.add(RootScreen.Auth)
                        }
                    },
                )
            }
            entry<RootScreen.Auth> {
                AuthNavigation(onLogin = {
                    backStack.removeLastOrNull()
                    backStack.add(RootScreen.Splash)
                })
            }
            entry<RootScreen.Main> {
                MainNavigation(
                    userViewModel = userViewModel,
                    onLogout = {
                        backStack.removeLastOrNull()
                        backStack.add(RootScreen.Auth)
                    })
            }
        },
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
    )
}
