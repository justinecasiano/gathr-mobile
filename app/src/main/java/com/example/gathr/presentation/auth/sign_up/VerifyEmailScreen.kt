package com.example.gathr.presentation.auth.sign_up

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.BottomButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts
import kotlinx.coroutines.delay

@Composable
fun VerifyEmailScreen(
    viewModel: SignUpViewModel, onNavigateBack: () -> Unit, onNavigateNext: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SignUpEffect.NavigateBack -> onNavigateBack()
                SignUpEffect.NavigateToNext -> onNavigateNext()
                else -> {}
            }
        }
    }

    VerifyEmailContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerifyEmailContent(
    state: SignUpState,
    onIntent: (SignUpIntent) -> Unit,
) {
    val otpValues =
        remember { mutableStateListOf<String>("", "", "", "", "", "") }
    val focusManager = LocalFocusManager.current

    var submitted by remember { mutableStateOf(false) }
    var hasFilledOut by remember { mutableStateOf(false) }

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
                        IconButton(onClick = { onIntent(SignUpIntent.BackClicked) }) {
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
                            "Verify your email",
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 24.sp,
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF6F6F6)
                            )
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            buildAnnotatedString {
                                append("Please enter the 6-digit code sent to ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(state.email)
                                }
                            }, style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OTPField(
                            otpValues = otpValues,
                            otpLength = 6,
                            isError = if (!submitted || !hasFilledOut) null else state.emailCodeError.isNotBlank(),
                            onOtpInputComplete = { hasFilledOut = true },
                            onUpdateOtpValuesByIndex = { index, value ->
                                otpValues[index] = value

                                val userOtp = otpValues.joinToString(separator = "")

                                if (userOtp.length < 6) {
                                    submitted = false
                                    hasFilledOut = false
                                    onIntent(SignUpIntent.EmailCodeErrorChanged(""))
                                } else {
                                    onIntent(
                                        SignUpIntent.EmailCodeChanged(userOtp)
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        if (state.emailCodeError.isNotBlank()) {
                            Text(
                                state.emailCodeError,
                                style = TextStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFC3436),
                                ),
                                modifier = Modifier.padding(start = 5.dp)
                            )
                        }
                        ResendCodeButton(
                            60,
                            onClick = {
                                focusManager.clearFocus()
                                onIntent(SignUpIntent.ResendCode)
                            }, modifier = Modifier.align(Alignment.End)
                        )
                    }
                    BottomButton(
                        bottomPadding = paddingValues.calculateBottomPadding(),
                        hasTextButton = false,
                        buttonText = "CREATE ACCOUNT",
                        isButtonEnabled = hasFilledOut && state.emailCodeError.isBlank(),
                        onButtonClick = {
                            submitted = true
                            onIntent(SignUpIntent.IsLoadingChanged(true))
                            onIntent(SignUpIntent.VerifyOtp)
                        },
                    )
                }
            }
        }
        if (state.isLoading) {
            LoadingOverlay()
        }
        if (state.signUpError.isNotBlank()) {
            Alert(
                onDismissRequest = { onIntent(SignUpIntent.SignUpErrorChanged("")) },
                title = "Error signing you up",
                message = state.signUpError,
                confirmButtonText = "Ok",
                onConfirmClicked = { onIntent(SignUpIntent.BackClicked) },
            )
        }
    }
}

@Composable
fun OTPField(
    otpLength: Int,
    onUpdateOtpValuesByIndex: (Int, String) -> Unit,
    onOtpInputComplete: () -> Unit,
    modifier: Modifier = Modifier,
    otpValues: List<String> = List(otpLength) { "" }, // Pass this as default for future reference
    isError: Boolean? = null,
) {
    val focusRequesters = List(otpLength) { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        otpValues.forEachIndexed { index, value ->
            OutlinedTextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
                    .focusRequester(focusRequesters[index])
                    .onKeyEvent { keyEvent ->
                        if (keyEvent.key == Key.Backspace) {
                            if (otpValues[index].isEmpty() && index > 0) {
                                onUpdateOtpValuesByIndex(index, "")
                                focusRequesters[index - 1].requestFocus()
                            } else {
                                onUpdateOtpValuesByIndex(index, "")
                            }
                            true
                        } else {
                            false
                        }
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = Color.White.copy(0.5f),

                    unfocusedBorderColor = if (isError == false) AppColors.success else Color(
                        0xFF916AD2
                    ),
                    focusedBorderColor = if (isError == false) AppColors.success else Color(
                        0xFF916AD2
                    ),

                    errorBorderColor = AppColors.error,
                    errorSupportingTextColor = AppColors.error,

                    errorTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White.copy(alpha = 0.8f),

                    unfocusedContainerColor = Color(0xFF312245),
                    focusedContainerColor = Color(0xFF312245),
                ),
                value = value,
                onValueChange = { newValue ->
                    // To use OTP code copied from keyboard
                    if (newValue.length == otpLength) {
                        for (i in otpValues.indices) {
                            onUpdateOtpValuesByIndex(
                                i,
                                if (i < newValue.length && newValue[i].isDigit()) newValue[i].toString() else ""
                            )
                        }

                        keyboardController?.hide()
                        onOtpInputComplete()
                    } else if (newValue.length <= 1) {
                        onUpdateOtpValuesByIndex(index, newValue)
                        if (newValue.isNotEmpty()) {
                            if (index < otpLength - 1) {
                                focusRequesters[index + 1].requestFocus()
                            } else {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onOtpInputComplete()
                            }
                        }
                    } else {
                        if (index < otpLength - 1) focusRequesters[index + 1].requestFocus()
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = if (index == otpLength - 1) ImeAction.Done else ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (index < otpLength - 1) {
                            focusRequesters[index + 1].requestFocus()
                        }
                    },
                    onDone = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onOtpInputComplete()
                    }
                ),
                shape = RoundedCornerShape(15.dp),
                isError = isError ?: false,
                textStyle = TextStyle(
                    fontFamily = AppFonts.rethinkSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )
            )

            LaunchedEffect(value) {
                if (otpValues.all { it.isNotEmpty() }) {
                    focusManager.clearFocus()
                    onOtpInputComplete()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequesters.first().requestFocus()
    }
}

@Composable
fun ResendCodeButton(
    totalSeconds: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var secondsRemaining by remember { mutableStateOf(totalSeconds) }
    val isTimerRunning = secondsRemaining > 0

    LaunchedEffect(key1 = secondsRemaining) {
        if (isTimerRunning) {
            delay(1000L)
            secondsRemaining--
        }
    }

    TextButton(
        onClick = {
            onClick()
            secondsRemaining = totalSeconds
        }, enabled = !isTimerRunning, modifier = modifier, colors = ButtonColors(
            containerColor = Color.Unspecified,
            disabledContainerColor = Color.Unspecified,

            contentColor = Color.White,
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        )
    ) {
        val buttonText = if (isTimerRunning) {
            "Resend Code ${secondsRemaining}s"
        } else {
            "Resend Code"
        }
        Text(
            text = buttonText, textAlign = TextAlign.End, style = TextStyle(
                fontFamily = AppFonts.instrumentSans,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
            )
        )
    }
}

@Preview
@Composable
private fun VerifyEmailScreenPreview() {
    VerifyEmailContent(
        state = SignUpState(), onIntent = {})
}