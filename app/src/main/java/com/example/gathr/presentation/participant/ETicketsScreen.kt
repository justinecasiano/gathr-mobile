package com.example.gathr.presentation.participant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.presentation.shared.LargeEventCard
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.shared.SearchNotFound
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.dummyEvents

@Composable
fun ETicketsScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        ETicketsContent(
            state, onIntent, onNavigate
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
fun ETicketsContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    var eventList: List<Event> = state.currentEvents.filter { it.isRegistered }
    var eTicketsList: List<Event> = emptyList()

    val tabTitles = listOf("Upcoming", "Ongoing", "Completed")
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
                    "Tickets",
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
                    placeholderText = "Search in Tickets",
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
                                searchText = ""
                            },
                            text = {
                                Text(
                                    text = title,
                                    style = TextStyle(
                                        fontFamily = AppFonts.rethinkSans,
                                        fontSize = 12.sp,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    ),
                                )
                            }
                        )
                    }
                }
            }
        }

        eTicketsList = eventList.filter {
            when (selectedTabIndex) {
                0 -> it.computedStatus == EventComputedStatus.UPCOMING

                1 -> it.computedStatus == EventComputedStatus.ONGOING

                else -> it.computedStatus == EventComputedStatus.COMPLETED
            }
        }.filter { event ->
            if (searchText.isBlank()) {
                true
            } else {
                event.title.contains(searchText, ignoreCase = true) ||
                        event.description.contains(searchText, ignoreCase = true)
            }
        }

        Box(Modifier.fillMaxSize()) {
            if (searchText.isNotBlank() && eventList.isNotEmpty()) {
                Column {
                    Spacer(Modifier.height(25.dp))
                    Text(
                        "Search result(s) for \"$searchText\"",
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                        ),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    MyEventsCards(
                        events = eTicketsList,
                        showBanner = false,
                        isOrganizer = false,
                        isETicket = true,
                        onIntent = onIntent,
                        onNavigate = onNavigate,
                        onTextButtonClick = {
                            onNavigate(MainEffect.NavigateQrCode)
                        },
                    )
                }
            } else if (searchText.isNotBlank() && eventList.isEmpty()) {
                SearchNotFound(Modifier.padding(horizontal = 20.dp))
            } else {
                MyEventsCards(
                    events = eTicketsList,
                    showBanner = searchText.isBlank(),
                    isOrganizer = false,
                    isETicket = true,
                    onIntent = onIntent,
                    onNavigate = onNavigate,
                    onTextButtonClick = {
                        onNavigate(MainEffect.NavigateQrCode)
                    },
                )
            }
        }
    }
}

@Composable
private fun MyEventsCards(
    events: List<Event> = emptyList(),
    showBanner: Boolean = true,
    isOrganizer: Boolean = false,
    isETicket: Boolean = false,
    onTextButtonClick: () -> Unit,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            if (showBanner) {
                Spacer(Modifier.height(15.dp))
                Box(
                    Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.width(379.dp),
                        painter = painterResource(R.drawable.etickets_banner),
                        contentDescription = "Events banner",
                        contentScale = ContentScale.FillWidth
                    )
                    Text(
                        "See your event\ntickets at this section",
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 25.dp, top = 10.dp)
                    )
                }
            }
        }
        items(events, key = { event -> event.id }) { event ->
            val isRejectedOrRemoved =
                event.status == EventApprovalStatus.REJECTED || event.isArchive
            LargeEventCard(
                event = event,
                role = "ATTENDEE",
                isRejectedOrRemoved = isRejectedOrRemoved,
                isETicket = isETicket,
                onCardClicked = {
                    onIntent(UserIntent.CurrentEventChanged(event))
                    onNavigate(MainEffect.NavigateParticipantViewEvent)
                },
                onTextButtonClick = {
                    onIntent(UserIntent.CurrentEventChanged(event))
                    onTextButtonClick()
                },
                onUpdateClicked = {
                    onIntent(UserIntent.CurrentEventChanged(event))
                    onNavigate(MainEffect.NavigateUpdateEvent)
                }
            )
        }
        item {
            Spacer(Modifier.height(25.dp))
        }
    }

}
