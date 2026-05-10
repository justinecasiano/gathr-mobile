package com.example.gathr.presentation.participant

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.data.model.DepartmentType
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.shared.LargeEventCard
import com.example.gathr.presentation.shared.SearchNotFound
import com.example.gathr.presentation.shared.SmallEventCard
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toAbbreviatedString
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toTitleCase
import kotlin.math.max

@Composable
fun ParticipantEventsScreen(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        ParticipantEventContent(
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

data class Tab(
    val title: String,
    @DrawableRes val icon: Int,
)

@Composable
fun ParticipantEventContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    val tabs = listOf(
        Tab("Upcoming", R.drawable.upcoming_icon),
        Tab("Ongoing", R.drawable.ongoing_icon),
        Tab("Ended", R.drawable.completed_icon)
    )
    val searchText = state.eventsSearchText
    val selectedFilter = state.activeEventsFilter
    val isFilterActive = selectedFilter != null

    val userDept = state.currentUser?.department ?: DepartmentType.ALL

    val eventList: List<Event> = remember(state.joinableEvents, state.eventsSearchText, userDept) {
        state.joinableEvents
            .filter { joinableEvent ->
                val event = joinableEvent
                val allowedDepts = event.allowedDepartments

                val isOpenToAll =
                    allowedDepts.isNullOrEmpty() || allowedDepts.contains(DepartmentType.ALL)

                val isUserDeptAllowed = allowedDepts?.contains(userDept) == true

                val isAlumniAllowed = (state.currentUser?.isAlumni ?: false) && event.allowAlumni

                isOpenToAll || isUserDeptAllowed || isAlumniAllowed
            }
            .filter { event ->
                if (searchText.isBlank()) {
                    true
                } else {
                    event.title.contains(searchText, ignoreCase = true) ||
                            event.startTime.toPrettyString("MMM. d, yyyy").contains(
                                searchText,
                                ignoreCase = true
                            ) ||
                            event.remainingSlots.toAbbreviatedString()
                                .contains(searchText, ignoreCase = true) ||
                            event.organizerName.contains(searchText, ignoreCase = true)
                }
            }
    }

    val upcomingEvents: List<Event> =
        eventList.filter { it -> it.computedStatus == EventComputedStatus.UPCOMING }
    val ongoingEvents: List<Event> =
        eventList.filter { it -> it.computedStatus == EventComputedStatus.ONGOING }
    val endedEvents: List<Event> =
        eventList.filter { it -> it.computedStatus == EventComputedStatus.ENDED }

    val activeEvents = eventList.filter {
        it.computedStatus == EventComputedStatus.UPCOMING ||
                it.computedStatus == EventComputedStatus.ONGOING
    }
    val popularEvent = activeEvents.sortedBy { it.remainingSlots }
        .maxByOrNull { event ->
            val slotsTaken = max(0, event.capacity - event.remainingSlots)
            val fillRatio = slotsTaken.toFloat() / event.capacity.toFloat()
            fillRatio
        }


    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxHeight()
                .padding(top = 30.dp)
                .background(Color.White)
        ) {
            Box(Modifier.padding(horizontal = 20.dp)) {
                SearchTextField(
                    searchText,
                    onValueChange = { onIntent(UserIntent.EventsSearchTextChanged(it)) },
                    onClearValue = { onIntent(UserIntent.EventsSearchTextChanged("")) },
                    placeholderText = "Search for events",
                )
            }
            Spacer(Modifier.height(20.dp))
            if (searchText.isNotBlank()) {
                if (eventList.isNotEmpty()) {
                    LazyColumn(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                    ) {
                        item {
                            Text(
                                "Search result(s) for \"$searchText\"",
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                ),
                            )
                            Spacer(Modifier.height(10.dp))
                        }
                        item {
                            FlowRow(
                                Modifier.fillMaxWidth(),
                                maxItemsInEachRow = 2,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                eventList.forEach { event ->
                                    SmallEventCard(
                                        event, onClick = {
                                            onIntent(UserIntent.CurrentEventChanged(event))
                                            onNavigate(MainEffect.NavigateParticipantViewEvent)
                                        },
                                        cardWidth = 160.dp,
                                        isDetailed = true
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                } else if (eventList.isEmpty()) {
                    SearchNotFound(
                        Modifier
                            .padding(horizontal = 20.dp)
                            .offset(y = (-70).dp)
                    )
                }
            } else {
                var rowSize by remember { mutableStateOf(IntSize.Zero) }
                val density = LocalDensity.current
                val rowWidthDp = with(density) { rowSize.width.toDp() - 100.dp }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val y = size.height - strokeWidth / 2

                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        }
                        .padding(bottom = 20.dp)
                        .onGloballyPositioned { coordinates -> rowSize = coordinates.size },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(3) { index ->
                        val oneThirdWidth = rowWidthDp / 3f

                        Column(
                            Modifier
                                .width(oneThirdWidth)
                                //                        .weight(1f)
                                .clickable {
                                    onIntent(UserIntent.ActiveEventsFilterChanged(tabs[index].title))
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painterResource(tabs[index].icon),
                                contentDescription = tabs[index].title,
                                Modifier.size(65.dp)
                            )
                            Spacer(Modifier.height(10.dp))
                            Text(
                                text = tabs[index].title,
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF473163)
                                ),
                            )
                        }
                    }
                }
                LazyColumn {
                    item {
                        Spacer(Modifier.height(10.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Image(
                                modifier = Modifier.width(388.dp),
                                painter = painterResource(R.drawable.events_banner),
                                contentDescription = "Event Banner",
                                contentScale = ContentScale.FillWidth
                            )
                            val user = state.currentUser!!
                            Text(
                                "Welcome to Gathr,\n${user.firstName.toTitleCase()}",
                                style = TextStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Start,
                                    color = Color.White
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .align(Alignment.CenterStart)
                                    .padding(start = 30.dp, top = 5.dp)
                            )
                        }
                        Column(Modifier.padding(horizontal = 20.dp)) {
                            if (popularEvent != null) {
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    "Popular",
                                    style = TextStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Start,
                                        color = Color.Black
                                    ),
                                )
                                Spacer(Modifier.height(10.dp))
                                LargeEventCard(
                                    event = popularEvent,
                                    onCardClicked = {
                                        onIntent(UserIntent.CurrentEventChanged(popularEvent))
                                        onNavigate(MainEffect.NavigateParticipantViewEvent)
                                    },
                                    isETicket = false,
                                    onTextButtonClick = {}
                                )
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onIntent(UserIntent.ActiveEventsFilterChanged("Upcoming"))
                                }
                                .padding(vertical = 5.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Upcoming events",
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Start,
                                    color = Color(0xFF232222)
                                ),
                            )
                            Spacer(Modifier.width(15.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.right_arrow),
                                contentDescription = "View upcoming events",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Spacer(Modifier.width(1.dp))
                            }
                            items(
                                items = upcomingEvents,
                                key = { event -> event.id }) { event ->
                                SmallEventCard(
                                    event,
                                    onClick = {
                                        onIntent(UserIntent.CurrentEventChanged(event))
                                        onNavigate(MainEffect.NavigateParticipantViewEvent)
                                    },
                                )
                            }
                            item {
                                Spacer(Modifier.width(1.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                    item {
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onIntent(UserIntent.ActiveEventsFilterChanged("Ongoing"))
                                }
                                .padding(vertical = 5.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Ongoing events",
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Start,
                                    color = Color(0xFF232222)
                                ),
                            )
                            Spacer(Modifier.width(15.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.right_arrow),
                                contentDescription = "View ongoing events",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Spacer(Modifier.width(1.dp))
                            }
                            items(
                                items = ongoingEvents,
                                key = { event -> event.id }) { event ->
                                SmallEventCard(
                                    event,
                                    onClick = {
                                        onIntent(UserIntent.CurrentEventChanged(event))
                                        onNavigate(MainEffect.NavigateParticipantViewEvent)
                                    },
                                )
                            }
                            item {
                                Spacer(Modifier.width(1.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                    item {
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onIntent(UserIntent.ActiveEventsFilterChanged("Ended"))
                                }
                                .padding(vertical = 5.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Ended events",
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Start,
                                    color = Color(0xFF232222)
                                ),
                            )
                            Spacer(Modifier.width(15.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.right_arrow),
                                contentDescription = "View ended events",
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(15.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                Spacer(Modifier.width(1.dp))
                            }
                            items(
                                items = endedEvents,
                                key = { event -> event.id }) { event ->
                                SmallEventCard(
                                    event,
                                    onClick = {
                                        onIntent(UserIntent.CurrentEventChanged(event))
                                        onNavigate(MainEffect.NavigateParticipantViewEvent)
                                    },
                                )
                            }
                            item {
                                Spacer(Modifier.width(1.dp))
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isFilterActive,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
        ) {
            if (selectedFilter != null) {
                ParticipantFilterScreen(
                    title = selectedFilter,
                    eventList = eventList.filter { event ->
                        event.computedStatus.toString().lowercase() == selectedFilter.lowercase()
                    },
                    onBack = {
                        onIntent(UserIntent.ActiveEventsFilterChanged(null))
                    },
                    onIntent,
                    onNavigate
                )
            }
        }
    }
}

@Composable
fun ParticipantFilterScreen(
    title: String,
    eventList: List<Event>,
    onBack: () -> Unit,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2

                    drawLine(
                        color = Color.LightGray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(horizontal = 30.dp)
                .padding(top = 20.dp, bottom = 15.dp),
        ) {
            IconButton(
                onClick = onBack,
            ) {
                Icon(
                    modifier = Modifier
                        .size(37.dp)
                        .align(Alignment.CenterStart),
                    tint = Color.Black,
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back"
                )
            }
            Spacer(Modifier.width(50.dp))
            Text(
                "$title Events",
                style = TextStyle(
                    fontFamily = AppFonts.rethinkSans,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(
                items = eventList,
                key = { it.id },
                contentType = { "event_card" }
            ) { event ->
                SmallEventCard(
                    event = event,
                    onClick = {
                        onIntent(UserIntent.CurrentEventChanged(event))
                        onNavigate(MainEffect.NavigateParticipantViewEvent)
                    },
                    cardWidth = 160.dp,
                    isDetailed = true
                )
            }
        }
    }
}

