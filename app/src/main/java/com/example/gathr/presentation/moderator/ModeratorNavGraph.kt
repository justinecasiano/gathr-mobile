package com.example.gathr.presentation.moderator

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.gathr.presentation.notifications.NotificationsScreen


import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

import com.example.gathr.presentation.moderator.PendingsScreen
import com.example.gathr.presentation.moderator.EventApprovalScreen
import com.example.gathr.presentation.moderator.EventData

@Composable
fun ModeratorNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "pendingEvents"
    ) {

        // ▶️ Pending Events
        composable("pendingEvents") {
            PendingsScreen(
                events = EventDataSource.pendingEvents,
                onEventClick = { event ->
                    val eventJson = Json.encodeToString(event)
                    val encoded = java.net.URLEncoder.encode(eventJson, "UTF-8")
                    navController.navigate("eventApproval/$encoded")
                },
                onNotificationsClick = { navController.navigate("notifications") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        // ▶️ Event Approval
        composable(
            route = "eventApproval/{eventJson}",
            arguments = listOf(navArgument("eventJson") { type = NavType.StringType })
        ) { backStackEntry ->

            val encoded = backStackEntry.arguments?.getString("eventJson") ?: ""
            val decoded = java.net.URLDecoder.decode(encoded, "UTF-8")
            val event = Json.decodeFromString<EventData>(decoded)

            EventApprovalScreen(
                event = event,
                onRemove = {},
                onReject = {},
                onApprove = {},
                onBack = { navController.popBackStack() },
                onSeeRegistered = { selectedEvent ->
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedEvent", selectedEvent)

                    navController.navigate("registered_screen")
                }
            )
        }

        // ▶️ Registered participants
        composable("registered_screen") {
            val event = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<EventData>("selectedEvent")

            if (event != null) {
                RegisteredScreen(
                    event = event,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // ▶️ Notifications
        composable("notifications") {
            NotificationsScreen(
                events = EventDataSource.pendingEvents,
                onNotificationsClick = { navController.navigate("notifications") },
                onPendingsClick = { navController.navigate("pendingEvents") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        // ▶️ Profile Screen (NEW)
        composable("profile") {
            ProfileScreen(
                onNotificationsClick = { navController.navigate("notifications") },
                onPendingsClick = { navController.navigate("pendingEvents") },
                onProfileClick = { navController.navigate("profile") }
            )
        }
    }
}
