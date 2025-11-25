package com.example.gathr.presentation.participant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.presentation.shared.LargeEventCard
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.ui.theme.AppFonts

@Composable
fun MyEventsScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigateQrCode: () -> Unit,
    onNavigateModifyEvent: () -> Unit,
    onNavigateUpdateEvent: () -> Unit,
    onNavigateViewEvent: () -> Unit,
) {
    Box(Modifier.fillMaxSize()) {
        MyEventContent(
            state, onIntent,
            onNavigateQrCode,
            onNavigateModifyEvent,
            onNavigateUpdateEvent,
            onNavigateViewEvent
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
fun MyEventContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigateQrCode: () -> Unit,
    onNavigateModifyEvent: () -> Unit,
    onNavigateUpdateEvent: () -> Unit,
    onNavigateViewEvent: () -> Unit,
) {
    val tabTitles = listOf("Pending", "Approved", "Rejected", "Removed")
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .dropShadow(
                    shape = RoundedCornerShape(0.dp),
                    shadow = Shadow(
                        radius = 30.dp,
                        spread = 0.dp,
                        color = Color(0xFF000000).copy(alpha = 0.25f),
                        offset = DpOffset(x = 0.dp, (1).dp)
                    )
                )
                .fillMaxWidth()
                .background(Color(0xFFF6F6F6))
                .padding(top = 30.dp),
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF6F6F6)),
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            {
                Text(
                    "My Events",
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                SearchTextField(
                    searchText,
                    onValueChange = { searchText = it },
                    onClearValue = { searchText = "" },
                    placeholderText = "Search in My Events",
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))
                SecondaryTabRow(
                    containerColor = Color(0xFFF6F6F6),
                    selectedTabIndex = selectedTabIndex,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(selectedTabIndex)
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(7.5.dp)),
                            height = 5.dp,
                            color = Color(0xFF473163)
                        )
                    },
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = (selectedTabIndex == index),
                            selectedContentColor = Color(0xFF473163),
                            unselectedContentColor = Color(0xFF473163),
                            onClick = {
                                selectedTabIndex = index
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = TextStyle(
                                        fontFamily = AppFonts.rethinkSans,
                                        fontSize = 14.sp,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    ),
                                )
                            }
                        )
                    }
                }
            }
        }
        Box {
            fun filterEvents() {

            }

            when (selectedTabIndex) {
                0 -> MyEventsCards(
                    events = state.currentMyEvents.filter { it.status == EventApprovalStatus.PENDING },
                    onIntent = onIntent,
                    onNavigateViewEvent = onNavigateViewEvent,
                    onNavigateUpdateEvent = onNavigateUpdateEvent,
                    onNavigateQrCode = onNavigateQrCode
                )

                1 -> MyEventsCards(
                    events = state.currentMyEvents.filter { it.status == EventApprovalStatus.APPROVED },
                    onIntent = onIntent,
                    onNavigateViewEvent = onNavigateViewEvent,
                    onNavigateUpdateEvent = onNavigateUpdateEvent,
                    onNavigateQrCode = onNavigateQrCode
                )

                2 -> MyEventsCards(
                    events = state.currentMyEvents.filter { it.status == EventApprovalStatus.REJECTED },
                    onIntent = onIntent,
                    onNavigateViewEvent = onNavigateViewEvent,
                    onNavigateUpdateEvent = onNavigateUpdateEvent,
                    onNavigateQrCode = onNavigateQrCode
                )

                3 -> MyEventsCards(
                    events = state.currentMyEvents.filter { it.isArchive },
                    onIntent = onIntent,
                    onNavigateViewEvent = onNavigateViewEvent,
                    onNavigateUpdateEvent = onNavigateUpdateEvent,
                    onNavigateQrCode = onNavigateQrCode
                )
            }

            val gradientColors = listOf(Color(0xFF7B55A3), Color(0xFF583181))
            Button(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(bottom = 35.dp, end = 20.dp),
                onClick = {},
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.horizontalGradient(colors = gradientColors))
                        .clickable { onNavigateModifyEvent() }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Create Event",
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ),
                        )
                        Spacer(Modifier.width(5.dp))
                        Icon(
                            painter = painterResource(R.drawable.add_icon),
                            contentDescription = "Create event",
                            modifier = Modifier.size(15.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyEventsCards(
    events: List<Event> = emptyList(),
    onIntent: (UserIntent) -> Unit,
    onNavigateViewEvent: () -> Unit,
    onNavigateUpdateEvent: () -> Unit,
    onNavigateQrCode: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(Modifier.height(25.dp))
            Image(
                alignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth(),
                painter = painterResource(R.drawable.my_events_banner),
                contentDescription = "Events banner",
                contentScale = ContentScale.FillWidth
            )
        }
        items(events, key = { event -> event.id }) { event ->
            LargeEventCard(
                event = event,
                onCardClick = {
                    onIntent(UserIntent.CurrentEventChanged(event))
                    onNavigateViewEvent()
                },
                onTextButtonClick = {
                    onIntent(UserIntent.CurrentEventChanged(event))
                    onNavigateQrCode()
                })
        }
        item {
            Spacer(Modifier.height(25.dp))
        }
    }

}

//@Preview(showBackground = true)
//@Composable
//private fun MyEventsScreenPreview() {
//    Scaffold { paddingValues ->
//        MyEventsScreen()
//    }
//}
