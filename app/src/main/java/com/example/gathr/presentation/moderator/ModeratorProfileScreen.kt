package com.example.gathr.presentation.moderator

import android.preference.CheckBoxPreference
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import coil3.request.placeholder
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.ImageViewer
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import com.example.gathr.utils.toTitleCase

@Composable
fun ModeratorProfileScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
    onNext: () -> Unit
) {
    val user = state.currentUser
    val userName = "${user?.firstName} ${user?.lastName}"
    val initial = userName.firstOrNull()?.uppercase() ?: "?"
    val school = user?.school?.toTitleCase() ?: "?"
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF312245))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = rethinkSans,
            )
            Spacer(modifier = Modifier.height(10.dp))
            ProfileCard(
                name = userName,
                displayName = user?.displayName,
                initial = initial,
                school = school,
                avatarUrl = user?.avatarUrl,
                onImageClick = { selectedImage = it },
                onClick = { onNavigate(MainEffect.NavigateEditProfile) })
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { showLogoutDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Logout", color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

        }

        if (selectedImage.isNotBlank())
            ImageViewer(selectedImage, onDismiss = { selectedImage = "" })

        if (showLogoutDialog) {
            Alert(
                title = "Logout",
                message = "Are you sure you want to logout?",
                onDismissRequest = { showLogoutDialog = false },
                confirmButtonText = "Confirm",
                onConfirmClicked = {
                    showLogoutDialog = false
                    onIntent(UserIntent.IsLoadingChanged(true))
                    onIntent(UserIntent.LogoutClicked)
                },
                cancelButtonText = "Cancel",
                onCancelClicked = { showLogoutDialog = false },
                isModerator= true
            )
        }
        if (state.actionError.isNotBlank()) {
            Alert(
                title = "Error",
                message = state.actionError,
                onDismissRequest = { onIntent(UserIntent.ActionErrorChanged("")) },
                confirmButtonText = "Ok",
                onConfirmClicked = { onIntent(UserIntent.ActionErrorChanged("")) },
                isModerator= true
            )
        }
    }
}

@Composable
private fun ProfileCard(
    name: String,
    displayName: String?,
    avatarUrl: String?,
    onImageClick: (String) -> Unit,
    initial: String,
    school: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF7954AB),
                        Color(0xFF312245),
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .border(2.dp, Color(0xFF312245))
            .padding(vertical = 20.dp)
            .clip(RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.Black,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Moderator",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = rethinkSans,
                fontWeight = FontWeight.Normal
            )
        }
    }
}
