package com.example.gathr.presentation.participant

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.FeedbackQuestion
import com.example.gathr.data.model.FormEditorValues
import com.example.gathr.data.model.FormSubmission
import com.example.gathr.data.model.ParticipantStatus
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.ResponseStatus
import com.example.gathr.data.model.UserRole
import com.example.gathr.presentation.main.MainEffect
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.Utils
import com.example.gathr.utils.dummyEvents
import com.example.gathr.utils.dummyParticipants
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toTitleCase
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import java.lang.Enum.valueOf
import java.time.Instant

@Composable
fun FeedbackScreen(viewModel: UserViewModel, onNavigateBack: () -> Unit) {
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
        FeedbackContent(
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
fun FeedbackContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
    onNavigate: (MainEffect) -> Unit,
) {
    val event: Event = state.currentEvent!!

    var justSubmitted by remember { mutableStateOf(false) }
    val isAlreadySubmitted = remember(state.joinedEvents) {
        state.currentEvent?.responseStatus == ResponseStatus.ANSWERED
    }
    val formStructure = remember(event.feedbackForm) {
        event.feedbackForm?.let {
            Json.decodeFromJsonElement<FormEditorValues>(it)
        }
    }

    val answers = remember { mutableStateMapOf<String, Any>() }
    val scrollState = rememberScrollState()
    var isSubmitted by remember { mutableStateOf(false) }

    val canSubmit by remember(answers.size) {
        derivedStateOf {
            formStructure?.questions?.all { question ->
                if (question.required) {
                    val answer = answers[question.id]
                    when (answer) {
                        is String -> answer.isNotBlank()
                        is List<*> -> answer.isNotEmpty()
                        is Float -> true
                        else -> answer != null
                    }
                } else true
            } ?: false
        }
    }

    LaunchedEffect(formStructure) {
        formStructure?.questions?.forEach { question ->
            if (question is FeedbackQuestion.SliderQuestion) {
                if (answers[question.id] == null) {
                    answers[question.id] = 3f
                }
            }
        }
    }

    LaunchedEffect(isAlreadySubmitted, state.currentEvent?.feedbackSubmission) {
        if (isAlreadySubmitted) {
            state.currentEvent?.feedbackSubmission?.let { json ->
                val submission = Json.decodeFromJsonElement<FormSubmission>(json)
                submission.responses.forEach { resp ->
                    val value: Any = when {
                        resp.answer is JsonPrimitive && resp.answer.isString -> resp.answer.content
                        resp.answer is JsonPrimitive -> resp.answer.content.toFloatOrNull()
                            ?: resp.answer.content

                        resp.answer is JsonArray -> resp.answer.map { it.jsonPrimitive.content }
                        else -> resp.answer.toString()
                    }
                    answers[resp.questionId] = value
                }
            }
        }
    }

    Scaffold(topBar = {
        Box(
            Modifier
                .background(Color(0xFFF6F6F6))
        ) {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Feedback Form",
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
        Column(Modifier.fillMaxSize()) {
            if (justSubmitted) {
                Column(
                    Modifier
                        .fillMaxHeight()
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
                        .padding(horizontal = 30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.feedback_done),
                        contentDescription = "Feedback Check",
                        modifier = Modifier
                            .width(200.dp)
                            .height(150.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = "Thank You For Your Feedback!",
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "We appreciate your time and input. It helps us improve!",
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 14.sp,
                            color = Color(0xFF5D5D5D)
                        )
                    )
                }
            } else if (event.feedbackForm == null) {
                Column(
                    Modifier
                        .fillMaxHeight()
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
                        .padding(horizontal = 30.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No survey available",
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 28.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "The organizer has not provided a feedback form for this event.",
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 14.sp,
                            color = Color(0xFF5D5D5D)
                        )
                    )
                }
            } else {
                Column(
                    Modifier
                        .fillMaxHeight(0.84f)
                        .background(Color(0xFFF0F0F0))
                        .verticalScroll(scrollState)
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(
                                LocalLayoutDirection.current
                            ),
                            end = paddingValues.calculateStartPadding(
                                LocalLayoutDirection.current
                            ),
                        )
                        .padding(horizontal = 30.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(Modifier.height(3.dp))
                    Box(
                        Modifier.fillMaxWidth()
                    ) {
                        Image(
                            modifier = Modifier.width(500.dp),
                            painter = painterResource(R.drawable.feedback_form_banner),
                            contentDescription = "Event Banner",
                            contentScale = ContentScale.FillWidth
                        )
                        Text(
                            text = if (isAlreadySubmitted) "Thank you for\nyour feedback!" else "Thank you for\njoining this event!",
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
                                .padding(start = 25.dp, top = 20.dp)
                        )
                    }
                    if (isAlreadySubmitted) {
                        Surface(
                            color = Color(0xFF7B55A3).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "You have already submitted this form. Below are your responses.",
                                modifier = Modifier.padding(12.dp),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color(0xFF7B55A3),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    } else {
                        Text(
                            "We value your opinion! Please take a moment to answer this short survey and help us improve our services for everyone.",
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Start,
                                color = Color(0xFF5D5D5D)
                            ),
                        )
                    }
                    if (formStructure?.title !== null && formStructure.title != "New Feedback Form") {
                        Text(
                            formStructure.title,
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            ),
                        )
                    }
                    formStructure?.questions?.sortedBy { it.order }
                        ?.forEachIndexed { index, question ->
                            FeedbackQuestionItem(
                                index = index,
                                question = question,
                                currentAnswer = answers[question.id],
                                onAnswerChange = { answers[question.id] = it },
                                readOnly = event.responseStatus == ResponseStatus.ANSWERED
                            )
                        }
                    Spacer(Modifier.height(20.dp))
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
                            text = if (isAlreadySubmitted || justSubmitted) "GO BACK" else "SUBMIT",
                            onClick = {
                                if (isAlreadySubmitted || justSubmitted) {
                                    onIntent(UserIntent.BackClicked)
                                } else if (canSubmit) {
                                    val submission = Utils.prepareSubmission(event.id.toString(), formStructure, answers)
                                    onIntent(UserIntent.SubmitFeedback(submission))
                                    justSubmitted = true
                                }
                            },
                            isEnabled = if (isAlreadySubmitted || justSubmitted) true else canSubmit,
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
fun FeedbackQuestionItem(
    index: Int,
    question: FeedbackQuestion,
    currentAnswer: Any?,
    onAnswerChange: (Any) -> Unit,
    readOnly: Boolean = false
) {
    val alpha = if (readOnly) 0.7f else 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .background(Color.White, RoundedCornerShape(20.dp))
            .border(2.dp, Color(0xFF7B55A3), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Question ${index + 1}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7B55A3),
                    letterSpacing = 1.sp
                )
            )
            if (question.required) {
                Text(" *", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            question.questionText,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF444444)
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(Modifier.height(10.dp))

        when (question) {
            is FeedbackQuestion.RadioQuestion -> {
                question.options.forEach { option ->
                    val isSelected = currentAnswer == option.id
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFF3EFFF) else Color(0xFFFAFAFA))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF7B55A3) else Color(0xFFEEEEEE),
                                RoundedCornerShape(12.dp)
                            )
                            .then(if (!readOnly) Modifier.clickable { onAnswerChange(option.id) } else Modifier)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            enabled = !readOnly,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color(0xFF7B55A3),
                                disabledSelectedColor = Color(0xFF7B55A3).copy(alpha = 0.6f)
                            )
                        )
                        Text(
                            option.label,
                            Modifier.padding(start = 8.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF444444)
                            ),
                        )
                    }
                }
            }

            is FeedbackQuestion.CheckboxQuestion -> {
                val selectedList = (currentAnswer as? List<String>) ?: emptyList()
                question.options.forEach { option ->
                    val isChecked = selectedList.contains(option.id)
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isChecked) Color(0xFFF3EFFF) else Color(0xFFFAFAFA))
                            .border(
                                1.dp,
                                if (isChecked) Color(0xFF7B55A3) else Color(0xFFEEEEEE),
                                RoundedCornerShape(12.dp)
                            )
                            .then(
                                if (!readOnly) {
                                    Modifier.clickable {
                                        val newList =
                                            if (isChecked) selectedList - option.id else selectedList + option.id
                                        onAnswerChange(newList)
                                    }
                                } else Modifier
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = null,
                            enabled = !readOnly,
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF7B55A3))
                        )
                        Text(
                            option.label, Modifier.padding(start = 8.dp),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF444444)
                            ),
                        )
                    }
                }
            }

            is FeedbackQuestion.SliderQuestion -> {
                val rating = (currentAnswer as? Float) ?: 3f
                Column {
                    Slider(
                        value = rating,
                        onValueChange = { onAnswerChange(it) },
                        valueRange = 1f..5f,
                        enabled = !readOnly,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF4C2D6C),
                            inactiveTrackColor = Color(0xFFE5E5E5),
                            activeTrackColor = Color(0xFF4C2D6C),
                            inactiveTickColor = Color(0xFFE5E5E5),
                            activeTickColor = Color(0xFF4C2D6C),
                        ),
                    )
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        (1..5).forEach {
                            Text(
                                it.toString(),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            is FeedbackQuestion.TextQuestion -> {
                OutlinedTextField(
                    value = (currentAnswer as? String) ?: "",
                    onValueChange = { onAnswerChange(it) },
                    placeholder = {
                        Text(
                            question.placeholder ?: "Type your answer...",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFF444444)
                            ),
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = readOnly,
                    enabled = !readOnly,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF7B55A3),
                        unfocusedContainerColor = Color(0xFFFAFAFA)
                    )
                )
            }
        }
    }
}

