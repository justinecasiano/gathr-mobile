package com.example.gathr.presentation.auth.sign_up

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.BottomButton
import com.example.gathr.core.ui.CustomTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.navigation.auth.AuthScreen
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.ui.theme.AppMisc

@Composable
fun SchoolVerificationScreen(
    viewModel: SignUpViewModel,
    onNavigateBack: () -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateNext: () -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignUpEffect.NavigateBack -> onNavigateBack()
                SignUpEffect.NavigateToLogin -> onNavigateLogin()
                SignUpEffect.NavigateToNext -> onNavigateNext()
            }
        }
    }

    SchoolVerificationContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolVerificationContent(
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
) {

    val hasFilledOut =
        (state.isUmak == true && state.department.isNotBlank() && state.isAlumni != null) ||
                (state.isUmak == false && state.school.isNotBlank() && state.school.length > 1)

    val booleanOptionsMap = mapOf(
        true to "Yes",
        false to "No"
    )

    val departmentTypes = listOf(
        "CCIS", "CCAPS", "CAL", "CBFS", "IOA",
        "CCSE", "CHK", "CGPP", "ION", "IIHS", "CITE",
        "COS", "CTHM", "IDEM", "ISW", "CET", "SOL", "HSU"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF261A36),
                ),
                navigationIcon = {
                    IconButton(onClick = { onIntent(SignUpIntent.BackClicked) }) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFFBFB6CA),
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .background(Color(0xFF261A36))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight(0.78f)
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    "Create Account", modifier = Modifier.fillMaxWidth(), style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(18.dp))
                SimpleDropdownMenu(
                    labelText = "Are you a UMak Student?",
                    options = listOf("Yes", "No"),
                    value = booleanOptionsMap[state.isUmak] ?: "",
                    onValueChange = { onIntent(SignUpIntent.IsUmakChanged(it == "Yes")) },
                )
                if (state.isUmak == true) {

                    Spacer(modifier = Modifier.height(5.dp))
                    SimpleDropdownMenu(
                        labelText = "Select your department",
                        options = departmentTypes,
                        value = state.department,
                        onValueChange = { onIntent(SignUpIntent.DepartmentChanged(it)) },
                    )
                } else if (state.isUmak == false) {
                    Spacer(modifier = Modifier.height(5.dp))
                    CustomTextField(
                        text = state.school,
                        isError = if (!state.school.isBlank()) false else null,
                        onValueChange = { onIntent(SignUpIntent.SchoolChanged(it)) },
                        labelText = "Name of School",
                    )
                }
                if (state.isUmak == true && state.department.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    SimpleDropdownMenu(
                        labelText = "Are you an alumni?",
                        options = listOf("Yes", "No"),
                        value = booleanOptionsMap[state.isAlumni] ?: "",
                        onValueChange = { onIntent(SignUpIntent.IsAlumniChanged(it == "Yes")) },
                    )
                }
            }
            BottomButton(
                bottomPadding = paddingValues.calculateBottomPadding(),
                textButtonText = "I ALREADY HAVE AN ACCOUNT",
                onTextButtonClick = { onIntent(SignUpIntent.LoginClicked) },
                buttonText = "NEXT",
                isButtonEnabled = hasFilledOut,
                onButtonClick = { onIntent(SignUpIntent.NextOfSchoolVerificationClicked) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDropdownMenu(
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String,
    options: List<String>,
) {
    var selectedOption by remember { mutableStateOf(value) }
    var isExpanded by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    val targetFontSize = if (isFocused) 15f else 18f
    val animatedFontSize by animateFloatAsState(
        targetValue = targetFontSize,
        animationSpec = tween(durationMillis = 100),
        label = "fontSizeAnimation"
    )

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
            textStyle = TextStyle(
                fontFamily = AppFonts.rethinkSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            ),
            label = {
                Text(
                    labelText,
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = animatedFontSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f),
                    ),
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.White,
                unfocusedBorderColor = if (value.isNotBlank()) AppColors.success else Color(
                    0xFF916AD2
                ),
                focusedBorderColor = if (value.isNotBlank()) AppColors.success else Color(0xFF916AD2),
                errorBorderColor = AppColors.error,
                errorSupportingTextColor = AppColors.error,

                errorTextColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White.copy(alpha = 0.8f),

                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.White.copy(alpha = 0.8f),

                unfocusedContainerColor = Color(0xFF312245),
                focusedContainerColor = Color(0xFF312245),
            ),
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            shape = RoundedCornerShape(15.dp),
            containerColor = Color(0xFF312245),
            border = BorderStroke(2.dp, Color(0xFF916AD2)),
            onDismissRequest = {
                isExpanded = false
            },
        ) {
            options.forEach { item ->
                val isSelected = (item == selectedOption)

                DropdownMenuItem(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isSelected) Color(0xFF7B55A3).copy(alpha = 0.5f)
                            else Color(0xFF312245)
                        ),
                    text = {
                        Text(
                            item,
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                        )
                    },
                    onClick = {
                        selectedOption = item
                        isExpanded = false
                        onValueChange(item)
                    },
                )
            }
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SchoolVerificationScreenPreview() {
    SchoolVerificationContent(SignUpState(), onIntent = {})
}