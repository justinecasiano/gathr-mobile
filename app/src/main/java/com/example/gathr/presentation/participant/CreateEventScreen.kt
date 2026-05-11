package com.example.gathr.presentation.participant

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import coil3.request.placeholder
import com.example.gathr.core.ui.ImageViewer
import com.example.gathr.core.ui.InstantDateTimePicker
import com.example.gathr.core.ui.PhotoUploadComposable
import com.example.gathr.data.model.CreateEventStep
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.DepartmentType
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.ui.theme.fadeIn
import com.example.gathr.ui.theme.fadeOut
import com.example.gathr.utils.toTitleCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CreateEventScreen(
    viewModel: UserViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.userEffect.collect { effect ->
            when (effect) {
                UserEffect.NavigateNext -> onNavigateNext()
                UserEffect.NavigateBack -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(UserIntent.FetchAvailableStaff)
    }

    BackHandler {
        viewModel.handleIntent(UserIntent.CreateEventPreviousStepClicked)
    }

    Box(Modifier.fillMaxSize()) {
        CreateEventContent(
            state = state,
            onIntent = viewModel::handleIntent,
        )
        if (state.isLoading) {
            LoadingOverlay()
        }

        when {
            state.actionError.contains("Add", ignoreCase = true) -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = {
                        state.actionOnConfirm()
                        viewModel.handleIntent(UserIntent.ActionErrorChanged(""))
                        viewModel.handleIntent(UserIntent.ActionOnConfirmClicked {})
                    },
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }

            state.actionError.contains("Remove", ignoreCase = true) -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = {
                        state.actionOnConfirm()
                        viewModel.handleIntent(UserIntent.ActionErrorChanged(""))
                        viewModel.handleIntent(UserIntent.ActionOnConfirmClicked {})
                    },
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }

            state.actionError.contains("You have not added", ignoreCase = true) -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = {
                        state.actionOnConfirm()
                    },
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }

            state.actionError.contains("Once submitted") -> {
                Alert(
                    title = state.actionTitle,
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Confirm",
                    onConfirmClicked = {
                        state.actionOnConfirm()
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                    },
                    cancelButtonText = "Cancel",
                    onCancelClicked = { viewModel.handleIntent(UserIntent.ActionOnClear) },
                )
            }

            state.actionError.contains("Please wait") -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Success" },
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = {
                        viewModel.sendMainEffect(MainEffect.ViewEvent)
                        viewModel.handleIntent(UserIntent.ActionOnClear)
                        state.actionOnConfirm()
                    },
                )
            }

            state.actionError.isNotBlank() -> {
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
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CreateEventContent(
    state: UserState, onIntent: (UserIntent) -> Unit
) {
    val event = state.currentCreateEvent
    val errors = event.createEventValidationState

    val isNotEmpty = event.title.isNotBlank() &&
            event.description.isNotBlank() &&
            event.location.isNotBlank() &&
            event.capacity != null &&
            !event.backgroundImage.isNullOrBlank()

    val isButtonEnabled =
        if (event.createEventStep == CreateEventStep.BASIC_INFO) isNotEmpty else true
    var submittedOnce by remember { mutableStateOf(false) }
    var selectedImage by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onIntent(UserIntent.CreateEventPreviousStepClicked) }) {
                            Icon(
                                modifier = Modifier.size(37.dp),
                                tint = Color.Black,
                                painter = painterResource(R.drawable.arrow_back),
                                contentDescription = "Back"
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            AnimatedContent(
                targetState = state.currentCreateEvent.createEventStep, transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    }.using(SizeTransform(clip = false))
                }, label = "CreateEventStepTransition"
            ) { step ->
                when (step) {
                    CreateEventStep.ADD_STAFF -> {
                        AddStaffContent(state = state, onIntent = onIntent, paddingValues)
                    }

                    CreateEventStep.BASIC_INFO -> {
                        Column(Modifier.fillMaxHeight()) {
                            Column(
                                Modifier
                                    .fillMaxHeight(0.84f)
                                    .background(Color(0xFFF0F0F0))
                                    .padding(
                                        top = paddingValues.calculateTopPadding(),
                                        start = paddingValues.calculateStartPadding(
                                            LocalLayoutDirection.current
                                        ),
                                        end = paddingValues.calculateStartPadding(
                                            LocalLayoutDirection.current
                                        ),
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
                                        "Create an Event",
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
                                        Modifier.fillMaxWidth()
                                    ) {
                                        Image(
                                            modifier = Modifier.width(500.dp),
                                            painter = painterResource(R.drawable.create_event_banner),
                                            contentDescription = "Event Banner",
                                            contentScale = ContentScale.FillWidth
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
                                                .padding(start = 15.dp)
                                        )
                                    }
                                    Spacer(Modifier.height(10.dp))

                                    val context = LocalContext.current
                                    PhotoUploadComposable(
                                        selectedImageUriString = state.currentCreateEvent.backgroundImage,
                                        onImageClick = { selectedImage = it },
                                        onImageSelected = { uriString ->
                                            val size = if (uriString != null) Utils.getFileSize(context, uriString) else 0L

                                            onIntent(UserIntent.CurrentCreateEventChanged(
                                                state.currentCreateEvent.copy(
                                                    backgroundImage = uriString,
                                                    backgroundImageSizeBytes = size
                                                )
                                            ))
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
                                        "Event Title", style = labelStyle
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    ClearTextField(
                                        text = state.currentCreateEvent.title,
                                        isError = if (!submittedOnce) null else errors.titleError.isNotBlank(),
                                        supportingText = errors.titleError,
                                        onValueChange = {
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
                                                        title = it
                                                    )
                                                )
                                            )
                                        },
                                        onClick = {
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
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
                                        "Description", style = labelStyle
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Box(Modifier.fillMaxWidth()) {
                                        CustomTextField(
                                            text = state.currentCreateEvent.description,
                                            minLines = 3,
                                            maxLines = 5,
                                            isError = if (!submittedOnce) null else errors.descriptionError.isNotBlank(),
                                            supportingText = errors.descriptionError,
                                            onValueChange = {
                                                onIntent(
                                                    UserIntent.CurrentCreateEventChanged(
                                                        state.currentCreateEvent.copy(
                                                            description = it
                                                        )
                                                    )
                                                )
                                            },
                                            labelText = "Tell us more about your event",
                                            containerColor = Color.White,
                                            contentColor = Color.Black,
                                            outlineColor = Color(0xFF777777),
                                            iconButton = { Spacer(Modifier.width(20.dp)) })
                                        if (state.currentCreateEvent.description.isNotBlank()) IconButton(
                                            onClick = {
                                                onIntent(
                                                    UserIntent.CurrentCreateEventChanged(
                                                        state.currentCreateEvent.copy(
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
                                        "Max Participants", style = labelStyle
                                    )
                                    Spacer(Modifier.height(5.dp))

                                    val capacity = state.currentCreateEvent.capacity
                                    CustomTextField(
                                        text = capacity?.toString() ?: "",
                                        isError = if (!submittedOnce) null else errors.capacityError.isNotBlank(),
                                        supportingText = errors.capacityError,
                                        onValueChange = {
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
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
                                        "Location", style = labelStyle
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    ClearTextField(
                                        text = state.currentCreateEvent.location,
                                        isError = if (!submittedOnce) null else errors.locationError.isNotBlank(),
                                        supportingText = errors.locationError,
                                        onValueChange = {
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
                                                        location = it
                                                    )
                                                )
                                            )
                                        },
                                        onClick = {
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
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
                                        "Who can register?", style = labelStyle
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        DepartmentType.entries.forEach { dept ->
                                            val isSelected =
                                                state.currentCreateEvent.allowedDepartments.contains(
                                                    dept
                                                )

                                            FilterChip(
                                                selected = isSelected,
                                                onClick = {
                                                    val currentList =
                                                        state.currentCreateEvent.allowedDepartments.toMutableList()

                                                    if (dept == DepartmentType.ALL) {
                                                        currentList.clear()
                                                        currentList.add(DepartmentType.ALL)
                                                    } else {
                                                        currentList.remove(DepartmentType.ALL)

                                                        if (isSelected) {
                                                            currentList.remove(dept)
                                                            if (currentList.isEmpty()) currentList.add(
                                                                DepartmentType.ALL
                                                            )
                                                        } else {
                                                            currentList.add(dept)
                                                        }
                                                    }

                                                    onIntent(
                                                        UserIntent.CurrentCreateEventChanged(
                                                            state.currentCreateEvent.copy(
                                                                allowedDepartments = currentList
                                                            )
                                                        )
                                                    )
                                                },
                                                border = FilterChipDefaults.filterChipBorder(
                                                    enabled = true,
                                                    selected = isSelected,
                                                    borderColor = Color.Black,
                                                    selectedBorderColor = Color(0xFF7B55A3),
                                                    borderWidth = 1.dp,
                                                    selectedBorderWidth = 2.dp
                                                ),
                                                label = {
                                                    Text(
                                                        text = dept.name,
                                                        style = TextStyle(
                                                            fontFamily = AppFonts.instrumentSans,
                                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                                            fontSize = 12.sp
                                                        )
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = Color(0xFF7B55A3),
                                                    selectedLabelColor = Color.White,
                                                    selectedLeadingIconColor = Color.White
                                                )
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(15.dp))
                                    Text(
                                        "Allow alumni?", style = labelStyle
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    Switch(
                                        checked = state.currentCreateEvent.allowAlumni,
                                        onCheckedChange = { isChecked ->
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(allowAlumni = isChecked)
                                                )
                                            )
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = Color(0xFF7B55A3),
                                            uncheckedThumbColor = Color.White,
                                            uncheckedTrackColor = Color.LightGray
                                        )
                                    )
                                    Spacer(Modifier.height(15.dp))
                                    Text(
                                        "Date and Time", style = labelStyle
                                    )
                                    Spacer(Modifier.height(5.dp))
                                    InstantDateTimePicker(
                                        label = "Choose Start Date and Time",
                                        value = state.currentCreateEvent.startDateAndTime,
                                        isError = if (!submittedOnce) null else errors.startDateAndTimeError.isNotBlank(),
                                        supportingText = errors.startDateAndTimeError,
                                        onValueChange = {
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
                                                        startDateAndTime = it
                                                    )
                                                )
                                            )
                                        },
                                    )
                                    Spacer(Modifier.height(15.dp))
                                    InstantDateTimePicker(
                                        label = "Choose End Date and Time",
                                        value = state.currentCreateEvent.endDateAndTime,
                                        isError = if (!submittedOnce) null else errors.endDateAndTimeError.isNotBlank(),
                                        supportingText = errors.endDateAndTimeError,
                                        onValueChange = {
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
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
                                    ElevatedButton(
                                        text = "NEXT",
                                        onClick = {
                                            submittedOnce = true
                                            onIntent(UserIntent.VerifyCreateEvent)
                                        },
                                        isEnabled = isButtonEnabled,
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
        }
        if (selectedImage.isNotBlank())
            ImageViewer(selectedImage, onDismiss = { selectedImage = "" })
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddStaffContent(
    state: UserState, onIntent: (UserIntent) -> Unit, paddingValues: PaddingValues
) {
    var selectedImage by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    val staffs = state.currentCreateEvent.staffs

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxHeight()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.84f)
                    .background(Color.White)
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    )
                    .padding(horizontal = 30.dp)
            ) {
                Text(
                    "Add Staffs",
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                        color = Color.Black
                    ),
                )
                Spacer(Modifier.height(10.dp))
                ClearTextField(
                    modifier = Modifier.fillMaxWidth(),
                    text = state.searchStaff,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Unspecified,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Unspecified
                    ),
                    onValueChange = {
                        isExpanded = it.isNotBlank()
                        onIntent(UserIntent.SearchStaffChanged(it))
                    },
                    onClick = {
                        onIntent(UserIntent.SearchStaffChanged(""))
                    },
                    labelText = "Input email or username",
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    outlineColor = Color(0xFF777777),
                    iconColor = Color(0xFF3C3C3C)
                )
                val selectedStaffIds = staffs.mapNotNull { it.userId }.toSet()

                val searchStaff = state.searchStaff

                val filteredAvailableStaff = state.availableStaff.filter { user ->
                    val isNotSelected = user.id !in selectedStaffIds

                    val matchesSearch =
                        user.firstName.contains(searchStaff, ignoreCase = true) ||
                                user.lastName.contains(searchStaff, ignoreCase = true) ||
                                (user.displayName ?: "").contains(
                                    searchStaff,
                                    ignoreCase = true
                                ) ||
                                user.email.contains(searchStaff, ignoreCase = true)

                    isNotSelected && matchesSearch
                }

                if (isExpanded && filteredAvailableStaff.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        LazyColumn {
                            items(filteredAvailableStaff) { user ->
                                StaffRow(
                                    modifier = Modifier.padding(8.dp, 5.dp),
                                    firstName = user.firstName,
                                    lastName = user.lastName,
                                    displayName = user.displayName ?: "",
                                    avatarUrl = user.avatarUrl,
                                    onImageClick = { selectedImage = it },
                                    onAdd = {
                                        onIntent(UserIntent.ActionTitleChanged("Add Staff"))
                                        onIntent(UserIntent.ActionErrorChanged("Are you sure you want to add ${user.firstName.toTitleCase()} ${user.lastName.toTitleCase()} as a staff?"))
                                        onIntent(
                                            UserIntent.ActionOnConfirmClicked {
                                                val newStaff = CreateStaff(
                                                    userId = user.id,
                                                    email = user.email,
                                                    displayName = user.displayName ?: "",
                                                    avatarUrl = user.avatarUrl ?: "",
                                                    firstName = user.firstName,
                                                    lastName = user.lastName,
                                                    participantType = ParticipantType.STAFF
                                                )

                                                isExpanded = false
                                                onIntent(UserIntent.SearchStaffChanged(""))

                                                val updatedList = staffs + newStaff
                                                onIntent(
                                                    UserIntent.CurrentCreateEventChanged(
                                                        state.currentCreateEvent.copy(staffs = updatedList)
                                                    )
                                                )
                                            },
                                        )
                                    })
                                HorizontalDivider(color = Color.LightGray.copy(0.5f))
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp)
                    ) {
                        items(staffs, key = { staff -> staff.userId!! }) { staff ->
                            StaffRow(
                                firstName = staff.firstName,
                                lastName = staff.lastName,
                                displayName = staff.displayName,
                                avatarUrl = staff.avatarUrl,
                                onImageClick = { selectedImage = it },
                                onRemove = {
                                    onIntent(UserIntent.ActionTitleChanged("Remove Staff"))
                                    onIntent(UserIntent.ActionErrorChanged("Are you sure you want to remove ${staff.firstName.toTitleCase()} ${staff.lastName.toTitleCase()} as a staff?"))
                                    onIntent(
                                        UserIntent.ActionOnConfirmClicked {
                                            val updatedList =
                                                staffs.filter { it.userId != staff.userId }
                                            onIntent(
                                                UserIntent.CurrentCreateEventChanged(
                                                    state.currentCreateEvent.copy(
                                                        staffs = updatedList
                                                    )
                                                )
                                            )
                                        },
                                    )
                                })
                        }
                        item {
                            Spacer(Modifier.height(40.dp))
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
                        text = "CREATE EVENT",
                        onClick = {
                            val scope = CoroutineScope(Dispatchers.Main)
                            if (staffs.count() == 0) {
                                onIntent(UserIntent.ActionTitleChanged("No Staff"))
                                onIntent(UserIntent.ActionErrorChanged("You have not added any staff, continue create event?"))
                                onIntent(UserIntent.ActionOnConfirmClicked {
                                    scope.launch {
                                        onIntent(UserIntent.ActionErrorChanged(""))
                                        delay(50)
                                        onIntent(UserIntent.ActionTitleChanged("Finalize Event"))
                                        onIntent(UserIntent.ActionErrorChanged("Once submitted, your event will be reviewed by a moderator before it becomes visible to others. Ready to send?"))
                                        onIntent(UserIntent.ActionOnConfirmClicked {
                                            onIntent(UserIntent.DoCreateEvent)
                                        })
                                    }
                                })
                            } else {
                                onIntent(UserIntent.ActionTitleChanged("Finalize Event"))
                                onIntent(UserIntent.ActionErrorChanged("Once submitted, your event will be reviewed by a moderator before it becomes visible to others. Ready to send?"))
                                onIntent(UserIntent.ActionOnConfirmClicked {
                                    onIntent(UserIntent.DoCreateEvent)
                                })
                            }
                        },
                        isEnabled = state.actionError.isBlank(),
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
    if (selectedImage.isNotBlank())
        ImageViewer(selectedImage, onDismiss = { selectedImage = "" })
}

@Composable
fun StaffRow(
    modifier: Modifier = Modifier,
    firstName: String,
    lastName: String,
    displayName: String,
    avatarUrl: String? = null,
    onImageClick: (String) -> Unit,
    onAdd: (() -> Unit)? = null,
    onRemove: (() -> Unit)? = null
) {
    Row(
        modifier
            .clickable(
                enabled = onAdd != null,
                onClick = { onAdd?.invoke() }
            )
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(63.dp)
                .clip(CircleShape)
                .clickable(enabled = avatarUrl != null || onAdd == null) {
                    onImageClick(
                        avatarUrl ?: ""
                    )
                },
            model = ImageRequest.Builder(LocalContext.current).data(avatarUrl ?: "")
                .placeholder(R.drawable.profile)
                .fallback(R.drawable.profile).crossfade(true)
                .listener(onStart = { request ->
                    Log.d(
                        "IMAGE_LOAD", "Image started loading"
                    )
                }, onError = { request, result ->
                    Log.e(
                        "IMAGE_LOAD", "FAILED: ${result.throwable.message}"
                    )
                }).build(),
            contentDescription = "Chatmate Photo",
            contentScale = ContentScale.Crop,
            error = painterResource(R.drawable.profile)
        )
        Spacer(Modifier.width(20.dp))
        Column(Modifier.weight(7f)) {
            Text(
                "${firstName.toTitleCase()} ${lastName.toTitleCase()}",
                style = TextStyle(
                    fontFamily = AppFonts.rethinkSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = Color.Black.copy(0.8f)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (displayName.isNotBlank())
                Text(
                    "@$displayName",
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Start,
                        color = Color.Gray
                    ),
                    overflow = TextOverflow.Ellipsis,
                )
        }
        Spacer(Modifier.weight(1f))
        if (onRemove != null) Box(Modifier.clickable(onClick = { onRemove() })) {
            Icon(
                painterResource(R.drawable.delete),
                contentDescription = "Remove Staff",
                tint = Color(0xFFEE101A),
                modifier = Modifier.size(32.dp)
            )
        }
    }
}