package com.example.gathr.navigation.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
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
import com.example.gathr.presentation.participant.AttendanceScreen
import com.example.gathr.presentation.participant.ModifyEventScreen
import com.example.gathr.presentation.participant.QrCodeScreen

@Composable
fun MainNavigation(userViewModel: UserViewModel, onLogout: () -> Unit) {
    val backStack = rememberNavBackStack(MainScreen.Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<MainScreen.Main> {
                userViewModel.handleIntent(UserIntent.CurrentEventChanged(null))

                MainScreen(
                    viewModel = userViewModel,
                    onLogout = onLogout,
                    onNavigateQrCode = {
                        backStack.add(MainScreen.QrScanner)
                    },
                    onNavigateModifyEvent = {
                        backStack.add(MainScreen.ModifyEvent)
                    }, onNavigateUpdateEvent = {
                        userViewModel.handleIntent(UserIntent.IsUpdateEventChanged(true))
                        backStack.add(MainScreen.ModifyEvent)
                    },
                    onNavigateViewEvent = {
                        backStack.add(MainScreen.ViewEvent)
                    }
                )
            }
            entry<MainScreen.ModifyEvent> {
                ModifyEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                        userViewModel.handleIntent(UserIntent.IsUpdateEventChanged(false))
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
                        userViewModel.handleIntent(UserIntent.FetchEvents)
                        userViewModel.handleIntent(UserIntent.IsUpdateEventChanged(false))
                        userViewModel.handleIntent(UserIntent.SearchStaffChanged(""))
                        userViewModel.handleIntent(UserIntent.CreateEventChanged(CreateEvent()))
                        userViewModel.handleIntent(UserIntent.AddStaffsChanged(emptyList()))
                    },
                )
            }
            entry<MainScreen.ViewEvent> {
                ViewEventScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                        backStack.add(MainScreen.Main)
//                        userViewModel.handleIntent(UserIntent.CurrentEventChanged(null))
                    },
                    onNavigateAttendance = {
                        backStack.add(MainScreen.ViewAttendance)
                    },
                    onNavigateEdit = {
                        userViewModel.handleIntent(UserIntent.IsUpdateEventChanged(true))
                        backStack.add(MainScreen.ModifyEvent)
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
//                        userViewModel.handleIntent(UserIntent.CurrentEventChanged(null))
                    },
                )
            }
            entry<MainScreen.QrScanner> {
                QrScannerScreen(
                    viewModel = userViewModel,
                    onNavigateBack = {
                        backStack.removeLastOrNull()
//                        userViewModel.handleIntent(UserIntent.CurrentEventChanged(null))
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