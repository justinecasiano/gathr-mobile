package com.example.gathr.presentation.participant

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.tooling.preview.Preview
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.ClearTextField
import com.example.gathr.core.ui.CustomTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@Composable
fun ModifyEventScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                UserEffect.NavigateToNext -> onNavigateNext()
                UserEffect.NavigateBack -> onNavigateBack()
                UserEffect.NavigateLogout -> {}
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        ModifyEventContent(
            state = state,
            onIntent = viewModel::handleIntent,
        )
        if (state.isLoading) {
            LoadingOverlay()
        }
        if (state.actionError.isNotBlank()) {
            Alert(
                title = "Error",
                message = state.actionError,
                onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                confirmButtonText = "Ok",
                onConfirmClicked = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ModifyEventContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit
) {
    val event = state.createEvent
    val hasFilledOut =
        !event.backgroundImage.isNullOrBlank() && event.title.isNotBlank() && event.description.isNotBlank() && event.capacity != null
                && event.location.isNotBlank() && event.startDateAndTime != event.endDateAndTime
    var submittedOnce by remember { mutableStateOf(false) }
    val errors = state.createEventValidationState

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
//                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onIntent(UserIntent.BackClicked) }) {
                            Icon(
                                modifier = Modifier.size(37.dp),
                                tint = Color.Black,
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        if (state.isUpdateEvent)
                            TextButton(
                                onClick = { onIntent(UserIntent.SaveModifiedEvent) },
                                enabled = false,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.Black,
                                    disabledContentColor = Color.Black.copy(0.5f)
                                )
                            ) {
                                Text(
                                    "Save",
                                    style = TextStyle(
                                        fontFamily = AppFonts.rethinkSans,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    ),
                                )
                            }
                    }
                )
            },
        ) { paddingValues ->
            Column(Modifier.fillMaxHeight()) {
                Column(
                    Modifier
                        .fillMaxHeight(0.84f)
                        .background(Color(0xFFF0F0F0))
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                            end = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        )
                        .padding(horizontal = 30.dp)
                ) {
                    val labelStyle = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    )

                    val scrollState = rememberScrollState()
                    Column(
                        Modifier.verticalScroll(scrollState)
                    ) {
                        Text(
                            if (state.isUpdateEvent) "Update Event" else "Create an Event",
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start,
                                color = Color.Black
                            ),
                        )
                        Spacer(Modifier.height(5.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                        ) {
                            Image(
                                modifier = Modifier.height(110.dp),
                                painter = painterResource(R.drawable.create_event_banner),
                                contentDescription = "Event Banner",
                                contentScale = ContentScale.FillHeight
                            )
                            Text(
                                "Events will be reviewed by the moderator",
                                style = TextStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Start,
                                    color = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth(0.65f)
                                    .align(Alignment.CenterStart)
                                    .padding(start = 15.dp, top = 15.dp)
                            )
                        }
                        Spacer(Modifier.height(10.dp))

                        val context = LocalContext.current
                        PhotoUploadComposable(
                            selectedImageUriString = state.createEvent.backgroundImage,
                            onImageSelected = { uriString ->
                                if (uriString != null) {
                                    val size = Utils.getFileSize(context, uriString)
                                    onIntent(
                                        UserIntent.CreateEventChanged(
                                            state.createEvent.copy(
                                                backgroundImage = uriString,
                                                backgroundImageSizeBytes = size
                                            )
                                        )
                                    )
                                }
                            }
                        )
                        Spacer(Modifier.height(2.dp))
                        if (errors.backgroundImageError.isNotBlank()) {
                            Text(
                                errors.backgroundImageError,
                                textAlign = TextAlign.Start,
                                style = TextStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                ),
                                color = AppColors.error
                            )
                        }
                        Spacer(Modifier.height(15.dp))
                        Text(
                            "Event Title",
                            style = labelStyle
                        )
                        Spacer(Modifier.height(5.dp))
                        ClearTextField(
                            text = state.createEvent.title,
                            isError = if (!submittedOnce) null else errors.titleError.isNotBlank(),
                            supportingText = errors.titleError,
                            onValueChange = {
                                onIntent(
                                    UserIntent.CreateEventChanged(
                                        state.createEvent.copy(
                                            title = it
                                        )
                                    )
                                )
                            },
                            onClick = {
                                onIntent(
                                    UserIntent.CreateEventChanged(
                                        state.createEvent.copy(
                                            title = ""
                                        )
                                    )
                                )
                            },
                            labelText = "Event Title",
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            outlineColor = Color(0xFF777777),
                            iconColor = Color(0xFF3C3C3C)
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            "Description",
                            style = labelStyle
                        )
                        Spacer(Modifier.height(5.dp))
                        Box(Modifier.fillMaxWidth()) {
                            CustomTextField(
                                text = state.createEvent.description,
                                minLines = 3,
                                maxLines = 5,
                                isError = if (!submittedOnce) null else errors.descriptionError.isNotBlank(),
                                supportingText = errors.descriptionError,
                                onValueChange = {
                                    onIntent(
                                        UserIntent.CreateEventChanged(
                                            state.createEvent.copy(
                                                description = it
                                            )
                                        )
                                    )
                                },
                                labelText = "Tell us more about your event",
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                outlineColor = Color(0xFF777777),
                                iconButton = { Spacer(Modifier.width(20.dp)) }
                            )
                            if (state.createEvent.description.isNotBlank())
                                IconButton(
                                    onClick = {
                                        onIntent(
                                            UserIntent.CreateEventChanged(
                                                state.createEvent.copy(
                                                    description = ""
                                                )
                                            )
                                        )
                                    },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 8.dp, end = 4.dp)
                                ) {
                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        painter = painterResource(R.drawable.clear),
                                        contentDescription = "Clear Input Text",
                                        tint = Color(0xFF3C3C3C),
                                    )
                                }
                        }
                        Spacer(Modifier.height(15.dp))
                        Text(
                            "Max Participants",
                            style = labelStyle
                        )
                        Spacer(Modifier.height(5.dp))

                        val capacity = state.createEvent.capacity
                        CustomTextField(
                            text = capacity?.toString() ?: "",
                            isError = if (!submittedOnce) null else errors.capacityError.isNotBlank(),
                            supportingText = errors.capacityError,
                            onValueChange = {
                                onIntent(
                                    UserIntent.CreateEventChanged(
                                        state.createEvent.copy(
                                            capacity = if (it.all { char -> char.isDigit() }) {
                                                it.toIntOrNull()
                                            } else null
                                        )
                                    )
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            labelText = "Max Participants",
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            outlineColor = Color(0xFF777777),
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            "Location",
                            style = labelStyle
                        )
                        Spacer(Modifier.height(5.dp))
                        ClearTextField(
                            text = state.createEvent.location,
                            isError = if (!submittedOnce) null else errors.locationError.isNotBlank(),
                            supportingText = errors.locationError,
                            onValueChange = {
                                onIntent(
                                    UserIntent.CreateEventChanged(
                                        state.createEvent.copy(
                                            location = it
                                        )
                                    )
                                )
                            },
                            onClick = {
                                onIntent(
                                    UserIntent.CreateEventChanged(
                                        state.createEvent.copy(
                                            location = ""
                                        )
                                    )
                                )
                            },
                            labelText = "Location",
                            containerColor = Color.White,
                            contentColor = Color.Black,
                            outlineColor = Color(0xFF777777),
                            iconColor = Color(0xFF3C3C3C)
                        )
                        Spacer(Modifier.height(15.dp))
                        Text(
                            "Date and Time",
                            style = labelStyle
                        )
                        Spacer(Modifier.height(5.dp))
                        InstantDateTimePicker(
                            label = "Choose Start Date and Time",
                            value = state.createEvent.startDateAndTime,
                            isError = if (!submittedOnce) null else errors.startDateAndTimeError.isNotBlank(),
                            supportingText = errors.startDateAndTimeError,
                            onValueChange = {
                                onIntent(
                                    UserIntent.CreateEventChanged(
                                        state.createEvent.copy(
                                            startDateAndTime = it
                                        )
                                    )
                                )
                            },
                        )
                        Spacer(Modifier.height(15.dp))
                        InstantDateTimePicker(
                            label = "Choose End Date and Time",
                            value = state.createEvent.endDateAndTime,
                            isError = if (!submittedOnce) null else errors.endDateAndTimeError.isNotBlank(),
                            supportingText = errors.endDateAndTimeError,
                            onValueChange = {
                                onIntent(
                                    UserIntent.CreateEventChanged(
                                        state.createEvent.copy(
                                            endDateAndTime = it
                                        )
                                    )
                                )
                            },
                        )
                        Spacer(Modifier.height(40.dp))
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
                        val context = LocalContext.current
                        val scope = rememberCoroutineScope()

                        ElevatedButton(
                            text = "NEXT",
                            onClick = {
                                submittedOnce = true
                                onIntent(UserIntent.IsLoadingChanged(true))

                                val uriString = state.createEvent.backgroundImage
                                if (uriString != null) {
                                    scope.launch(Dispatchers.IO) {

                                        val file =
                                            Utils.createTempFileFromUri(
                                                context,
                                                uriString.toUri()
                                            )

                                        if (file != null) {
                                            onIntent(UserIntent.CreateEventImageFileChanged(file))
                                            onIntent(UserIntent.ValidateCreateEvent)
                                        } else {
                                            Log.d(
                                                "CREATE_EVENT",
                                                "Error: Could not create temp file"
                                            )
                                        }
                                    }
                                }
                            },
                            isEnabled = hasFilledOut,
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
}

@Composable
fun PhotoUploadComposable(
    selectedImageUriString: String?,
    onImageSelected: (String?) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        onImageSelected(uri?.toString())
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (selectedImageUriString != null && selectedImageUriString.isNotBlank()) {
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = selectedImageUriString,
                    contentDescription = "Uploaded Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )

                Box(
                    Modifier
                        .padding(top = 10.dp, end = 10.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .size(20.dp)
                            .clickable { onImageSelected("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Remove photo",
                            tint = Color(0xFF8C44AA),
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF56387D), CircleShape)
                        .padding(5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_photo_icon),
                        contentDescription = "Add photo",
                        tint = Color.White,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Add photo",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppFonts.rethinkSans,
                    color = Color.Black.copy(0.8f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstantDateTimePicker(
    label: String,
    value: Instant?,
    isError: Boolean? = null,
    supportingText: String = "",
    onValueChange: (Instant) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var tempDate by remember { mutableStateOf<LocalDate?>(null) }

    val formattedDateTime = remember(value) {
        value?.let {
            val localDateTime = LocalDateTime.ofInstant(it, ZoneId.systemDefault())
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm").format(localDateTime)
        } ?: ""
    }

    Box {
        CustomTextField(
            text = formattedDateTime,
            onValueChange = {},
            labelText = label,
            isError = isError,
            supportingText = supportingText,
            contentColor = Color.Black,
            containerColor = Color.White,
            outlineColor = Color(0xFF777777),
            readOnly = true,
            modifier = Modifier.clickable { showDatePicker = true },
            iconButton = {
                Icon(painterResource(R.drawable.calendar_today_icon), contentDescription = null)
            }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePicker = true }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            tempDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("Asia/Manila"))
                                .toLocalDate()
                            showDatePicker = false
                            showTimePicker = true
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val initialTime =
            value?.atZone(ZoneId.of("Asia/Manila"))?.toLocalTime() ?: LocalTime.now()

        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime =
                            LocalTime.of(timePickerState.hour, timePickerState.minute)

                        if (tempDate != null) {
                            val localDateTime = LocalDateTime.of(tempDate, selectedTime)
                            val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
                            onValueChange(zonedDateTime.toInstant())
                        }
                        showTimePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Select Time",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}