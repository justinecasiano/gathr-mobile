package com.example.gathr.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.gathr.navigation.ModeratorBottomBarScreen
import com.example.gathr.navigation.ModeratorBottomBarScreenSaver
import com.example.gathr.navigation.ParticipantBottomBarScreen
import com.example.gathr.navigation.ParticipantBottomBarScreenSaver
import com.example.gathr.navigation.moderatorBottomBarItems
import com.example.gathr.navigation.participantBottomBarItems
import com.example.gathr.presentation.shared.EventsScreen
import com.example.gathr.presentation.participant.ETicketsScreen
import com.example.gathr.presentation.participant.MyEvents
import com.example.gathr.presentation.shared.NotificationsScreen
import com.example.gathr.presentation.participant.ProfileScreen
import com.example.gathr.ui.theme.AppFonts

@Composable
fun MainScreen(isParticipant: Boolean = true) {
    if (isParticipant) ParticipantScreen()
    else ModeratorScreen()
}

@Composable
fun ParticipantScreen() {
    val backStack = rememberNavBackStack(ParticipantBottomBarScreen.Events)
    var currentBottomBarScreen: ParticipantBottomBarScreen by rememberSaveable(
        stateSaver = ParticipantBottomBarScreenSaver
    ) { mutableStateOf(ParticipantBottomBarScreen.Events) }

    Scaffold(bottomBar = {
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier
                .drawWithContent {
                    drawContent()
                    drawLine(
                        color = Color(0xFFD7D7D7),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                },
        ) {
            participantBottomBarItems.forEach { destination ->
                NavigationBarItem(
                    modifier = Modifier,
                    selected = currentBottomBarScreen == destination,
                    icon = {
                        if (destination.title == "E-tickets" || destination.title == "Notifications") {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = Color(0xFFF36F44),
                                        contentColor = Color.White,
                                        modifier = Modifier
                                            .border(
                                                width = 1.5.dp,
                                                color = Color.Black,
                                                shape = CircleShape
                                            )
                                    )
                                    { Text(text = "0") }
                                },
                            ) {
                                Icon(
                                    modifier = Modifier.size(28.dp),
                                    painter = painterResource(destination.icon),
                                    contentDescription = "$destination icon"
                                )
                            }
                        } else {
                            Icon(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(destination.icon),
                                contentDescription = "$destination icon"
                            )
                        }
                    },
                    label = {
                        Text(
                            destination.title,
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                            ),
                        )
                    },
                    onClick = {
                        if (backStack.lastOrNull() != destination) {
                            if (backStack.lastOrNull() in participantBottomBarItems) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                            backStack.add(destination)
                            currentBottomBarScreen = destination
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color(0xFFF36F44),
                        selectedTextColor = Color(0xFFF36F44),
                        unselectedIconColor = Color(0xFFA5A5A5),
                        unselectedTextColor = Color(0xFFA5A5A5),
                        disabledIconColor = Color.Unspecified,
                        disabledTextColor = Color.Unspecified,
                    )
                )
            }
        }
    }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<ParticipantBottomBarScreen.Events> {
                        EventsScreen()
                    }
                    entry<ParticipantBottomBarScreen.MyEvents> {
                        MyEvents()
                    }
                    entry<ParticipantBottomBarScreen.ETickets> {
                        ETicketsScreen()
                    }
                    entry<ParticipantBottomBarScreen.Notifications> {
                        NotificationsScreen()
                    }
                    entry<ParticipantBottomBarScreen.Profile> {
                        ProfileScreen()
                    }
                }
            )

        }
    }
}

@Composable
fun ModeratorScreen() {
    val backStack = rememberNavBackStack(ModeratorBottomBarScreen.Events)
    var currentBottomBarScreen: ModeratorBottomBarScreen by rememberSaveable(
        stateSaver = ModeratorBottomBarScreenSaver
    ) { mutableStateOf(ModeratorBottomBarScreen.Pendings) }

    val colorStops = listOf(
        0f to Color(0xFF312245),
        1f to Color(0xFF493367),
    )
    val backgroundBrush = Brush.linearGradient(
        colorStops = colorStops.toTypedArray(),
        start = Offset(x = Float.POSITIVE_INFINITY / 2f, y = 0f),
        end = Offset(x = Float.POSITIVE_INFINITY / 2f, y = Float.POSITIVE_INFINITY)
    )

    val modifierWithBorder = Modifier.drawWithContent {
        drawContent()
        drawLine(
            color = Color(0xFF261A36),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx()
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                modifier = modifierWithBorder
                    .background(backgroundBrush)
                    .padding(horizontal = 25.dp),
            )
            {
                moderatorBottomBarItems.forEach { destination ->
                    NavigationBarItem(
                        modifier = Modifier,
                        selected = currentBottomBarScreen == destination,
                        icon = {
                            if (destination.title == "Notifications") {
                                BadgedBox(
                                    badge = {
                                        Badge(
//                                        containerColor = Color(0xFF312245),
                                            containerColor = Color(0xFFF7906E),
                                            contentColor = Color.White,
                                            modifier = Modifier
                                                .border(
                                                    width = 1.5.dp,
                                                    color = Color.Black,
                                                    shape = CircleShape
                                                )
                                        )
                                        { Text(text = "0") }
                                    },
                                ) {
                                    Icon(
                                        modifier = Modifier.size(28.dp),
                                        painter = painterResource(destination.icon),
                                        contentDescription = "$destination icon"
                                    )
                                }
                            } else {
                                Icon(
                                    modifier = Modifier.size(28.dp),
                                    painter = painterResource(destination.icon),
                                    contentDescription = "$destination icon"
                                )
                            }
                        },
                        label = {
                            Text(
                                destination.title,
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                ),
                            )
                        },
                        onClick = {
                            if (backStack.lastOrNull() != destination) {
                                if (backStack.lastOrNull() in participantBottomBarItems) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                                backStack.add(destination)
                                currentBottomBarScreen = destination
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.White.copy(alpha = 0.5f),
                            unselectedTextColor = Color.White.copy(alpha = 0.5f),
                            disabledIconColor = Color.Unspecified,
                            disabledTextColor = Color.Unspecified,
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF312245))
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<ModeratorBottomBarScreen.Events> {
                        EventsScreen()
                    }
                    entry<ModeratorBottomBarScreen.Pendings> {
                        MyEvents()
                    }
                    entry<ModeratorBottomBarScreen.Notifications> {
                        NotificationsScreen()
                    }
                    entry<ModeratorBottomBarScreen.Account> {
                        ProfileScreen()
                    }
                }
            )

        }
    }
}

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}

