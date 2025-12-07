package com.example.gathr.navigation.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.participant.QrScannerScreen
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserScreen
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.presentation.moderator.ModeratorViewEventScreen
import com.example.gathr.presentation.participant.AddStaffScreen
import com.example.gathr.presentation.participant.AttendanceScreen
import com.example.gathr.presentation.participant.EditProfileScreen
import com.example.gathr.presentation.participant.ModifyEventScreen
import com.example.gathr.presentation.participant.ParticipantViewEventScreen
import com.example.gathr.presentation.participant.QrCodeScreen
import com.example.gathr.presentation.participant.StaffScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MainNavigation(userViewModel: UserViewModel, onLogout: () -> Unit) {
    val backStack = rememberNavBackStack(MainScreen.User)

    LaunchedEffect(Unit) {
        userViewModel.mainEffect.collect { effect ->
            when (effect) {
                MainEffect.NavigateLogout -> onLogout()
                MainEffect.NavigateBackUser -> backStack.removeLastOrNull()
                MainEffect.NavigateUpdateEvent -> {
                    userViewModel.handleIntent(UserIntent.IsUpdateEventChanged(true))
                    backStack.add(MainScreen.ModifyEvent)
                }

                MainEffect.NavigateCreateEvent -> backStack.add(MainScreen.ModifyEvent)
                MainEffect.NavigateParticipantViewEvent -> backStack.add(MainScreen.ParticipantViewEvent)
                MainEffect.NavigateModeratorViewEvent -> backStack.add(MainScreen.ModeratorViewEvent)
                MainEffect.NavigateEditProfile -> backStack.add(MainScreen.EditProfile)
                MainEffect.NavigateAttendance -> {
                    userViewModel.handleIntent(UserIntent.IsLoadingChanged(true))
                    userViewModel.handleIntent(UserIntent.FetchAttendance)
                    backStack.add(MainScreen.ViewAttendance)
                    userViewModel.handleIntent(UserIntent.IsLoadingChanged(false))
                }

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
                userViewModel.handleIntent(UserIntent.CurrentEventChanged(null))
                UserScreen(
                    viewModel = userViewModel,
                )
            }
            entry<MainScreen.ModifyEvent> {
                ModifyEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                        userViewModel.handleIntent(UserIntent.CreateEventOnClear)
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
                        backStack.add(MainScreen.ParticipantViewEvent)
                        userViewModel.handleIntent(UserIntent.FetchEvents)
                        userViewModel.handleIntent(UserIntent.CreateEventOnClear)
                    },
                )
            }
            entry<MainScreen.ParticipantViewEvent> {
                ParticipantViewEventScreen(
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
            entry<MainScreen.ModeratorViewEvent> {
                ModeratorViewEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                        backStack.add(MainScreen.User)
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