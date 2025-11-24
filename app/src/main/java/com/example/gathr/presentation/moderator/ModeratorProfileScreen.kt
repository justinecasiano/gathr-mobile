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
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.presentation.auth.sign_up.SignUpIntent
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState


@Composable
fun ModeratorProfileScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
) {
    val user = state.currentUser
    val userName = "${user?.firstName} ${user?.lastName}"
    val initial = userName.firstOrNull()?.uppercase() ?: "?"
    var showLogoutDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
//            .background(Color(0xFF312245))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .padding(top = 30.dp)
        ) {
//            Spacer(modifier = Modifier.height(screenHeight * 0.065f))
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
            ProfileCard(name = userName, initial = initial)

            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clickable { showLogoutDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Logout", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

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
        }
//        DialogBox(
//            show = showLogoutDialog,
//            title = "Logout",
//            description = "Are you sure you want to logout?",
//            confirmColor = Color(0xFFFC3436),
//            onDismiss = { showLogoutDialog = false },
//            onConfirm = {
//                showLogoutDialog = false
//                onIntent(UserIntent.IsLoadingChanged(true))
//                onIntent(UserIntent.LogoutClicked)
//            }
//        )
        if (showLogoutDialog) {
            Alert(
                title = "Logout",
                message = "Are you sure you want to logout?",
                isContrast = true,
                onDismissRequest = { showLogoutDialog = false },
                backgroundBrush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF6A3BA8),
                        Color(0xFF3A245F)
                    ),
                ),
                confirmButtonText = "Confirm",
                onConfirmClicked = {
                    showLogoutDialog = false
                    onIntent(UserIntent.IsLoadingChanged(true))
                    onIntent(UserIntent.LogoutClicked)
                },
                cancelButtonText = "Cancel",
                onCancelClicked = { showLogoutDialog = false }
            )
        }
        if (state.actionError.isNotBlank()) {
            Alert(
                title = "Error",
                message = state.actionError,
                isContrast = true,
                onDismissRequest = { onIntent(UserIntent.ActionErrorChanged("")) },
                backgroundBrush = Brush.verticalGradient(
                    listOf(
                        Color(0xFF6A3BA8),
                        Color(0xFF3A245F)
                    ),
                ),
                confirmButtonText = "Ok",
                onConfirmClicked = { onIntent(UserIntent.ActionErrorChanged("")) },
            )
        }
    }
}

@Composable
fun ProfileCard(name: String, initial: String) {

    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF7954AB), Color(0xFF312245)
        )
    )

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