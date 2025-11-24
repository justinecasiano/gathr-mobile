package com.example.gathr.presentation.participant

import android.preference.CheckBoxPreference
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
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
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import com.example.gathr.utils.toTitleCase

@Composable
fun ParticipantProfileScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
) {
    val user = state.currentUser
    val userName = "${user?.firstName} ${user?.lastName}"
    val initial = userName.firstOrNull()?.uppercase() ?: "?"
    val school = user?.school?.toTitleCase() ?: "?"
    var showLogoutDialog by remember { mutableStateOf(false) }

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
            ProfileCard(name = userName, initial = initial, school = school)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
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
                onCancelClicked = { showLogoutDialog = false }
            )
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
fun ProfileCard(name: String, initial: String, school: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFFCFCFC)),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF473163)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

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

@Composable
fun ProfileBanner(
    onClick: () -> Unit,
    title: String,
    body: String,
    @DrawableRes image: Int
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(20.dp),
                shadow = Shadow(
                    radius = 50.dp,
                    spread = 0.dp,
                    color = Color(0xFF000000).copy(alpha = 0.1f),
                    offset = DpOffset(x = 4.dp, (4).dp)
                )
            )
            .fillMaxWidth()
            .height(150.dp),
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
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
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
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier.fillMaxHeight(),
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
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    Column(Modifier.fillMaxSize()) {
        ProfileBanner(
            onClick = {},
            title = "Become an event organizer",
            body = "Begin hosting events for your college",
            image = R.drawable.organizer_pic
        )
        Spacer(Modifier.height(20.dp))
    }
}
