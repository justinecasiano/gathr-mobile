package com.example.gathr.presentation.shared

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.gathr.core.ui.ImageViewer
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.data.model.DepartmentType
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toAbbreviatedString
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime
import com.example.gathr.utils.toTitleCase
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEventScreen(
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
        val event = state.currentEvent ?: return
        var role = event.userRole
        var isRegistered =
            if (role == ParticipantType.ATTENDEE && event.isRegistered) true else false
        val isModerator = state.currentUser!!.role.name == "MODERATOR"

        Log.d("VIEW_EVENT", "User Role is $role")

        ViewEventContent(
            event = state.currentEvent!!,
            role = role,
            isRegistered = isRegistered,
            onIntent = viewModel::handleIntent,
            onNavigate = viewModel::sendMainEffect,
            isModerator = isModerator
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

            state.actionTitle == "Registration Cancelled" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = state.actionOnConfirm,
                )
            }

            state.actionTitle == "Cancel Registration" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = state.actionOnConfirm,
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }

            state.actionTitle == "Delete Event" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Continue",
                    onConfirmClicked = state.actionOnConfirm,
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    isModerator = isModerator
                )
            }

            state.actionTitle == "Event Deleted" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = state.actionOnConfirm,
                    isModerator = isModerator
                )
            }

            state.actionTitle == "Review Success" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = state.actionOnConfirm,
                    isModerator = isModerator
                )
            }

            state.actionTitle == "Approve Event" -> {
                val event = state.currentEvent!!
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    confirmButtonText = "Confirm",
                    cancelButtonText = "Cancel",
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    onConfirmClicked = {
                        viewModel.handleIntent(
                            UserIntent.ReviewEvent(
                                eventId = event.id,
                                status = EventApprovalStatus.APPROVED,
                                comment = ""
                            )
                        )
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                    },
                    isModerator = true
                )
            }

            state.actionTitle == "Reject Event" -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    confirmButtonText = "Confirm",
                    cancelButtonText = "Cancel",
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    onConfirmClicked = state.actionOnConfirm,
                    isModerator = true
                )
            }

            state.actionTitle == "Reason" -> {
                var selectedReason by remember { mutableStateOf("Inappropriate content") }
                var otherReasonText by remember { mutableStateOf("") }
                val event = state.currentEvent!!

                Alert(
                    title = "Reason",
                    confirmButtonText = "Confirm",
                    cancelButtonText = "Cancel",
                    isModerator = true,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    onConfirmClicked = {
                        val finalComment =
                            if (selectedReason == "Others") otherReasonText else selectedReason
                        viewModel.handleIntent(
                            UserIntent.ReviewEvent(
                                eventId = event.id,
                                status = EventApprovalStatus.REJECTED,
                                comment = finalComment
                            )
                        )
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                    },
                    content = {
                        RejectionReasonContent(
                            selectedReason = selectedReason,
                            onReasonSelected = { selectedReason = it },
                            otherText = otherReasonText,
                            onOtherTextChange = { otherReasonText = it }
                        )
                    }
                )
            }

            state.actionTitle.isNotBlank() -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Error" },
                    message = state.actionError.ifBlank { "" },
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    isModerator = isModerator
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEventContent(
    event: Event,
    role: ParticipantType,
    isRegistered: Boolean,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
    isModerator: Boolean = false
) {
    var selectedImage by remember { mutableStateOf("") }

    var buttonText: String = ""
    var buttonColor = Color(0xFF7B55A3)
    var buttonOutlineColor = Color(0xFF4C2576)
    var onButtonClick: () -> Unit = {}

    val isRemoved = event.isArchive
    val isUpcoming = event.startTime.isAfter(Instant.now())
    val isPastEnd = Instant.now().isAfter(event.endTime)

    val bottomBorderThickness = 5.dp
    val eventStatusColor =
        if (isUpcoming && !isRegistered) Color.Black.copy(0.8f) else Color(0xFF9FC090)
    var eventStatus = event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
    var eventDateTime = "${
        event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
    } | ${event.startTime.toSimpleTime()} to\n${event.endTime.toPrettyString(pattern = " MMM '.' d, yyyy").uppercase()
    } | ${event.endTime.toSimpleTime()}"

    if (!isModerator) {
        if (role == ParticipantType.ATTENDEE) {
            if (!isRegistered) {
                buttonText = "REGISTER"
                onButtonClick = {
                    onIntent(UserIntent.RegisterEvent)
                }
            } else {
                buttonText = "CANCEL"
                eventStatus = "REGISTERED"
                onButtonClick = {
                    onIntent(UserIntent.ActionTitleChanged("Cancel Registration"))
                    onIntent(UserIntent.ActionErrorChanged("Are you sure you want to cancel your registration?"))
                    onIntent(UserIntent.ActionOnConfirmClicked {
                        onIntent(UserIntent.CancelEvent)
                        onIntent(UserIntent.ActionOnClear)
                    })
                }
                buttonColor = Color(0xFFFC3436)
                buttonOutlineColor = Color(0xFF820006)
                eventDateTime = "${
                    event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
                } | ${event.startTime.toSimpleTime()} to\n${
                    event.endTime.toPrettyString(pattern = " MMM '.' d, yyyy").uppercase()
                } | ${event.endTime.toSimpleTime()}"
            }
        }

        if (!isRemoved && isRegistered) {
            if (Instant.now().isAfter(event.endTime)) {
                eventStatus = "EVENT ENDED"
                eventDateTime = "${
                    event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
                } | ${event.startTime.toSimpleTime()} to\n${
                    event.endTime.toPrettyString(pattern = " MMM '.' d, yyyy").uppercase()
                } | ${event.endTime.toSimpleTime()}"

                buttonText = "GIVE FEEDBACK"
                buttonColor = Color(0xFF7B55A3)
                buttonOutlineColor = Color(0xFF4C2576)
                onButtonClick = { onNavigate(MainEffect.NavigateFeedback) }
            } else if (Instant.now().isAfter(event.startTime)) {
                eventStatus = "ONGOING EVENT"
                eventDateTime = "${
                    event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase()
                } | ${event.startTime.toSimpleTime()} to\n${
                    event.endTime.toPrettyString(pattern = " MMM '.' d, yyyy").uppercase()
                } | ${event.endTime.toSimpleTime()}"

                buttonText = "SHOW QR"
                buttonColor = Color(0xFF7B986E)
                buttonOutlineColor = Color(0xFF3E6831)
                onButtonClick = { onNavigate(MainEffect.NavigateQrCode) }
            }
        }

        if (role == ParticipantType.ORGANIZER || role == ParticipantType.STAFF) {
            Log.d("INSIDE ORGANIZER OR STAFF", "User Role is $role")
            buttonText = "TRACK ATTENDANCE"
            buttonColor = Color(0xFF7B55A3)
            onButtonClick = { onNavigate(MainEffect.NavigateAttendance) }
        }
    }

    val showRegisterButton = when {
        event.isArchive -> false
        role == ParticipantType.ORGANIZER || role == ParticipantType.STAFF -> true
        role == ParticipantType.ATTENDEE -> {
            if (isRegistered) true
            else !isPastEnd
        }

        else -> false
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
                    val isOrganizer = role == ParticipantType.ORGANIZER
                    val isStaff = role == ParticipantType.STAFF
                    val isPending = event.status == EventApprovalStatus.PENDING
                    val isUpcoming = event.computedStatus == EventComputedStatus.UPCOMING
                    val isApproved = event.status == EventApprovalStatus.APPROVED
                    val isRejected = event.status == EventApprovalStatus.REJECTED
                    val isArchived = event.isArchive

                    val showMenu = when {
                        isOrganizer -> when {
                            isRemoved -> false // Archived/Deleted = No menu
                            isPending && !isUpcoming -> false // Pending + Ongoing/Ended = No menu
                            else -> true // Pending+Upcoming, Rejected, and all Approved show the menu
                        }
                        // Staff Rules
                        isStaff -> when {
                            isApproved && !isArchived -> true // Only show for approved events
                            else -> false // Pending/Rejected/Archived = No menu for staff
                        }

                        isModerator -> when {
                            isRemoved -> false
                            else -> true
                        }

                        else -> false
                    }

                    if (showMenu) {
                        ThreeDotMenu(event, role, isRemoved, onIntent, onNavigate, isModerator)
                    }
                })
        },
    ) { outerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .clickable { selectedImage = event.backgroundImage ?: "" },
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
            BottomScreenSheet(Modifier.padding(outerPadding), event, isModerator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isModerator) {
                            Modifier.background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFF312245), Color(0xFF7954AB))
                                ),
                            )
                        } else {
                            Modifier.background(Color.White)
                        }
                    )
                    .border(
                        1.2.dp,
                        color = if (isModerator) Color(0xFF261A36) else Color(0xFFD7D7D7)
                    )
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
                Box(modifier = Modifier.fillMaxWidth(if (isModerator) 0.4f else 0.49f)) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isModerator) Color.White else eventStatusColor
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
                            color = if (isModerator) Color.White else Color.Black.copy(alpha = 0.8f)
                        )
                    )
                }
                Spacer(Modifier.width(5.dp))
                if (event.status == EventApprovalStatus.APPROVED && showRegisterButton && !isModerator) {
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
                        buttonShape = RoundedCornerShape(20.dp),
                        bottomBorderThickness = bottomBorderThickness,
                        shouldAddShadow = true,
                    )
                }
                if (isModerator && event.status == EventApprovalStatus.PENDING && !event.isArchive) {
                    Row(Modifier.fillMaxWidth()) {
                        ElevatedButton(
                            text = "REJECT",
                            onClick = {
                                onIntent(UserIntent.ActionTitleChanged("Reject Event"))
                                onIntent(UserIntent.ActionErrorChanged("Are you sure you want to reject this event?"))
                                onIntent(UserIntent.ActionOnConfirmClicked {
                                    onIntent(UserIntent.ActionOnClear)
                                    onIntent(UserIntent.ActionTitleChanged("Reason"))
                                })
                            },
                            isEnabled = true,
                            shouldFill = false,
                            buttonColor = Color(0xFFFC3436),
                            outlineColor = Color(0xFF820006),
                            textStyle = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ),
                            buttonShape = RoundedCornerShape(20.dp),
                            bottomBorderThickness = bottomBorderThickness,
                            shouldAddShadow = true,
                        )
                        Spacer(Modifier.width(5.dp))
                        ElevatedButton(
                            text = "APPROVE",
                            onClick = {
                                onIntent(UserIntent.ActionTitleChanged("Approve Event"))
                                onIntent(UserIntent.ActionErrorChanged("Are you sure you want to approve this event?"))
                            },
                            isEnabled = true,
                            shouldFill = false,
                            buttonColor = Color(0xFF9FC090),
                            outlineColor = Color(0xFF61924B),
                            textStyle = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            ),
                            buttonShape = RoundedCornerShape(20.dp),
                            bottomBorderThickness = bottomBorderThickness,
                            shouldAddShadow = true,
                        )
                    }
                }
            }
            if (selectedImage.isNotBlank())
                ImageViewer(selectedImage, onDismiss = { selectedImage = "" })
        }
    }
}

