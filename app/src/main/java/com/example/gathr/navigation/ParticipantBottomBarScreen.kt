package com.example.gathr.navigation

import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import com.example.gathr.R

val participantBottomBarItems = listOf<ParticipantBottomBarScreen>(
    ParticipantBottomBarScreen.Events,
    ParticipantBottomBarScreen.MyEvents,
    ParticipantBottomBarScreen.ETickets,
    ParticipantBottomBarScreen.Notifications,
    ParticipantBottomBarScreen.Profile,
)

@Serializable
sealed class ParticipantBottomBarScreen(val icon: Int, val title: String) : NavKey {
    @Serializable
    data object Events : ParticipantBottomBarScreen(icon = R.drawable.events_icon, title = "Events")

    @Serializable
    data object MyEvents :
        ParticipantBottomBarScreen(icon = R.drawable.my_events_icon, title = "My Events")

    @Serializable
    data object ETickets :
        ParticipantBottomBarScreen(icon = R.drawable.e_tickets_icon, title = "E-tickets")

    @Serializable
    data object Notifications :
        ParticipantBottomBarScreen(icon = R.drawable.notifications_icon, title = "Notifications")

    @Serializable
    data object Profile :
        ParticipantBottomBarScreen(icon = R.drawable.profile_icon, title = "Profile")
}

val ParticipantBottomBarScreenSaver = Saver<ParticipantBottomBarScreen, String>(
    save = { it::class.simpleName ?: "Unknown" },
    restore = {
        when (it) {
            ParticipantBottomBarScreen.Events::class.simpleName -> ParticipantBottomBarScreen.Events
            ParticipantBottomBarScreen.MyEvents::class.simpleName -> ParticipantBottomBarScreen.MyEvents
            ParticipantBottomBarScreen.ETickets::class.simpleName -> ParticipantBottomBarScreen.ETickets
            ParticipantBottomBarScreen.Notifications::class.simpleName -> ParticipantBottomBarScreen.Notifications
            ParticipantBottomBarScreen.Profile::class.simpleName -> ParticipantBottomBarScreen.Profile
            else -> ParticipantBottomBarScreen.Events
        }
    }
)
