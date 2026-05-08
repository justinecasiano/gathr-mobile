package com.example.gathr.presentation.participant

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.Notification
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.shared.LargeEventCard
import com.example.gathr.presentation.shared.SearchNotFound
import com.example.gathr.presentation.shared.SmallEventCard
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import com.example.gathr.utils.dummyEvents
import com.example.gathr.utils.dummyNotifications
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime

@Composable
fun ParticipantNotificationsScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
    onNext: () -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        ParticipantNotificationsContent(
            state, onIntent, onNavigate, onNext
        )

        when {
            state.actionError.isNotBlank() -> {
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
}

@Composable
fun ParticipantNotificationsContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
    onNext: () -> Unit
) {
    val newNotifications =
        state.notifications.filter { it -> !it.isRead }.sortedByDescending { it.createdAt }
    val pastNotifications =
        state.notifications.filter { it -> it.isRead }.sortedByDescending { it.createdAt }
    val eventsCount =
        state.managedEvents.count { it.userParticipantType === ParticipantType.ORGANIZER }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .padding(top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Notifications",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = rethinkSans,
                )
                Spacer(modifier = Modifier.height(15.dp))
                Box(Modifier.fillMaxWidth()) {
                    Image(
                        modifier = Modifier.width(379.dp),
                        painter = painterResource(R.drawable.participant_notifications_banner),
                        contentDescription = "Notifications Banner",
                        contentScale = ContentScale.FillWidth
                    )
                    Column(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .padding(start = 20.dp, top = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "You have $eventsCount\ncreated events",
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Start,
                                color = Color.White
                            ),
                        )
                        Spacer(Modifier.height(5.dp))
                        Row(
                            Modifier
                                .clickable { onNext() }
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF8F8F8))
                                .padding(horizontal = 20.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.calendar_icon),
                                contentDescription = "My events",
                                Modifier.size(25.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "My Events",
                                style = TextStyle(
                                    fontFamily = rethinkSans,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF574272)
                                ),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "New",
                        style = TextStyle(
                            fontFamily = rethinkSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        ),
                    )
                    Spacer(Modifier.weight(1f))
                    Box(
                        Modifier
                            .clickable { onIntent(UserIntent.MarkAllNotificationsAsRead) }
                            .padding(5.dp),
                    ) {
                        Text(
                            "Mark all read",
                            style = TextStyle(
                                fontFamily = rethinkSans,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF36F44)
                            ),
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            items(
                items = newNotifications,
                key = { notification -> notification.id }) { notification ->
                NotificationRow(
                    notification,
                    { onIntent(UserIntent.MarkNotificationAsRead(notification.id)) })
                Spacer(Modifier.height(10.dp))
            }
            item {
                Spacer(Modifier.height(20.dp))
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    Text(
                        "Past",
                        style = TextStyle(
                            fontFamily = rethinkSans,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        ),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
                Spacer(Modifier.height(10.dp))
            }
            items(
                items = pastNotifications,
                key = { notification -> notification.id }) { notification ->
                NotificationRow(notification)
                Spacer(Modifier.height(10.dp))
            }
            item {
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun NotificationRow(notification: Notification, onClick: (() -> Unit)? = null) {
    val msg = notification.message
    val style = when {
        msg.contains("rejected", ignoreCase = true) ||
                msg.contains("not approved", ignoreCase = true) ->
            NotificationStyle(Color(0xFFEE101A), R.drawable.info)

        msg.contains("approved", ignoreCase = true) ||
                msg.contains("re-registered", ignoreCase = true) ||
                msg.contains("successfully registered", ignoreCase = true) ->
            NotificationStyle(Color(0xFF81AC6E), R.drawable.updates_icon)

        msg.contains("absent", ignoreCase = true) ||
                msg.contains("Cancelled", ignoreCase = true) ->
            NotificationStyle(Color(0xFFF36F44), R.drawable.info)

        msg.contains("assigned as Staff", ignoreCase = true) ||
                msg.contains("forget", ignoreCase = true) ||
                msg.contains("Preparation", ignoreCase = true) ||
                msg.contains("slots", ignoreCase = true) ->
            NotificationStyle(Color(0xFF603C8D), R.drawable.info)

        else -> NotificationStyle(Color(0xFF3B3B3B), R.drawable.details_icon)
    }

    Row(
        modifier = Modifier
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (!notification.isRead) {
            Box(
                Modifier
                    .dropShadow(
                        shape = RoundedCornerShape(20.dp),
                        shadow = Shadow(
                            radius = 5.dp,
                            spread = 2.dp,
                            color = Color(0xFF820006).copy(alpha = 0.25f),
                        )
                    )
                    .clip(CircleShape)
                    .background(Color(0xFF820006))
                    .size(8.dp),
            )
            Spacer(Modifier.width(8.dp))
        }

        Box(
            Modifier
                .clip(RoundedCornerShape(13.dp))
                .background(style.color.copy(alpha = 0.3f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(style.iconRes),
                contentDescription = "Notification Icon",
                modifier = Modifier.size(28.dp),
                tint = style.color
            )
        }

        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = formatNotificationMessage(notification.message, style.color),
                style = TextStyle(
                    fontFamily = rethinkSans,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF3B3B3B),
                    lineHeight = 18.sp
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                notification.createdAt.toSimpleTime(),
                style = TextStyle(
                    fontFamily = rethinkSans,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF3B3B3B).copy(alpha = 0.6f)
                ),
            )
        }
    }
}

private data class NotificationStyle(val color: Color, val iconRes: Int)

@Composable
private fun formatNotificationMessage(message: String, highlightColor: Color): AnnotatedString {
    val keywords = listOf(
        "approved",
        "rejected",
        "auto-rejected",
        "absent",
        "Staff",
        "re-registered",
        "successfully registered",
        "Cancelled",
        "unsubscribed"
    )

    return buildAnnotatedString {
        val parts = message.split(" ")
        parts.forEachIndexed { index, word ->
            val cleanWord =
                word.trim().replace("\"", "").replace(".", "").replace("!", "").replace(":", "")

            if (keywords.any { it.equals(cleanWord, ignoreCase = true) }) {
                withStyle(SpanStyle(color = highlightColor, fontWeight = FontWeight.Black)) {
                    append(word)
                }
            } else {
                append(word)
            }

            if (index < parts.size - 1) append(" ")
        }
    }
}
