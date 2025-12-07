package com.example.gathr.presentation.auth.forgot_password

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.BottomButton
import com.example.gathr.core.ui.ClearTextField
import com.example.gathr.core.ui.CustomTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.core.ui.PasswordField
import com.example.gathr.navigation.auth.AuthScreen
import com.example.gathr.presentation.auth.PasswordValidationState
import com.example.gathr.presentation.auth.login.LoginIntent
import com.example.gathr.presentation.auth.sign_up.SignUpIntent
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun ResetPasswordScreen(
    viewModel: ForgotPasswordViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ForgotPasswordEffect.NavigateBack -> onNavigateBack()
                ForgotPasswordEffect.NavigateToLogin -> {}
                ForgotPasswordEffect.NavigateToNext -> onNavigateNext()
            }
        }
    }

    ResetPasswordContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordContent(
    state: ForgotPasswordState,
    onIntent: (ForgotPasswordIntent) -> Unit,
) {
    val hasFilledOut = state.newPassword.isNotBlank() && state.confirmNewPassword.isNotBlank()
    var submitted by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF261A36),
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onIntent(ForgotPasswordIntent.BackClicked) }) {
                            Icon(
                                modifier = Modifier.size(22.dp),
                                tint = Color(0xFFBFB6CA),
                                painter = painterResource(R.drawable.close),
                                contentDescription = "Close"
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
                            .padding(horizontal = 30.dp),
                    ) {
                        Text(
                            "Reset Password", modifier = Modifier.fillMaxWidth(), style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF6F6F6)
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        PasswordField(
                            text = state.newPassword,
                            isError = if (!submitted) null else state.passwordValidation.hasValidationErrors ||
                                    state.confirmNewPasswordError.isNotBlank(),
                            onValueChange = { onIntent(ForgotPasswordIntent.NewPasswordChanged(it)) },
                            labelText = "New Password"
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        PasswordField(
                            text = state.confirmNewPassword,
                            isError = if (!submitted) null else state.passwordValidation.hasValidationErrors
                                    || state.confirmNewPasswordError.isNotBlank(),
                            supportingText = state.confirmNewPasswordError,
                            onValueChange = {
                                onIntent(ForgotPasswordIntent.ConfirmNewPasswordChanged(it))
                                submitted = false
                                onIntent(
                                    ForgotPasswordIntent.PasswordValidationChanged(
                                        PasswordValidationState()
                                    )
                                )
                            },
                            labelText = "Confirm New Password"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            buildAnnotatedString {
                                append("The password must contain at least ")
                                withStyle(
                                    style = SpanStyle(
                                        color = if (!submitted) {
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
                                        color = if (!submitted) {
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
                                        color = if (!submitted) {
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
                                        color = if (!submitted) {
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
                                        color = if (!submitted) {
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
                        hasTextButton = false,
                        buttonText = "NEXT",
                        isButtonEnabled = hasFilledOut,
                        onButtonClick = {
                            submitted = true
                            onIntent(ForgotPasswordIntent.IsLoadingChanged(true))
                            onIntent(ForgotPasswordIntent.SubmitResetPasswordClicked)
                        },
                    )
                }
            }
        }
        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}

@Preview
@Composable
private fun ResetPasswordScreenPreview() {
    ResetPasswordContent(state = ForgotPasswordState(), onIntent = {})
}
