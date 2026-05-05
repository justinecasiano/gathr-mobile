package com.example.gathr.presentation.participant

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.presentation.shared.LargeEventCard
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.presentation.shared.SearchNotFound
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.dummyEvents

@Composable
fun StaffScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.userEffect.collect { effect ->
            when (effect) {
                UserEffect.NavigateNext -> {}
                UserEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        StaffContent(
            state = state,
            onIntent = viewModel::handleIntent,
            onNavigate = viewModel::sendMainEffect
        )

        if (state.isLoading) {
            LoadingOverlay()
        }
        when {
            state.actionError.isNotBlank() -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Error" },
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    var staffedEvents: List<Event> =
        state.managedEvents.filter { it.userParticipantType === ParticipantType.STAFF }
            .map { it.event }
    var eventList: List<Event> = emptyList()

    val tabTitles = listOf("Pending", "Approved", "Rejected", "Removed")
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color(0xFFF6F6F6),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF6F6F6)),
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
                    .background(Color(0xFFF6F6F6)),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFFF6F6F6)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
                {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        IconButton(
                            onClick = { onIntent(UserIntent.BackClicked) },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(37.dp),
                                tint = Color.Black,
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    }
                    Text(
                        "Staffed Events",
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
                        placeholderText = "Search in Staffed Events",
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

            Log.d("EVENT_LIST", eventList.toString())
            eventList = staffedEvents.filter {
                when (selectedTabIndex) {
                    0 -> it.status == EventApprovalStatus.PENDING

                    1 -> it.status == EventApprovalStatus.APPROVED

                    2 -> it.status == EventApprovalStatus.REJECTED

                    else -> it.isArchive
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
                            events = eventList,
                            showBanner = false,
                            role = "STAFF",
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
                        role = "STAFF",
                        isETicket = false,
                        onIntent = onIntent,
                        onNavigate = onNavigate,
                        onTextButtonClick = {
                            onNavigate(MainEffect.NavigateQrScanner)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun MyEventsCards(
    events: List<Event> = emptyList(),
    showBanner: Boolean = true,
    role: String = "ATTENDEE",
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
                        "See your staffed events\nat this section",
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 25.dp)
                    )
                }
            }
        }
        items(events, key = { event -> event.id }) { event ->
            val isRejectedOrRemoved =
                event.status == EventApprovalStatus.REJECTED || event.isArchive
            LargeEventCard(
                event = event,
                role = role,
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
