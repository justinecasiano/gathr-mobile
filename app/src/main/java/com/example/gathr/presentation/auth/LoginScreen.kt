package com.example.gathr.presentation.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
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
import com.example.gathr.R
import com.example.gathr.core.ui.CustomInputField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.PasswordField
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
    onNavigateForgotPassword: () -> Unit,
    onNavigateAfterLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

//    ScreenWithTopBarButton(
//        containerColor = Color(0xFF261A36),
//        topBarColor = Color(0xFF261A36),
//        titleColor = Color(0xFFBFB6CA),
//        iconColor = Color(0xFFBFB6CA),
//        isBackButton = true,
//        onClick = onNavigateBack,
//        title = "Enter your details",
//        content = {
//            Column(
//                modifier = Modifier.padding(horizontal = 20.dp)
//            ) {
//                Spacer(modifier = Modifier.height(8.dp))
//                MergedInputFields(
//                    usernameValue = username,
//                    passwordValue = password,
//                    onUsernameChange = { it -> username = it },
//                    onPasswordChange = { it -> password = it },
//                )
//                Spacer(modifier = Modifier.height(15.dp))
//                ElevatedButton(
//                    buttonColor = AppColors.primary,
//                    text = "SIGN IN",
//                    isEnabled = username.isNotEmpty() && password.isNotEmpty(),
//                    onClick = {},
//                    bottomBorderThickness = 0.dp,
//                    textStyle = TextStyle(
//                        fontFamily = AppFonts.instrumentSans,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 14.sp,
//                        color = Color.White
//                    ),
//                )
//                Spacer(modifier = Modifier.height(15.dp))
//                TextButton(
//                    onClick = onNavigateForgotPassword,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                ) {
//                    Text(
//                        "FORGOT PASSWORD",
//                        style = TextStyle(
//                            fontFamily = AppFonts.instrumentSans,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 13.sp,
//                            textAlign = TextAlign.Center,
//                            color = Color(0xFFF7906E)
//                        )
//                    )
//                }
//                Spacer(modifier = Modifier.height(15.dp))
//                Row(
//                    modifier = Modifier.padding(horizontal = 30.dp),
//                    horizontalArrangement = Arrangement.spacedBy(5.dp)
//                ) {
//                    Line(
//                        modifier = Modifier
//                            .fillMaxWidth(0.45f)
//                            .align(Alignment.CenterVertically),
//                        lineColor = Color(0xFF574272),
//                        strokeWidth = 6f
//                    )
//                    Text(
//                        "OR",
//                        style = TextStyle(
//                            fontFamily = AppFonts.instrumentSans,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 13.sp,
//                            textAlign = TextAlign.Center,
//                            color = Color(0xFF574272)
//                        )
//                    )
//                    Line(
//                        modifier = Modifier.align(Alignment.CenterVertically),
//                        lineColor = Color(0xFF574272),
//                        strokeWidth = 6f
//                    )
//                }
//                Spacer(modifier = Modifier.height(32.dp))
//                ElevatedButton(
//                    buttonColor = Color(0xFF261A36),
//                    outlineColor = Color(0xFF574272),
//                    text = "SIGN IN WITH GOOGLE",
//                    isContrast = false,
//                    onClick = {},
//                    bottomBorderThickness = 6.dp,
//                    icon = {
//                        Image(
//                            painter = painterResource(R.drawable.google_icon),
//                            contentDescription = "Google Icon",
//                            modifier = Modifier
//                                .size(24.dp)
//                                .padding(end = 5.dp)
//                        )
//                    },
//                    textStyle = TextStyle(
//                        fontFamily = AppFonts.instrumentSans,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 13.sp,
//                        color = Color.White
//                    ),
//                )
//                Spacer(modifier = Modifier.height(15.dp))
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
//                        fontSize = 15.sp,
//                        textAlign = TextAlign.Center,
//                        color = Color.White
//                    )
//                )
//            }
//        }
//    )
}

@Composable
fun MergedInputFields(
    usernameValue: String,
    passwordValue: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    Column {
        CustomInputField(
            text = usernameValue,
            placeholder = "Email or username",
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp),
            onValueChange = onUsernameChange
        )
        PasswordField(
            text = passwordValue,
            modifier = Modifier
                .offset(y = -3.dp)
                .zIndex(1f),
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
    Scaffold { paddingValues ->
        LoginScreen(
            paddingValues,
            onNavigateBack = {},
            onNavigateAfterLogin = {},
            onNavigateForgotPassword = {},
        )
    }
}
