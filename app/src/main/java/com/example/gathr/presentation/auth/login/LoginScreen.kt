package com.example.gathr.presentation.auth.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.R
import com.example.gathr.core.ui.CustomTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.core.ui.PasswordField
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateBack: () -> Unit,
    onNavigateForgotPassword: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateBack -> onNavigateBack()
                LoginEffect.NavigateToForgotPassword -> onNavigateForgotPassword()
                LoginEffect.NavigateToNext -> onNavigateNext()
            }
        }
    }

    LoginContent(
        state = state,
        onIntent = viewModel::handleIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
) {
    val hasFilledOut = state.email.isNotBlank() && state.password.isNotBlank()
    var submitted by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                    title = {
                        Text(
                            "Enter your details", style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBFB6CA)
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        IconButton(onClick = { onIntent(LoginIntent.BackClicked) }) {
                            Icon(
                                modifier = Modifier.size(37.dp),
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
                    .fillMaxSize()
                    .background(Color(0xFF261A36))
                    .padding(paddingValues)
                    .padding(horizontal = 30.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                MergedInputFields(
                    emailValue = state.email,
                    passwordValue = state.password,
                    isEmailError = if (!submitted) null else state.emailOrPasswordError.isNotBlank(),
                    isPasswordError = if (!submitted) null else state.emailOrPasswordError.isNotBlank(),
                    onEmailChange = {
                        onIntent(LoginIntent.EmailChanged(it))
                        submitted = false
                        onIntent(LoginIntent.EmailOrPasswordErrorChanged(""))
                    },
                    onPasswordChange = {
                        onIntent(LoginIntent.PasswordChanged(it))
                        submitted = false
                        onIntent(LoginIntent.EmailOrPasswordErrorChanged(""))
                    },
                )
                Spacer(modifier = Modifier.height(5.dp))
                if (state.emailOrPasswordError.isNotBlank()) {
                    Text(
                        state.emailOrPasswordError,
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFC3436),
                        ),
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                ElevatedButton(
                    buttonColor = AppColors.primary,
                    text = "SIGN IN",
                    isEnabled = hasFilledOut,
                    onClick = {
                        focusManager.clearFocus()
                        submitted = true
                        onIntent(LoginIntent.IsLoadingChanged(true))
                        onIntent(LoginIntent.LoginClicked)
                    },
                    bottomBorderThickness = 0.dp,
                    textStyle = TextStyle(
                        fontFamily = AppFonts.instrumentSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    ),
                )
                Spacer(modifier = Modifier.height(15.dp))
                TextButton(
                    onClick = { onIntent(LoginIntent.ForgotPasswordClicked) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "FORGOT PASSWORD",
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFFF7906E)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
//                Text(
//                    buildAnnotatedString {
//                        append("By signing in to Gathr, you agree to our ")
//                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                            append("Terms ")
//                        }
//                        append("and ")
//                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                            append("Privacy Policy.")
//                        }
//                    },
//                    modifier = Modifier.padding(horizontal = 30.dp),
//                    style = TextStyle(
//                        fontFamily = AppFonts.instrumentSans,
//                        fontWeight = FontWeight.Normal,
//                        fontSize = 16.sp,
//                        textAlign = TextAlign.Center,
//                        color = Color.White
//                    )
//                )
            }
        }
        if (state.isLoading) {
            LoadingOverlay()
        }
    }
}

@Composable
fun MergedInputFields(
    emailValue: String,
    isEmailError: Boolean?,
    passwordValue: String,
    isPasswordError: Boolean?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.Center) {
        CustomTextField(
            text = emailValue,
            onValueChange = onEmailChange,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Unspecified,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Unspecified
            ),
            isError = isEmailError,
            labelText = "Email",
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
        )
        PasswordField(
            text = passwordValue,
            modifier = Modifier
                .offset(y = (-8).dp)
                .zIndex(1f),
            isError = isPasswordError,
            labelText = "",
            placeHolderText = "Password",
            shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
            onValueChange = onPasswordChange
        )
    }
}

@Composable
fun Line(
    modifier: Modifier = Modifier,
    lineColor: Color = Color.Black,
    strokeWidth: Float = 5f
) {
    Canvas(modifier = modifier.fillMaxWidth()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawLine(
            start = Offset(x = 0f, y = canvasHeight / 2),
            end = Offset(x = canvasWidth, y = canvasHeight / 2),
            color = lineColor,
            strokeWidth = strokeWidth,
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginContent(state = LoginState(), onIntent = {})
}
