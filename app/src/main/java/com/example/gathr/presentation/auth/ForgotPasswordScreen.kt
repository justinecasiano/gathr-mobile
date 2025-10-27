package com.example.gathr.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
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
import com.example.gathr.core.ui.ClearTextField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun ForgotPasswordScreen(
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
) {
    var email by remember { mutableStateOf("") }

//    ScreenWithTopBarButton(
//        containerColor = Color(0xFF261A36),
//        topBarColor = Color(0xFF261A36),
//        titleColor = Color(0xFFBFB6CA),
//        iconColor = Color(0xFFBFB6CA),
//        isBackButton = false,
//        onClick = onNavigateBack,
//        content = {
//            Column(
//                modifier = Modifier.padding(horizontal = 20.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                Text(
//                    "Forgot Password?", modifier = Modifier.fillMaxWidth(), style = TextStyle(
//                        fontFamily = AppFonts.rethinkSans,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color(0xFFF6F6F6)
//                    )
//                )
//                ClearTextField(
//                    email,
//                    onValueChange = { it -> email = it },
//                    onClick = { email = "" },
//                    placeholder = "Email",
//                )
//                Text(
//                    "Enter your email address to receive a link to reset your password.",
//                    modifier = Modifier.fillMaxWidth(),
//                    style = TextStyle(
//                        fontFamily = AppFonts.instrumentSans,
//                        fontSize = 15.sp,
//                        fontWeight = FontWeight.Normal,
//                        color = Color.White,
//                    )
//                )
//            }
//        },
//        componentOutside = {
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight(),
//                contentAlignment = Alignment.BottomCenter
//            ) {
//                Column(
//                    modifier = Modifier
//                        .fillMaxHeight(0.15f)
//                        .fillMaxSize()
//                ) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .fillMaxHeight()
//                            .dropShadow(
//                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
//                                shadow = Shadow(
//                                    radius = 10.dp,
//                                    spread = 6.dp,
//                                    color = Color(0x40000000),
//                                    offset = DpOffset(x = 4.dp, 3.dp)
//                                )
//                            )
//                            .background(
//                                color = Color(0xFF261A36),
//                                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
//                            )
//                            .padding(start = 20.dp, end = 20.dp, bottom = 58.dp),
//                        contentAlignment = Alignment.BottomCenter
//                    ) {
//                        ElevatedButton(
//                            buttonColor = AppColors.primary,
//                            outlineColor = AppColors.primaryDark,
//                            text = "NEXT",
//                            isEnabled = true,
//                            onClick = {},
//                            bottomBorderThickness = 5.dp,
//                            textStyle = TextStyle(
//                                fontFamily = AppFonts.instrumentSans,
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 14.sp,
//                                color = Color.White
//                            ),
//                        )
//                    }
//
//                }
//            }
//        }
//    )
}

@Composable
fun PasswordResetStatus(modifier: Modifier = Modifier) {
//    StatusView(
//        title = "Password Reset Link",
//        buttonText = "GO BACK TO LOGIN",
//        isBackButton = false,
//        onClick = {},
//        message = {
//            Text(
//                buildAnnotatedString {
//                    append("We have sent a change password link to your email ")
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("umak@umak.edu.ph")
//                    }
//                },
//                style = TextStyle(
//                    fontFamily = AppFonts.instrumentSans,
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 15.sp,
//                    color = Color.White
//                )
//            )
//        })
}

@Preview
@Composable
private fun ForgotPasswordPreview() {
    Scaffold { paddingValues ->
        ForgotPasswordScreen(
            paddingValues,
            onNavigateBack = {},
        )
    }
}
