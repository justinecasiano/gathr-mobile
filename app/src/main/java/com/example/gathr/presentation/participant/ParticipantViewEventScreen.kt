package com.example.gathr.presentation.participant

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.dummyEvents
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime
import com.example.gathr.utils.toTitleCase
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantViewEventScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit,
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
        var role = "ATTENDEE"
        var isRegistered = false
        val event = state.currentEvent!!

        if (event.isOrganizer) role = "ORGANIZER"
        else if (event.isStaff) role = "STAFF"

        if (role == "ATTENDEE" && event.isRegistered) isRegistered = true

        Log.d("CREATE_EVENT", "Organizer: ${event.isOrganizer}")

        ParticipantViewEventContent(
            event = state.currentEvent!!,
            role = role,
            isRegistered = isRegistered,
            onIntent = viewModel::handleIntent,
            onNavigate = viewModel::sendMainEffect
        )
        if (state.isLoading) {
            LoadingOverlay()
        }
        when {
            state.actionTitle == "Registration Successful" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = state.actionOnConfirm,
                )
            }

            state.actionTitle == "Cancel Registration" ||
                    state.actionTitle == "Delete Event" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Continue",
                    onConfirmClicked = state.actionOnConfirm,
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }

            state.actionTitle.isNotBlank() -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Error" },
                    message = state.actionError.ifBlank { "" },
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantViewEventContent(
    event: Event,
    role: String,
    isRegistered: Boolean,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    var buttonText: String = ""
    var buttonColor = Color(0xFF7B55A3)
    var buttonOutlineColor = Color(0xFF4C2576)
    var onButtonClick: () -> Unit = {}

    val isRejectedOrRemoved = event.isArchive || event.status == EventApprovalStatus.REJECTED
    val isUpcoming = event.startTime.isAfter(Instant.now())
    var bottomBorderThickness = 5.dp
    val eventStatusColor =
        if (isUpcoming && !isRegistered) Color.Black.copy(0.8f) else Color(0xFF9FC090)
    var eventStatus = event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
    var eventDateTime = "${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}"

    if (role == "ATTENDEE") {
        if (!isRegistered) {
            buttonText = "REGISTER"
            onButtonClick = {
                onIntent(UserIntent.IsLoadingChanged(true))
                onIntent(UserIntent.RegisterEvent)
            }
        } else {
            buttonText = "CANCEL"
            eventStatus = "REGISTERED"
            onButtonClick = {
                onIntent(UserIntent.ActionTitleChanged("Cancel Registration"))
                onIntent(UserIntent.ActionErrorChanged("Are you sure you want to cancel your registration?"))
                onIntent(UserIntent.ActionOnConfirmClicked {
                    onIntent(UserIntent.IsLoadingChanged(true))
                    onIntent(UserIntent.CancelEvent)
                })
            }
            buttonColor = Color(0xFFFC3436)
            buttonOutlineColor = Color(0xFF820006)
            eventDateTime = "${
                event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
            } | ${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}"
        }
    }

    if (!isRejectedOrRemoved && isRegistered) {
        if (Instant.now().isAfter(event.endTime)) {
            eventStatus = "COMPLETED EVENT"
            eventDateTime = "${
                event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
            } | ${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}"

            buttonText = "GIVE FEEDBACK"
            buttonColor = Color(0xFF7B55A3)
            bottomBorderThickness = 0.dp
            onButtonClick = {}
        } else if (Instant.now().isAfter(event.startTime)) {
            eventStatus = "ONGOING EVENT"
            eventDateTime = "${
                event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
            } | ${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}"

            buttonText = "SHOW QR"
            buttonColor = Color(0xFF7B986E)
            bottomBorderThickness = 0.dp
            onButtonClick = { onNavigate(MainEffect.NavigateQrCode) }
        }
    }

    if (role == "ORGANIZER" || role == "STAFF") {
        buttonText = "TRACK ATTENDANCE"
        buttonColor = Color(0xFF7B55A3)
        bottomBorderThickness = 0.dp
        onButtonClick = { onNavigate(MainEffect.NavigateAttendance) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(top = 10.dp, start = 20.dp, end = 20.dp),
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    Surface(
                        onClick = { onIntent(UserIntent.BackClicked) },
                        shape = CircleShape,
                        color = Color(0xFFD9D9D9).copy(alpha = 0.76f),
                        modifier = Modifier.size(35.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.back_outline),
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                },
                actions = {
                    if (role != "ATTENDEE")
                        ThreeDotMenu(role, isRejectedOrRemoved, onIntent, onNavigate)
                })
        },
    ) { outerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                modifier = Modifier.fillMaxHeight(0.6f),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.backgroundImage)
                    .fallback(R.drawable.placeholder_landscape)
                    .crossfade(true).listener(
                        onStart = { request -> Log.d("IMAGE_LOAD", "Image started loading") },
                        onError = { request, result ->
                            Log.e(
                                "IMAGE_LOAD", "FAILED: ${result.throwable.message}"
                            )
                        }).build(),
                contentDescription = "Event Card Image",
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.placeholder_landscape)
            )
            BottomScreenSheet(Modifier.padding(outerPadding), event)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .border(1.2.dp, color = Color(0xFFD7D7D7))
                    .padding(
                        top = 20.dp,
                        bottom = outerPadding.calculateBottomPadding(),
                        start = 20.dp,
                        end = 20.dp
                    )
                    .align(Alignment.BottomEnd),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(modifier = Modifier.fillMaxWidth(0.49f)) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = eventStatusColor
                                )
                            ) {
                                append("${eventStatus}\n")
                            }
                            append(eventDateTime)
                        }, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    )
                }
                Spacer(Modifier.width(5.dp))
                if (event.status == EventApprovalStatus.APPROVED) {
                    ElevatedButton(
                        text = buttonText,
                        onClick = onButtonClick,
                        isEnabled = true,
                        shouldFill = true,
                        buttonColor = buttonColor,
                        outlineColor = buttonOutlineColor,
                        textStyle = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                        buttonShape = RoundedCornerShape(17.dp),
                        bottomBorderThickness = bottomBorderThickness,
                        shouldAddShadow = true,
                    )
                }
            }
        }
    }
}

