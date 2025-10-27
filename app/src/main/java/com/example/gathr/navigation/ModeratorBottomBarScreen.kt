package com.example.gathr.navigation

import androidx.compose.runtime.saveable.Saver
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import com.example.gathr.R

val moderatorBottomBarItems = listOf<ModeratorBottomBarScreen>(
    ModeratorBottomBarScreen.Events,
    ModeratorBottomBarScreen.Pendings,
    ModeratorBottomBarScreen.Notifications,
    ModeratorBottomBarScreen.Account,
)

@Serializable
sealed class ModeratorBottomBarScreen(val icon: Int, val title: String) : NavKey {
    @Serializable
    data object Events : ModeratorBottomBarScreen(icon = R.drawable.events_icon, title = "Events")

    @Serializable
    data object Pendings :
        ModeratorBottomBarScreen(icon = R.drawable.pendings_icon, title = "Pendings")

    @Serializable
    data object Notifications :
        ModeratorBottomBarScreen(icon = R.drawable.notifications_icon, title = "Notifications")

    @Serializable
    data object Account :
        ModeratorBottomBarScreen(icon = R.drawable.profile_icon, title = "Account")
}

val ModeratorBottomBarScreenSaver = Saver<ModeratorBottomBarScreen, String>(
    save = { it::class.simpleName ?: "Unknown" },
    restore = {
        when (it) {
            ModeratorBottomBarScreen.Events::class.simpleName -> ModeratorBottomBarScreen.Events
            ModeratorBottomBarScreen.Pendings::class.simpleName -> ModeratorBottomBarScreen.Pendings
            ModeratorBottomBarScreen.Notifications::class.simpleName -> ModeratorBottomBarScreen.Notifications
            ModeratorBottomBarScreen.Account::class.simpleName -> ModeratorBottomBarScreen.Account
            else -> ModeratorBottomBarScreen.Pendings
        }
    }
)
