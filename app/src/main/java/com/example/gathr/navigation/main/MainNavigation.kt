package com.example.gathr.navigation.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.data.model.UserRole
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.participant.QrScannerScreen
import com.example.gathr.presentation.main.UserScreen
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.presentation.participant.AttendanceScreen
import com.example.gathr.presentation.participant.CreateEventScreen
import com.example.gathr.presentation.participant.EditProfileScreen
import com.example.gathr.presentation.participant.UpdateEventScreen
import com.example.gathr.presentation.shared.ViewEventScreen
import com.example.gathr.presentation.participant.QrCodeScreen
import com.example.gathr.presentation.participant.StaffScreen

@Composable
fun MainNavigation(userViewModel: UserViewModel, onLogout: () -> Unit) {
    val backStack = rememberNavBackStack(MainScreen.User)

    LaunchedEffect(Unit) {
        userViewModel.mainEffect.collect { effect ->
            when (effect) {
                MainEffect.NavigateLogout -> onLogout()
                MainEffect.NavigateBackUser -> backStack.removeLastOrNull()
                MainEffect.NavigateUpdateEvent -> backStack.add(MainScreen.UpdateEvent)
                MainEffect.NavigateCreateEvent -> backStack.add(MainScreen.CreateEvent)
                MainEffect.ViewEvent -> backStack.add(MainScreen.ParticipantViewEvent)
                MainEffect.NavigateEditProfile -> backStack.add(MainScreen.EditProfile)
                MainEffect.NavigateAttendance -> backStack.add(MainScreen.ViewAttendance)
                MainEffect.NavigateFeedback -> backStack.add(MainScreen.Feedback)
                MainEffect.NavigateQrCode -> backStack.add(MainScreen.QrCode)
                MainEffect.NavigateQrScanner -> backStack.add(MainScreen.QrScanner)
                MainEffect.NavigateStaff -> backStack.add(MainScreen.Staff)
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<MainScreen.User> {
                UserScreen(
                    viewModel = userViewModel,
                )
            }
            entry<MainScreen.CreateEvent> {
                CreateEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {
                        backStack.add(MainScreen.ParticipantViewEvent)
                    },
                )
            }
            entry<MainScreen.UpdateEvent> {
                UpdateEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    onNavigateNext = {},
                )
            }
            entry<MainScreen.ParticipantViewEvent> {
                ViewEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                        backStack.add(MainScreen.User)
                    },
                )
            }
            entry<MainScreen.ViewAttendance> {
                AttendanceScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<MainScreen.QrCode> {
                QrCodeScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<MainScreen.QrScanner> {
                QrScannerScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<MainScreen.Staff> {
                StaffScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                )
            }
            entry<MainScreen.EditProfile> {
                EditProfileScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                )
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