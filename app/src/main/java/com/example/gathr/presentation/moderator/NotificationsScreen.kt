package com.example.gathr.presentation.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.presentation.moderator.EventData
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars


// --------------------------------------------------
// ENUM
// --------------------------------------------------
enum class NotificationType {
    EVENT_SUBMITTED,
    EVALUATION_READY,
    EVENT_UPDATED,
}

// --------------------------------------------------
// MODEL
// --------------------------------------------------
data class NotificationItem(
    val id: Int,
    val event: EventData,
    val type: NotificationType,
    val time: String,
    var isNew: Boolean
)

// --------------------------------------------------
// MAIN SCREEN
// --------------------------------------------------
@Composable
fun NotificationsScreen(
    events: List<EventData>,
    onNotificationsClick: () -> Unit,
    onPendingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var activeBottomTab by remember { mutableStateOf("Notifications") }

    val topGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7954AB), Color(0xFF312245))
    )
    val bottomGradient = Color(0xFF312245)

    val firstEvent = events.firstOrNull()

    if (firstEvent == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No notifications available", color = Color.White)
        }
        return
    }

    // -- SAMPLE NEW NOTIFICATIONS
    val newList = remember {
        mutableStateListOf<NotificationItem>().apply {
            repeat(4) { index ->
                add(
                    NotificationItem(
                        id = index,
                        event = firstEvent,
                        type = NotificationType.EVENT_SUBMITTED,
                        time = "10:${index} AM",
                        isNew = true
                    )
                )
            }
        }
    }

    // -- TODAY NOTIFICATIONS
    val todayList = remember {
        mutableStateListOf<NotificationItem>().apply {
            repeat(10) { index ->
                add(
                    NotificationItem(
                        id = 100 + index,
                        event = firstEvent,
                        type = NotificationType.EVENT_UPDATED,
                        time = "11:${index} AM",
                        isNew = false
                    )
                )
            }
        }
    }

    // MARK ALL NEW → TODAY
    fun markAllRead() {
        newList.forEach { it.isNew = false }
        todayList.addAll(0, newList.toList())
        newList.clear()
    }

    // --------------------------------------------------
    // UI
    // --------------------------------------------------
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF312245))
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {

        // ------------------ HEADER --------------------
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            // 🔻 SUPER VISIBLE SHADOW BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.13f)   // bigger so you can see it clearly
                    .offset(y = 9.dp)               // pushes the shadow down more
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.45f),   // brighter
                                Color.White.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(
                            bottomStart = 40.dp,
                            bottomEnd = 40.dp
                        )
                    )
            )

            //HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight * 0.12f)
                    .background(
                        brush = topGradient,
                        shape = RoundedCornerShape(
                            bottomStart = 20.dp,
                            bottomEnd = 20.dp
                        )
                    )
                    .padding(top = screenHeight * 0.02f),   // ✔️ this must be OUTSIDE .background()
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Notifications",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = rethinkSans
                )
            }


        }






        // ------------------ CONTENT AREA --------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(bottomGradient)

        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = (-15).dp)
            ) { // items...
            if (newList.isNotEmpty()) {

                    // SECTION HEADER
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "New",
                                color = Color.White,
                                fontFamily = rethinkSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(onClick = { markAllRead() }) {
                                Text(
                                    "Mark all read",
                                    color = Color(0xFFF36F44),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // INDIVIDUAL ITEMS
                    items(newList) { notif ->
                        NotificationRow(
                            notif = notif,
                            isNewBackground = true,
                            onClick = { clicked ->
                                newList.remove(clicked)
                                clicked.isNew = false
                                todayList.add(0, clicked)
                            }
                        )
                    }
                }

                // TODAY HEADER
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Today",
                        color = Color.White,
                        fontFamily = rethinkSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 14.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // TODAY LIST
                items(todayList) { notif ->
                    NotificationRow(
                        notif = notif,
                        isNewBackground = false,
                        onClick = {}
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth() // makes it stretch horizontally
                .height(1.dp)   // line thickness
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth() // makes it stretch horizontally
                .height(1.dp)   // line thickness
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
        // ------------------ BOTTOM NAV --------------------
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
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(tint)
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
}



// --------------------------------------------------
// NOTIFICATION ROW
// --------------------------------------------------
@Composable
fun NotificationRow(
    notif: NotificationItem,
    isNewBackground: Boolean,
    onClick: (NotificationItem) -> Unit
) {
    val icon = when (notif.type) {
        NotificationType.EVENT_SUBMITTED -> R.drawable.ic_approval
        NotificationType.EVALUATION_READY -> R.drawable.ic_evaluation
        NotificationType.EVENT_UPDATED -> R.drawable.ic_reapproval
    }

    val titleText = when (notif.type) {
        NotificationType.EVENT_SUBMITTED ->
            "New event submitted for approval: ${notif.event.title}"
        NotificationType.EVALUATION_READY ->
            "Evaluation form ready for: ${notif.event.title}"
        NotificationType.EVENT_UPDATED ->
            "Event updated and needs reapproval: ${notif.event.title}"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(notif) }
            .background(
                if (isNewBackground)
                    Color(0xFF7954AB).copy(alpha = 0.50f)
                else
                    Color(0xFF312245)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // RED INDICATOR
        if (notif.isNew) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color(0xFF820006), shape = MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        // ICON
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(46.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                titleText,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                notif.time,
                color = Color(0xFFE1D9F5),
                fontSize = 12.sp
            )
        }
    }
}
