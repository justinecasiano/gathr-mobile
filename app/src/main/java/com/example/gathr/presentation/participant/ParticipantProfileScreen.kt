package com.example.gathr.presentation.participant

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ParticipantProfileScreen(
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
            .background(Color.White)
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
                color = Color.Black,
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
                onClick = {})
            Spacer(modifier = Modifier.height(10.dp))
            ProfileBanner(
                onClick = onNext,
                title = "Become an event organizer",
                body = "Begin hosting events for your college",
                image = R.drawable.organizer_pic
            )
            Spacer(Modifier.height(20.dp))
            ProfileBanner(
                onClick = { onNavigate(MainEffect.NavigateStaff) },
                title = "Staffed events",
                body = "View all events you've been assigned to here",
                image = R.drawable.staffed_events_pic
            )
            Spacer(modifier = Modifier.height(20.dp))
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
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Logout", color = Color.Black, fontSize = 16.sp)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.right_arrow),
                    contentDescription = "Logout",
                    tint = Color.Black,
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
                onCancelClicked = { showLogoutDialog = false })
        }
        if (state.actionError.isNotBlank()) {
            Alert(
                title = "Error",
                message = state.actionError,
                onDismissRequest = { onIntent(UserIntent.ActionErrorChanged("")) },
                confirmButtonText = "Ok",
                onConfirmClicked = { onIntent(UserIntent.ActionErrorChanged("")) },
            )
        }
    }
}

@Composable
fun ProfileCard(
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
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .padding(start = 150.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF616162).copy(0.2f))
                    .size(width = 55.dp, height = 28.dp)
                    .clickable { onClick }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Edit",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = rethinkSans
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (avatarUrl != null)
                AsyncImage(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(color = Color.Black, shape = CircleShape, width = 2.dp)
                        .clickable {
                            onImageClick(avatarUrl)
                        },
                    model = ImageRequest.Builder(LocalContext.current).data(avatarUrl)
                        .placeholder(R.drawable.profile)
                        .fallback(R.drawable.profile).crossfade(true)
                        .listener(onStart = { request ->
                            Log.d(
                                "IMAGE_LOAD", "Image started loading"
                            )
                        }, onError = { request, result ->
                            Log.e(
                                "IMAGE_LOAD", "FAILED: ${result.throwable.message}"
                            )
                        }).build(),
                    contentDescription = "User Avatar Photo",
                    contentScale = ContentScale.Crop,
                    error = painterResource(R.drawable.profile)
                )
            else
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF473163)), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

            Spacer(modifier = Modifier.height(10.dp))
            if (displayName != null)
                Text(
                    text = "@$displayName",
                    color = Color(0xFF583181),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

            Text(
                text = name,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = school,
                color = Color.Black,
                fontSize = 12.sp,
                fontFamily = rethinkSans,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileBanner(
    onClick: () -> Unit, title: String, body: String, @DrawableRes image: Int
) {
    Card(
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(20.dp), shadow = Shadow(
                    radius = 50.dp,
                    spread = 0.dp,
                    color = Color(0xFF000000).copy(alpha = 0.1f),
                    offset = DpOffset(x = 4.dp, (4).dp)
                )
            )
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFFFCFCFC))
        ) {
            Box(
                Modifier
                    .padding(15.dp)
                    .align(Alignment.TopEnd)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF9053C9), Color(0xFF473163))
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable { onClick() },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Text(
                        text = "Explore",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = rethinkSans,
                    )
                }
            }
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.height(120.dp),
                    painter = painterResource(image),
                    contentDescription = "Organizer",
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontFamily = rethinkSans,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        ) {
                            append("${title}\n")
                        }
                        append(body)
                    },
                    color = Color.Black,
                    fontFamily = rethinkSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(end = 10.dp)
                )
            }
        }
    }
}