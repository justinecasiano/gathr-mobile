package com.example.gathr.presentation.moderator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.gathr.R
import com.example.gathr.presentation.moderator.components.DialogBox
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.draw.drawBehind

@Composable
fun ProfileScreen(
    onEventsClick: () -> Unit = {},
    onPendingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    val userName = "Angela Mae Cabrera"
    val initial = userName.firstOrNull()?.uppercase() ?: "?"
    val topGradient = Brush.verticalGradient(colors = listOf(Color(0xFF7954AB), Color(0xFF312245)))
    val bottomGradient = Color(0xFF312245)
    var activeBottomTab by remember { mutableStateOf("Account") }

    // dialog state
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF312245)) // same base background as NotificationsScreen
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding( horizontal = 18.dp)

        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.065f))

            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = rethinkSans,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )


            Spacer(modifier = Modifier.height(25.dp))
            // Profile card and content
            ProfileCard(name = userName, initial = initial)

            Spacer(modifier = Modifier.height(25.dp))

            // LOGOUT ROW (click opens dialog)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showLogoutDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Logout", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_right),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // two thin gradient lines above bottom nav (copied from NotificationsScreen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )

            // BOTTOM NAV (copied exactly from NotificationsScreen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.08f)
                    .background(bottomGradient),
                contentAlignment = Alignment.Center
            ) {
                val navItems = listOf(
                    "Events" to R.drawable.ic_events,
                    "Pendings" to R.drawable.ic_pending,
                    "Notifications" to R.drawable.ic_notifications,
                    "Account" to R.drawable.ic_account
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    navItems.forEach { (label, iconRes) ->
                        val isActive = label == activeBottomTab
                        val tint = if (isActive) Color.White else Color.White.copy(alpha = 0.5f)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                activeBottomTab = label
                                when (label) {
                                    "Events" -> onEventsClick()
                                    "Pendings" -> onPendingsClick()
                                    "Notifications" -> onNotificationsClick()
                                    "Account" -> onProfileClick()
                                }
                            }
                        ) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = label,
                                modifier = Modifier.size((screenWidth.value * 0.07f).dp),
                                colorFilter = ColorFilter.tint(tint)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = label,
                                color = tint,
                                fontFamily = rethinkSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = (screenWidth.value * 0.032f).sp
                            )
                        }
                    }
                }
            }
        }

        // LOGOUT DIALOG (wired)
        DialogBox(
            show = showLogoutDialog,
            title = "Logout",
            description = "Are you sure you want to logout?",
            confirmColor = Color(0xFFFC3436),
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogoutClick()
            }
        )
    }
}

// reuse your existing ProfileCard composable (unchanged)
@Composable
fun ProfileCard(name: String, initial: String) {

    val cardGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7954AB), Color(0xFF312245)
        )
    )

    // 🔥 Drop shadow settings (soft, wide, screenshot style)
    val shadowColor = Color.Black.copy(alpha = 0.35f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(cardGradient),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            // 🔵 WHITE CIRCLE (BACK)
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color(0xFF43305D),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Moderator",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }
    }
}


