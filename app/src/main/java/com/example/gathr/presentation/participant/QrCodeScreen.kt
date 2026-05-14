package com.example.gathr.presentation.participant

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.drawToBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.CaptureComposable
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.core.ui.LocalCaptureTrigger
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.ParticipantStatus
import com.example.gathr.data.model.ResponseStatus
import com.example.gathr.data.model.User
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.Utils
import com.example.gathr.utils.Utils.generateQrBitmap
import com.example.gathr.utils.Utils.saveBitmapToGallery
import com.example.gathr.utils.dummyEvents
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime
import com.example.gathr.utils.toTitleCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.Instant
import java.util.UUID

@Composable
fun QrCodeScreen(
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
        QrCodeContent(
            state = state,
            onIntent = viewModel::handleIntent,
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

            (state.currentEvent!!).participantStatus == ParticipantStatus.CHECKED_IN -> {
                Alert(
                    title = "Invalid Ticket",
                    message = "This QR code has already been scanned for entry.",
                    onDismissRequest = {
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                        onNavigateBack()
                    },
                    confirmButtonText = "Go back",
                    onConfirmClicked = {
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                        onNavigateBack()
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeContent(state: UserState, onIntent: (UserIntent) -> Unit) {
    val user: User = state.currentUser!!
    val event: Event = state.currentEvent!!

    val context = LocalContext.current
    var captureFunction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val scope = rememberCoroutineScope()

    val activity = context as? Activity
    DisposableEffect(Unit) {
        val params = activity?.window?.attributes
        val originalBrightness = params?.screenBrightness ?: -1f
        params?.screenBrightness = 1f
        activity?.window?.attributes = params
        onDispose {
            params?.screenBrightness = originalBrightness
            activity?.window?.attributes = params
        }
    }

    Scaffold(topBar = {
        Box(Modifier.background(Color(0xFFF6F6F6))) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "QR Code",
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                        ),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    //                containerColor = Color(0xFFF6F6F6)
                    containerColor = Color.Transparent
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
            Row(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .offset(y = (15).dp)
                    .padding(bottom = 10.dp)
                    .padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.calendar_icon),
                    contentDescription = "Event name",
                    tint = Color(0xFF5D5D5D),
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    event.title.toTitleCase(),
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF5D5D5D),
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
        }
    }
    ) { paddingValues ->
        Column(Modifier.fillMaxHeight()) {
            Column(
                Modifier
                    .fillMaxHeight(0.84f)
                    .background(Color(0xFFF0F0F0))
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                    )
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
                            .fillMaxHeight(0.15f)
                            .background(Color(0xFFF6F6F6)),
                    )
                    LazyColumn(Modifier.padding(top = 10.dp)) {
                        item {
                            Spacer(Modifier.height(5.dp))
                            CaptureComposable(
                                modifier = Modifier,
                                onTriggerProvided = { function -> captureFunction = function },
                                onBitmapCaptured = { bitmap ->
                                    saveBitmapToGallery(
                                        context,
                                        bitmap,
                                        "${event.title}_${event.startTime.toPrettyString()}"
                                    )
                                }
                            ) {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 30.dp)
                                ) {
                                    Image(
                                        modifier = Modifier.size(630.dp),
                                        painter = painterResource(R.drawable.ticket_holder),
                                        contentDescription = "QR Code ticket",
                                        contentScale = ContentScale.FillBounds
                                    )
                                    Column(
                                        modifier = Modifier
                                            .size(630.dp)
                                            .padding(vertical = 40.dp, horizontal = 40.dp)
                                    ) {
                                        Column(Modifier.weight(3f)) {
                                            Text(
                                                buildAnnotatedString {
                                                    append("Attendee\n")
                                                    withStyle(
                                                        style = SpanStyle(
                                                            fontFamily = AppFonts.rethinkSans,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    ) {
                                                        append("${user.firstName.toTitleCase()} ${user.lastName.toTitleCase()}")
                                                    }
                                                }, style = TextStyle(
                                                    fontFamily = AppFonts.rethinkSans,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    textAlign = TextAlign.Start,
                                                    color = Color.White.copy(alpha = 0.8f)
                                                ), maxLines = 3, overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(Modifier.height(20.dp))
                                            Text(
                                                buildAnnotatedString {
                                                    append("Date & Time\n")
                                                    withStyle(
                                                        style = SpanStyle(
                                                            fontFamily = AppFonts.rethinkSans,
                                                            fontSize = 16.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    ) {
                                                        append(
                                                            "${
                                                                event.startTime.toPrettyString(
                                                                    pattern = "MMM'.' d, yyyy"
                                                                )
                                                            } | ${event.startTime.toSimpleTime()} to\n" +
                                                                    "${
                                                                        event.endTime.toPrettyString(
                                                                            pattern = "MMM'.' d, yyyy"
                                                                        )
                                                                    } | ${event.endTime.toSimpleTime()}"
                                                        )
                                                    }
                                                }, style = TextStyle(
                                                    fontFamily = AppFonts.rethinkSans,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White.copy(alpha = 0.8f)
                                                ), modifier = Modifier.weight(1.2f, fill = false)
                                            )
                                        }
                                        Box(
                                            Modifier
                                                .padding(top = 50.dp)
                                                .weight(7.4f)
                                                .border(
                                                    width = 5.dp,
                                                    color = Color(0xFFF7906E),
                                                    shape = RoundedCornerShape(20.dp)
                                                )
                                                .clip(RoundedCornerShape(20.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val securePayload = remember(user.id, event.id) {
                                                Utils.generateSignedPayload(user.id.toString(), event.id)
                                            }
                                            QrCodeDisplay(securePayload)
                                        }
                                        Column(
                                            Modifier
                                                .weight(3.5f)
                                                .fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Spacer(Modifier.height(20.dp))
                                            Text(
                                                event.title.toTitleCase(),
                                                style = TextStyle(
                                                    fontFamily = AppFonts.rethinkSans,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    lineHeight = 17.sp,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                                ),
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Spacer(Modifier.height(10.dp))
                                            Text(
                                                text = event.location.toTitleCase(),
                                                style = TextStyle(
                                                    fontFamily = AppFonts.rethinkSans,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    textAlign = TextAlign.Center
                                                ),
                                                maxLines = 3,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(Modifier.background(Color.White)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .border(2.dp, color = Color(0xFFD7D7D7))
                        .padding(horizontal = 55.dp)
                        .padding(top = 20.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedButton(
                        text = "DOWNLOAD",
                        onClick = {
                            scope.launch {
                                onIntent(UserIntent.IsLoadingChanged(true))
                                captureFunction?.invoke()
                                delay(500)
                                onIntent(UserIntent.IsLoadingChanged(false))
                            }
                        },
                        isEnabled = captureFunction != null,
                        buttonColor = Color(0xFF7B55A3),
                        outlineColor = Color(0xFF4C2576),
                        textStyle = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                        isContrast = false,
                        buttonShape = RoundedCornerShape(20.dp),
                        shouldAddShadow = false,
                    )
                }
            }
        }
    }
}

@Composable
fun QrCodeDisplay(text: String) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(text) {
        val bitmap = withContext(Dispatchers.Default) {
            generateQrBitmap(text, margin = 3)
        }
        qrBitmap = bitmap
    }

    qrBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
    }
}