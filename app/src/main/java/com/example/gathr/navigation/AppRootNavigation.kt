package com.example.gathr.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.presentation.MainScreen
import com.example.gathr.presentation.SplashScreen

@Composable
fun AppRootNavigation() {
    val backStack = rememberNavBackStack(Screen.Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Screen.Splash> {
                SplashScreen(onLoaded = {
                    backStack.removeLastOrNull()
//                    backStack.add(Screen.Auth)
                    backStack.add(Screen.Main)
                })
            }
            entry<Screen.Auth> {
//                AuthScreen()
            }
            entry<Screen.Main> {
//                MainScreen()
                MainScreen(isParticipant = false)
            }
        }
    )
}

