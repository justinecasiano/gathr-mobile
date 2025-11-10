package com.example.gathr.presentation.auth.sign_up

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.BottomButton
import com.example.gathr.core.ui.CustomTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.PasswordField
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.ui.theme.AppMisc
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
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

    SignUpContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpContent(
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
) {

    val hasFilledOut =
        state.firstName.isNotBlank() && state.lastName.isNotBlank()
                && state.email.isNotBlank() && state.username.isNotBlank()
                && state.password.isNotBlank() && state.confirmPassword.isNotBlank()
    var submittedOnce by remember { mutableStateOf(false) }

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
                .fillMaxHeight()
                .background(Color(0xFF261A36))
        ) {
            Column(
                modifier = Modifier.padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.75f)
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
                    Row {
                        Box(modifier = Modifier.fillMaxWidth(0.48f)) {
                            CustomTextField(
                                text = state.firstName,
                                isError = if (!submittedOnce) null else state.firstNameError.isNotBlank(),
                                onValueChange = { onIntent(SignUpIntent.FirstNameChanged(it)) },
                                labelText = "First Name",
                                supportingText = state.firstNameError
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        CustomTextField(
                            text = state.lastName,
                            isError = if (!submittedOnce) null else state.lastNameError.isNotBlank(),
                            onValueChange = { onIntent(SignUpIntent.LastNameChanged(it)) },
                            labelText = "Last Name",
                            supportingText = state.lastNameError
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    CustomTextField(
                        text = state.email,
                        isError = if (!submittedOnce) null else state.emailError.isNotBlank(),
                        onValueChange = { onIntent(SignUpIntent.EmailChanged(it)) },
                        labelText = "Email",
                        supportingText = state.emailError
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    CustomTextField(
                        text = state.username,
                        isError = if (!submittedOnce) null else state.usernameError.isNotBlank(),
                        prefix = {
                            Text(
                                "@",
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                ),
                            )
                        },
                        onValueChange = { onIntent(SignUpIntent.UsernameChanged(it)) },
                        labelText = "@Username",
                        supportingText = state.usernameError
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    PasswordField(
                        text = state.password,
                        isError = if (!submittedOnce) null else state.passwordValidation.hasValidationErrors,
                        onValueChange = { onIntent(SignUpIntent.PasswordChanged(it)) },
                        labelText = "Create Password"
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    PasswordField(
                        text = state.confirmPassword,
                        isError = if (!submittedOnce) null else state.passwordValidation.hasConfirmPasswordError
                                || state.passwordValidation.hasValidationErrors,
                        onValueChange = { onIntent(SignUpIntent.ConfirmPasswordChanged(it)) },
                        labelText = "Confirm Password"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        buildAnnotatedString {
                            append("The password must contain at least ")

                            withStyle(
                                style = SpanStyle(
                                    color = if (!submittedOnce) {
                                        Color.White
                                    } else if (state.passwordValidation.hasMinLength) {
                                        AppColors.success
                                    } else AppColors.error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("8 characters")
                            }

                            append(", including an ")
                            withStyle(
                                style = SpanStyle(
                                    color = if (!submittedOnce) {
                                        Color.White
                                    } else if (state.passwordValidation.hasUppercase) {
                                        AppColors.success
                                    } else AppColors.error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("uppercase letter")
                            }
                            append(", a ")
                            withStyle(
                                style = SpanStyle(
                                    color = if (!submittedOnce) {
                                        Color.White
                                    } else if (state.passwordValidation.hasLowercase) {
                                        AppColors.success
                                    } else AppColors.error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("lowercase letter")
                            }
                            append(", a ")
                            withStyle(
                                style = SpanStyle(
                                    color = if (!submittedOnce) {
                                        Color.White
                                    } else if (state.passwordValidation.hasDigit) {
                                        AppColors.success
                                    } else AppColors.error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("number")
                            }
                            append(", and a ")
                            withStyle(
                                style = SpanStyle(
                                    color = if (!submittedOnce) {
                                        Color.White
                                    } else if (state.passwordValidation.hasSpecialChar) {
                                        AppColors.success
                                    } else AppColors.error,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("special character")
                            }
                            append(".")
                        },
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                            color = Color.White,
                        )
                    )
                }
                BottomButton(
                    bottomPadding = paddingValues.calculateBottomPadding(),
                    textButtonText = "I ALREADY HAVE AN ACCOUNT",
                    onTextButtonClick = { onIntent(SignUpIntent.LoginClicked) },
                    buttonText = "NEXT",
                    isButtonEnabled = hasFilledOut,
                    onButtonClick = {
                        submittedOnce = true
                        onIntent(SignUpIntent.NextOfBasicInfoClicked)
                    },
                )
            }
        }
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    SignUpContent(
        state = SignUpState(),
        onIntent = {}
    )
}