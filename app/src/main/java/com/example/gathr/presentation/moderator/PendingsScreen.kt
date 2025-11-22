package com.example.gathr.presentation.moderator

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.SearchClearField
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.PaddingValues


@Composable
fun PendingsScreen(
    events: List<EventData>,                // your event list now comes from outside
    onEventClick: (EventData) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Pending") }
    var activeBottomTab by remember { mutableStateOf("Pendings") }

    val topGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF7954AB), Color(0xFF312245))
    )
    val bottomGradient = Color(0xFF312245)

    // 🔍 Filtering uses your existing EventData
    val filteredEvents = remember(searchText, events) {
        if (searchText.isBlank()) events
        else events.filter { event ->
            event.title.contains(searchText, ignoreCase = true) ||
                    event.location.contains(searchText, ignoreCase = true) ||
                    event.organizer.contains(searchText, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(topGradient)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {
        // ------------------ HEADER --------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(screenHeight * 0.18f)
                .background(topGradient),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = screenWidth * 0.05f)
                    .padding(top = screenHeight * 0.02f),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pending Events",
                    color = Color.White,
                    fontFamily = rethinkSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = (screenWidth.value * 0.06f).sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SearchClearField(
                        text = searchText,
                        onValueChange = { searchText = it },
                        onClearClick = { searchText = "" },
                        placeholder = "Search in pending events",
                        modifier = Modifier.weight(1f)
                    )

                }

                val labels = listOf("Pending", "Approved", "Rejected", "Removed")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    labels.forEach { label ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = label }
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontFamily = rethinkSans,
                                fontSize = (screenWidth.value * 0.036f).sp,
                                fontWeight = if (selectedTab == label) FontWeight.Bold else FontWeight.Normal
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            if (selectedTab == label) {
                                Box(
                                    modifier = Modifier
                                        .width(screenWidth * 0.18f)
                                        .height(screenHeight * 0.006f)
                                        .background(Color.White, shape = RoundedCornerShape(50.dp))
                                )
                            } else {
                                Spacer(modifier = Modifier.height(screenHeight * 0.006f))
                            }
                        }
                    }
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
        // ------------------ EVENT GRID --------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f)
                .background(Color(0xFF312245))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = screenWidth * 0.05f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            )
            {

                item {
                    Text(
                        text = "Needs Review",
                        color = Color.White,
                        fontFamily = rethinkSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = (screenWidth.value * 0.05f).sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                val rows = filteredEvents.chunked(2)

                items(rows) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        row.forEach { event ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onEventClick(event) }
                            ) {
                                // Image
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.9f)
                                        .clip(RoundedCornerShape(16.dp))
                                ) {
                                    Image(
                                        painter = painterResource(id = event.imageRes),
                                        contentDescription = event.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .background(Color.White, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = event.status,
                                            color = Color.Black,
                                            fontFamily = rethinkSans,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = (screenWidth.value * 0.03f).sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Column(modifier = Modifier.padding(4.dp)) {
                                    Text(
                                        text = event.title,
                                        color = Color.White,
                                        fontFamily = rethinkSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (screenWidth.value * 0.04f).sp
                                    )
                                    Text(
                                        text = event.location,
                                        color = Color.White,
                                        fontFamily = rethinkSans,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = (screenWidth.value * 0.032f).sp
                                    )
                                    Text(
                                        text = event.dateTime,
                                        color = Color.White,
                                        fontFamily = rethinkSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (screenWidth.value * 0.032f).sp
                                    )
                                    Text(
                                        text = "Organizer: ${event.organizer}",
                                        color = Color.White,
                                        fontFamily = rethinkSans,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = (screenWidth.value * 0.032f).sp
                                    )
                                    Text(
                                        text = "${event.slots} slots left",
                                        color = Color(0xFFFC3436),
                                        fontFamily = rethinkSans,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (screenWidth.value * 0.032f).sp
                                    )
                                }
                            }
                        }

                        if (row.size < 2) Spacer(modifier = Modifier.weight(1f))
                    }
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
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable {
                            activeBottomTab = label

                            if (label == "Notifications") {
                                onNotificationsClick()
                            }
                            else if (label == "Account") {
                                onProfileClick()
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