@Composable
fun ThreeDotMenu(
    event: Event,
    role: ParticipantType,
    isRemoved: Boolean = false,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
    isModerator: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val isOrganizer = role == ParticipantType.ORGANIZER
    val isApproved = event.status == EventApprovalStatus.APPROVED
    val isUpcoming = event.computedStatus == EventComputedStatus.UPCOMING
    val isRejected = event.status == EventApprovalStatus.REJECTED
    val isPending = event.status == EventApprovalStatus.PENDING

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
                val canSeeAttendance = !isRemoved && !isRejected && isApproved &&
                        (isOrganizer || role == ParticipantType.STAFF || isModerator)
                if (canSeeAttendance) {
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
                                    "See Attendance", style = TextStyle(
                                        fontFamily = AppFonts.rethinkSans,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.White
                                    )
                                )
                            }
                        })
                }
                if (isOrganizer && !isRemoved) {
                    val canModify =
                        (isPending && isUpcoming) || isRejected || (isApproved && isUpcoming)

                    if (canModify) {
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

                if ((isOrganizer || isModerator) && !isRemoved) {
                    val canDelete =
                        (event.status == EventApprovalStatus.PENDING && isUpcoming) ||
                                isRejected ||
                                (isApproved && isUpcoming)

                    if (canDelete) {
                        DropdownMenuItem(onClick = {
                            onIntent(UserIntent.ActionTitleChanged("Delete Event"))
                            onIntent(UserIntent.ActionErrorChanged("Are you sure you want to delete this event?"))
                            onIntent(UserIntent.ActionOnConfirmClicked {
                                expanded = false
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
                }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomScreenSheet(modifier: Modifier = Modifier, event: Event, isModerator: Boolean = false) {
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
                    .then(
                        if (isModerator) {
                            Modifier.background(
                                brush = Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0.0f to Color(0xFF7954AB),
                                        0.43f to Color(0xFF312245),
                                        1.0f to Color(0xFF312245)
                                    )
                                )
                            )
                        } else {
                            Modifier.background(Color.White)
                        }
                    )
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
                    color = if (isModerator) Color.White else Color.Black,
                )
                Text(
                    event.title.uppercase(),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isModerator) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
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
                            append(event.computedStatus.toString().uppercase())
                        }
                    },
                )
                Text(
                    event.location,
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        color = if (isModerator) Color.White else Color.Black,
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    "${event.startTime.toPrettyString("MMMM d, yyyy")} | ${event.startTime.toSimpleTime()} to\n${event.endTime.toPrettyString("MMMM d, yyyy")} | ${event.endTime.toSimpleTime()}",
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 12.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        color = if (isModerator) Color.White else Color.Black,
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
                                    color = if (isModerator) Color.White else Color.Black,
                                )
                            ) {
                                append("${event.capacity.toAbbreviatedString()}\n")
                            }
                            append("Capacity")
                        }, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            color = if (isModerator) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                        ), modifier = Modifier.weight(2.8f)
                    )
                    VerticalDivider(Modifier.height(40.dp), color = Color(0xFFDEDEDE))
                    Box(
                        modifier = Modifier.weight(4.4f), contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.height(36.dp),
                            painter = painterResource(if (isModerator) R.drawable.highlight_event_moderator else R.drawable.highlight_event),
                            contentScale = ContentScale.FillHeight,
                            contentDescription = "Event Highlight Image"
                        )
                        Text(
                            "Significant\nEvent", style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 14.sp,
                                lineHeight = 13.sp,
                                textAlign = TextAlign.Center,
                                color = if (isModerator) Color.White else Color.Black,
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
                                    color = if (isModerator) Color(0xFFFC3436) else Color(0xFF820006)
                                )
                            ) {
                                append("${event.remainingSlots.toAbbreviatedString()}\n")
                            }
                            append("Slots Left")
                        }, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            color = if (isModerator) Color.White else Color.Black,
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(2.dp))
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.profile_icon),
                            tint = if (isModerator) Color.White else Color.Black,
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
                                        color = if (isModerator) Color.White else Color.Black,
                                    )
                                ) {
                                    append("Organizer\n")
                                }
                                append("@${event.organizerDisplayName} | ${event.organizerName.toTitleCase()}")
                            },
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Start,
                                color = if (isModerator) Color.White else Color.Black,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(R.drawable.attendees),
                            tint = if (isModerator) Color.White else Color.Black,
                            contentDescription = "Departments Icon"
                        )
                        Spacer(Modifier.width(20.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isModerator) Color.White else Color.Black,
                                    )
                                ) {
                                    append("Who can join?\n")
                                }
                                val allowedDepts = event.allowedDepartments ?: emptyList()
                                val alumniSuffix = if (event.allowAlumni) " and Alumni" else ""

                                val displayText = when {
                                    allowedDepts.contains(DepartmentType.ALL) -> {
                                        "Everyone can join${if (event.allowAlumni) " (including Alumni)" else ""}"
                                    }

                                    allowedDepts.isEmpty() -> {
                                        if (event.allowAlumni) "Alumni only" else "No departments specified"
                                    }

                                    else -> {
                                        val deptsString =
                                            allowedDepts.joinToString(separator = ", ") { it.name }
                                        "$deptsString$alumniSuffix"
                                    }
                                }

                                append(displayText)
                            },
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Start,
                                color = if (isModerator) Color.White else Color.Black,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(R.drawable.description),
                            tint = if (isModerator) Color.White else Color.Black,
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
                                        color = if (isModerator) Color.White else Color.Black,
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
                                color = if (isModerator) Color.White else Color.Black,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    if (event.status == EventApprovalStatus.REJECTED)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                modifier = Modifier.size(30.dp),
                                painter = painterResource(R.drawable.clear),
                                tint = if (isModerator) Color.White else Color.Black,
                                contentDescription = "Comment Icon"
                            )
                            Spacer(Modifier.width(20.dp))
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = AppFonts.instrumentSans,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isModerator) Color.White else Color.Black,
                                        )
                                    ) {
                                        append("Moderator's Comment\n")
                                    }
                                    val content =
                                        if (event.comment !== null) event.comment else "No specific reason provided."
                                    append(content)
                                },
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    textAlign = TextAlign.Start,
                                    color = if (isModerator) Color.White else Color.Black,
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

@Composable
fun RejectionReasonContent(
    selectedReason: String,
    onReasonSelected: (String) -> Unit,
    otherText: String,
    onOtherTextChange: (String) -> Unit
) {
    val reasons = listOf("Inappropriate content", "Policy violation", "Others")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        reasons.forEach { reason ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onReasonSelected(reason) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedReason == reason),
                    onClick = { onReasonSelected(reason) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White.copy(alpha = 0.6f)
                    )
                )
                Spacer(Modifier.width(8.dp))
                if (reason == "Others") {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = "Others (specify)",
                            color = Color.White,
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 14.sp
                        )
                        TextField(
                            value = otherText,
                            onValueChange = onOtherTextChange,
                            enabled = selectedReason == "Others",
                            placeholder = {
                                Text(
                                    "Enter reason...",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,

                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                disabledTextColor = Color.White.copy(alpha = 0.5f),

                                focusedIndicatorColor = Color.White,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(fontSize = 14.sp)
                        )
                    }
                } else {
                    Text(
                        text = reason,
                        color = Color.White,
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}