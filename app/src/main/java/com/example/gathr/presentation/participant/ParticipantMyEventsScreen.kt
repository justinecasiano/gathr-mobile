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
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.shared.SearchNotFound
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toAbbreviatedString
import com.example.gathr.utils.toSimpleTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ParticipantMyEventsScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        ParticipantMyEventContent(
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
fun ParticipantMyEventContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    var eventList: List<Event> =
        state.managedEvents.filter { it.userParticipantType === ParticipantType.ORGANIZER }
            .map { it.event }

    val tabTitles = listOf("Pending", "Approved", "Rejected", "Removed")
    val selectedTabIndex = state.myEventsSelectedTabIndex
    val searchText = state.myEventsSearchText

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
                    onValueChange = { onIntent(UserIntent.MyEventsSearchTextChanged(it)) },
                    onClearValue = { onIntent(UserIntent.MyEventsSearchTextChanged("")) },
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
                                onIntent(UserIntent.MyEventsSelectedTabChanged(index))
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

        eventList = eventList.filter {
            when (selectedTabIndex) {
                0 -> it.status == EventApprovalStatus.PENDING && !it.isArchive

                1 -> it.status == EventApprovalStatus.APPROVED && !it.isArchive

                2 -> it.status == EventApprovalStatus.REJECTED && !it.isArchive

                else -> it.isArchive
            }
        }.filter { event ->
            if (searchText.isBlank()) {
                true
            } else {
                event.title.contains(searchText, ignoreCase = true) ||
                        "${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}".contains(
                            searchText,
                            ignoreCase = true
                        ) ||
                        event.remainingSlots.toAbbreviatedString()
                            .contains(searchText, ignoreCase = true)
            }
        }.let { filtered ->
            if (selectedTabIndex >= 3) {
                filtered.sortedByDescending { it.deletedAt ?: it.submittedAt }
            } else {
                filtered.sortedByDescending { it.submittedAt }
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
                        events = eventList,
                        showBanner = false,
                        isETicket = false,
                        onIntent = onIntent,
                        onNavigate = onNavigate,
                        onTextButtonClick = {
                            onNavigate(MainEffect.NavigateQrScanner)
                        },
                    )
                }
            } else if (searchText.isNotBlank() && eventList.isEmpty()) {
                SearchNotFound(Modifier.padding(horizontal = 20.dp))
            } else {
                MyEventsCards(
                    events = eventList,
                    showBanner = searchText.isBlank(),
                    isETicket = false,
                    onIntent = onIntent,
                    onNavigate = onNavigate,
                    onTextButtonClick = {
                        onNavigate(MainEffect.NavigateQrScanner)
                    },
                )
            }

            val gradientColors = listOf(Color(0xFF7B55A3), Color(0xFF583181))
            Button(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .padding(bottom = 25.dp, end = 20.dp),
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
                        .clickable { onNavigate(MainEffect.NavigateCreateEvent) }
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
    showBanner: Boolean = true,
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
                Spacer(Modifier.height(25.dp))
                Box(
                    Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier.width(379.dp),
                        painter = painterResource(R.drawable.my_events_banner),
                        contentDescription = "Events banner",
                        contentScale = ContentScale.FillWidth
                    )
                    Text(
                        "See your created events\nat this section",
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 15.dp)
                    )
                }
            }
        }
        items(events, key = { event -> event.id }, contentType = { "large_card" }) { event ->
            val scope = CoroutineScope(Dispatchers.Main)
            val isRejectedOrRemoved =
                event.status == EventApprovalStatus.REJECTED || event.isArchive
            LargeEventCard(
                event = event,
                role = "ORGANIZER",
                isRejectedOrRemoved = isRejectedOrRemoved,
                isETicket = isETicket,
                onCardClicked = {
                    onIntent(UserIntent.CurrentEventChanged(event))
                    onNavigate(MainEffect.ViewEvent)
                },
                onTextButtonClick = {
                    onIntent(UserIntent.CurrentEventChanged(event))
                    onTextButtonClick()
                },
                onUpdateClicked = {
                    scope.launch {
                        onIntent(UserIntent.IsLoadingChanged(true))
                        onIntent(UserIntent.CurrentEventChanged(event))
                        onNavigate(MainEffect.NavigateUpdateEvent)
                    }
                }
            )
        }
        item {
            Spacer(Modifier.height(25.dp))
        }
    }

}