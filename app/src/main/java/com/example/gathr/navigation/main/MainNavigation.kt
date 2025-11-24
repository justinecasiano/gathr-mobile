package com.example.gathr.navigation.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.presentation.participant.QrScannerScreen
import com.example.gathr.presentation.main.MainScreen
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.presentation.main.ViewEventScreen
import com.example.gathr.presentation.participant.AddStaffScreen
import com.example.gathr.presentation.participant.ModifyEventScreen
import com.example.gathr.presentation.participant.QrCodeScreen

@Composable
fun MainNavigation(userViewModel: UserViewModel, onLogout: () -> Unit) {
    val backStack = rememberNavBackStack(MainScreen.ModifyEvent)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<MainScreen.Main> {
                MainScreen(viewModel = userViewModel, onLogout = onLogout)
            }
            entry<MainScreen.ModifyEvent> {
                ModifyEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(MainScreen.AddStaff)
                    },
                )
            }
            entry<MainScreen.AddStaff> {
                AddStaffScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.clear()
                        backStack.add(MainScreen.ViewEvent)
                        userViewModel.handleIntent(UserIntent.CreateEventChanged(CreateEvent()))
                    },
                )
            }
            entry<MainScreen.ViewEvent> {
                ViewEventScreen()
            }
            entry<MainScreen.QrCode> {
                QrCodeScreen()
            }
            entry<MainScreen.QrScanner> {
                QrScannerScreen()
            }
        },
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(
                targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
                targetOffsetX = { it })
        },
    )
}