package com.example.gathr.presentation.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
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
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.Event
import com.example.gathr.presentation.participant.AddStaffContent
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime
import com.example.gathr.utils.toTitleCase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEventScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNavigateAttendance: () -> Unit,
    onNavigateEdit: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                UserEffect.NavigateToNext -> {}
                UserEffect.NavigateBack -> onNavigateBack()
                UserEffect.NavigateLogout -> {}
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ViewEventContent(
            state = state,
            onIntent = viewModel::handleIntent,
            onNavigateAttendance = onNavigateAttendance,
            onNavigateEdit = onNavigateEdit
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
fun ViewEventContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigateAttendance: () -> Unit,
    onNavigateEdit: () -> Unit
) {
    val isOrganizer = state.currentEvent?.createdBy == state.currentUser?.id
    val event: Event = state.currentEvent!!

    var onButtonClick: () -> Unit = {}
    var buttonText: String = ""

    if (isOrganizer) {
        buttonText = "TRACK ATTENDANCE"
        onButtonClick = {}
    } else {
        buttonText = "REGISTER"
        onButtonClick = {}
    }
    Log.d("CREATE_EVENT", "VIEW_EVENT: image - ${event.backgroundImage}")

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
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
                ThreeDotMenu(onIntent, onNavigateAttendance, onNavigateEdit)
            })
    }, bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(2.dp, color = Color(0xFFD7D7D7))
                .padding(top = 20.dp, start = 25.dp, end = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(6.5f)) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        ) {
                            append(
                                event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy")
                                    .uppercase()
                            )
                        }
                        append("${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}")
                    }, style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
            ElevatedButton(
                text = buttonText,
                onClick = onButtonClick,
                isEnabled = true,
                shouldFill = false,
                buttonColor = Color(0xFF7B55A3),
                outlineColor = Color(0xFF4C2576),
                textStyle = TextStyle(
                    fontFamily = AppFonts.instrumentSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
                isContrast = false,
                buttonShape = RoundedCornerShape(15.dp),
                shouldAddShadow = false,
            )
        }
    }) { outerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                modifier = Modifier.fillMaxHeight(0.6f),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.backgroundImage)
                    .crossfade(true)
                    .listener(
                        onStart = { request -> Log.d("IMAGE_LOAD", "Image started loading") },
                        onError = { request, result ->
                            Log.e(
                                "IMAGE_LOAD",
                                "FAILED: ${result.throwable.message}"
                            )
                        }
                    )
                    .build(),
                contentDescription = "Event Card Image",
                contentScale = ContentScale.Crop
            )
            BottomScreenSheet(Modifier.padding(outerPadding), event)
        }
    }
}

@Composable
fun ThreeDotMenu(
    onIntent: (UserIntent) -> Unit,
    onNavigateAttendance: () -> Unit,
    onNavigateEdit: () -> Unit
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
                modifier = Modifier.background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF7B55A3), Color(0xFF583181)),
                        start = Offset(0f, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                ), expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(onClick = { onNavigateAttendance() }, text = {
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
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    color = Color.Black,
                    thickness = 0.8.dp
                )
                DropdownMenuItem(onClick = {
//                    onIntent(UserIntent.DeleteEvent)
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
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    color = Color.Black,
                    thickness = 0.8.dp
                )
                DropdownMenuItem(onClick = { onNavigateEdit() }, text = {
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
                })
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
                    event.title.uppercase(),
                    style = TextStyle(
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
                                color = Color(0xFFF6835E)
                            )
                        ) {
                            append("${event.computedStatus.toString().uppercase()}\n")
                        }
                        append("${event.startTime.toPrettyString("MMMM d, yyyy")} | ${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}")
                        append(event.location)
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
                                    fontWeight = FontWeight.Bold,
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
                            modifier = Modifier.height(35.dp),
                            painter = painterResource(R.drawable.highlight_event),
                            contentScale = ContentScale.FillHeight,
                            contentDescription = "Event Highlight Image"
                        )
                    }
                    VerticalDivider(Modifier.height(40.dp), color = Color(0xFFDEDEDE))
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
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
        sheetDragHandle = {
        },
    ) { innerPadding ->
    }
}

//@Preview
//@Composable
//private fun ViewEventScreenPreview() {
//    ViewEventScreen()
//}