@Composable
fun ThreeDotMenu(
    role: String,
    isRejectedOrRemoved: Boolean = false,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        Column(verticalArrangement = Arrangement.Top) {
            Surface(
                onClick = { expanded = !expanded },
                shape = CircleShape,
                color = Color(0xFFD9D9D9).copy(alpha = 0.76f),
                modifier = Modifier.size(35.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.option_outline),
                        contentDescription = "Option menu",
                        tint = Color.Black
                    )
                }
            }
            DropdownMenu(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF7B55A3), Color(0xFF583181)),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    ),
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onNavigate(MainEffect.NavigateAttendance)
                    },
                    text = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.attendees),
                                contentDescription = "Attendees",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "See who registered", style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                            )
                        }
                    })
                if (role == "ORGANIZER" || role == "MODERATOR") {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 15.dp),
                        color = Color.Black,
                        thickness = 0.8.dp
                    )
                    DropdownMenuItem(onClick = {
                        onIntent(UserIntent.ActionTitleChanged("Delete Event"))
                        onIntent(UserIntent.ActionErrorChanged("Are you sure you want to delete this event?"))
                        onIntent(UserIntent.ActionOnConfirmClicked {
                            onIntent(UserIntent.IsLoadingChanged(true))
                            onIntent(UserIntent.ActionOnClear)
                            onIntent(UserIntent.DeleteEvent)
                        })
                    }, text = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.delete),
                                contentDescription = "Delete Event",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Delete Event", style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                            )
                        }
                    })
                }

                if (role == "ORGANIZER" && !isRejectedOrRemoved) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 15.dp),
                        color = Color.Black,
                        thickness = 0.8.dp
                    )
                    DropdownMenuItem(
                        onClick = {
                            expanded = false
                            onNavigate(MainEffect.NavigateUpdateEvent)
                        },
                        text = {
                            Row {
                                Icon(
                                    painter = painterResource(R.drawable.edit),
                                    contentDescription = "Edit Event",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Edit Event", style = TextStyle(
                                        fontFamily = AppFonts.rethinkSans,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White
                                    )
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomScreenSheet(modifier: Modifier = Modifier, event: Event) {
    val density = LocalDensity.current

    val halfScreenHeightInPixels = LocalWindowInfo.current.containerSize.height / 2

    val peekHeight: Dp = with(density) {
        halfScreenHeightInPixels.toDp()
    }

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded, skipHiddenState = true
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val isUpcoming = event.startTime.isAfter(Instant.now())

    BottomSheetScaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        sheetShape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = peekHeight)
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 36.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(
                    Modifier
                        .width(80.dp)
                        .clip(RoundedCornerShape(7.5.dp))
                        .align(Alignment.CenterHorizontally),
                    thickness = 5.dp,
                    color = Color.Black,
                )
                Text(
                    event.title.uppercase(), style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isUpcoming) Color(0xFFF6835E) else Color(0xFF9FC090)
                            )
                        ) {
                            append("${event.computedStatus.toString().uppercase()}\n")
                        }
                        append("${event.location}\n")
                        append("${event.startTime.toPrettyString("MMMM d, yyyy")} | ${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}")
                    },
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 12.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                    ),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.Black
                                )
                            ) {
                                append("${event.capacity}\n")
                            }
                            append("Capacity")
                        }, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        ), modifier = Modifier.weight(2.8f)
                    )
                    VerticalDivider(Modifier.height(40.dp), color = Color(0xFFDEDEDE))
                    Box(
                        modifier = Modifier.weight(4.4f), contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.height(36.dp),
                            painter = painterResource(R.drawable.highlight_event),
                            contentScale = ContentScale.FillHeight,
                            contentDescription = "Event Highlight Image"
                        )
                        Text(
                            "Significant\nEvent", style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 14.sp,
                                lineHeight = 13.sp,
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        )
                    }
                    VerticalDivider(Modifier.height(40.dp), color = Color(0xFFDEDEDE))
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF820006)
                                )
                            ) {
                                append("${event.remainingSlots}\n")
                            }
                            append("Slots Left")
                        }, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        ), modifier = Modifier.weight(2.8f)
                    )
                }
                Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start
                ) {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.81.dp)
                    Spacer(Modifier.height(15.dp))
                    Row {
                        Spacer(Modifier.width(2.dp))
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.profile_icon),
                            tint = Color.Black,
                            contentDescription = "Profile Icon"
                        )
                        Spacer(Modifier.width(20.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                ) {
                                    append("Organizer\n")
                                }
                                append(event.createdByName.toTitleCase())
                            },
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    Row {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(R.drawable.description),
                            tint = Color.Black,
                            contentDescription = "Description Icon"
                        )
                        Spacer(Modifier.width(20.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                ) {
                                    append("Description\n")
                                }
                                append(event.description)
                            },
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                }
            }
        },
        sheetDragHandle = {},
    ) { innerPadding ->
    }
}