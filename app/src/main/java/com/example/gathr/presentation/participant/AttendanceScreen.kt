package com.example.gathr.presentation.participant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.ParticipantStatus
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.UserRole
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.utils.dummyEvents
import com.example.gathr.utils.dummyParticipants
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toTitleCase
import java.lang.Enum.valueOf
import java.time.Instant

data class RegisteredUser(
    val name: String,
    val dateAndTime: Instant,
    val status: ParticipantStatus
)

@Composable
fun AttendanceScreen(viewModel: UserViewModel, onNavigateBack: () -> Unit) {
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
        AttendanceContent(
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
fun AttendanceContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val event: Event = state.currentEvent!!
    val role: UserRole = state.currentUser!!.role
    val participantList =
        state.currentAttendees.sortedByDescending { it.joinedAt }
    val isEventOngoing = event.computedStatus == EventComputedStatus.ONGOING

    var searchList = participantList
    if (searchText.isNotBlank())
        searchList =
            participantList.filter { p ->
                p.fullName?.contains(
                    searchText,
                    ignoreCase = true
                ) == true || p.joinedAt.toPrettyString("MMM. d, yyyy - h:mm a")
                    .contains(searchText, ignoreCase = true) || p.participantStatus.toString()
                    .contains(searchText, ignoreCase = true)
            }

    Scaffold(
        containerColor = Color(0xFFF6F6F6),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Attendance",
                        style = TextStyle(
                            fontFamily = rethinkSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF6F6F6)
                ),
                navigationIcon = {
                    Box(Modifier.padding(top = 10.dp, start = 10.dp)) {
                        IconButton(onClick = { onIntent(UserIntent.BackClicked) }) {
                            Icon(
                                modifier = Modifier.size(37.dp),
                                tint = Color.Black,
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            if (isEventOngoing && role != UserRole.MODERATOR) {
                Box(Modifier.padding(end = 10.dp, bottom = 30.dp)) {
                    FloatingActionButton(
                        onClick = { onNavigate(MainEffect.NavigateQrScanner) },
                        containerColor = Color(0xFF6A3BA8),
                        shape = CircleShape,
                        modifier = Modifier.size(70.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.scan_icon),
                            contentDescription = "Scan",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
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
                        .fillMaxHeight(0.12f)
                        .background(Color(0xFFF6F6F6)),
                )
                Column(
                    Modifier.padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_icon),
                            contentDescription = "Event name",
                            tint = Color.Black.copy(0.8f),
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            event.title.toTitleCase(),
                            style = TextStyle(
                                fontFamily = rethinkSans,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black.copy(0.8f)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    Spacer(Modifier.height(20.dp))

                    var rowSize by remember { mutableStateOf(IntSize.Zero) }
                    val density = LocalDensity.current
                    val rowWidthDp = with(density) { rowSize.width.toDp() - 20.dp }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates -> rowSize = coordinates.size },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val oneThirdWidth = rowWidthDp / 3f

                        val presentCount =
                            participantList.count { participant -> participant.participantStatus == ParticipantStatus.PRESENT || participant.participantStatus == ParticipantStatus.CHECKED_IN }
                        StatCard(
                            number = presentCount.toString(),
                            label = "Present",
                            color = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF7B55A3),
                                    Color(0xFF583181)
                                )
                            ),
                            modifier = Modifier.width(oneThirdWidth)
                        )
                        Spacer(Modifier.width(10.dp))
                        val cancelledCount =
                            participantList.count { participant -> participant.participantStatus == ParticipantStatus.CANCELLED }
                        StatCard(
                            number = cancelledCount.toString(),
                            label = "Cancelled",
                            color = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFFBBA6),
                                    Color(0xFFF6835E)
                                )
                            ),
                            modifier = Modifier.width(oneThirdWidth)
                        )
                        Spacer(Modifier.width(10.dp))
                        val absentCount =
                            participantList.count { participant -> participant.participantStatus == ParticipantStatus.ABSENT }
                        StatCard(
                            number = absentCount.toString(),
                            label = "Absent",
                            color = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF6835E),
                                    Color(0xFF6C0005)
                                )
                            ),
                            modifier = Modifier.width(oneThirdWidth)
                        )
                    }
                }
            }
            Column(Modifier.padding(horizontal = 30.dp)) {
                Spacer(Modifier.height(25.dp))
                SearchTextField(
                    searchText,
                    onValueChange = { searchText = it },
                    onClearValue = { searchText = "" },
                    placeholderText = "Search in ${participantList.count()} participants",
                )
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF9B7CBC),
                                    Color(0xFF583181)
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        )
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Name",
                        style = TextStyle(
                            fontFamily = rethinkSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                            color = Color.White
                        ), modifier = Modifier.weight(2.7f)
                    )
                    Spacer(Modifier.weight(0.4f))
                    Text(
                        "Date & Time",
                        style = TextStyle(
                            fontFamily = rethinkSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Start,
                            color = Color.White
                        ), modifier = Modifier.weight(4f)
                    )
                    Text(
                        "Status",
                        style = TextStyle(
                            fontFamily = rethinkSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.End,
                            color = Color.White
                        ), modifier = Modifier.weight(2f)
                    )
                }
                LazyColumn(Modifier.padding(top = 10.dp)) {
                    items(
                        searchList,
                        key = { participant -> participant.userId }) { participant ->
                        RegisteredRow(
                            RegisteredUser(
                                name = participant.fullName ?: "Unknown User",
                                dateAndTime = participant.joinedAt,
                                status = participant.participantStatus
                            )
                        )
                        HorizontalDivider(color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    color: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .dropShadow(
                shape = RoundedCornerShape(9.dp),
                shadow = Shadow(
                    radius = 4.25.dp,
                    spread = 0.dp,
                    color = Color(0xFF000000).copy(alpha = 0.2f),
                    offset = DpOffset(x = 0.dp, (4.25).dp)
                )
            )
            .clip(RoundedCornerShape(9.dp))
            .background(color)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            number,
            style = TextStyle(
                fontFamily = rethinkSans,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF6F6F6)
            ),
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            label,
            style = TextStyle(
                fontFamily = rethinkSans,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF6F6F6)
            ),
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

@Composable
fun RegisteredRow(user: RegisteredUser) {
    val statusColor = when (user.status) {
        ParticipantStatus.CANCELLED -> Color(0xFFF36F44)
        ParticipantStatus.PRESENT -> Color(0xFF9FC090)
        ParticipantStatus.CHECKED_IN -> Color(0xFF9FC090)
        ParticipantStatus.REGISTERED -> Color(0xFF9FC090)
        ParticipantStatus.ABSENT -> Color(0xFF820006)
        else -> Color.Transparent
    }
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                user.name.toTitleCase(),
                style = TextStyle(
                    fontFamily = rethinkSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Start,
                    color = Color.Black
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(2.7f)
            )
            Spacer(Modifier.weight(0.4f))
            Text(
                user.dateAndTime.toPrettyString("MMM. d, yyyy - h:mm a"),
                style = TextStyle(
                    fontFamily = rethinkSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Start,
                    color = Color.Black
                ), modifier = Modifier.weight(4f)
            )
            Text(
                user.status.toString().toTitleCase(),
                style = TextStyle(
                    fontFamily = rethinkSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.End,
                    color = statusColor
                ), modifier = Modifier.weight(2f)
            )
        }
        HorizontalDivider(
            color = Color(0xFFCCCCCC),
            thickness = 0.5.dp
        )
    }
}