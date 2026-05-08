package com.example.gathr.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun StatusScreen(
    title: String,
    buttonText: String,
    hasTopBar: Boolean = false,
    message: @Composable () -> Unit,
    @DrawableRes id: Int = R.drawable.parachute,
    onNavigateBack: () -> Unit = {},
    onNavigateNext: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = if (hasTopBar) {
                {
                    CenterAlignedTopAppBar(
                        modifier = Modifier.padding(top = 10.dp, start = 10.dp),
                        title = {},
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    modifier = Modifier.size(37.dp),
                                    tint = Color(0xFFBFB6CA),
                                    painter = painterResource(R.drawable.arrow_back),
                                    contentDescription = "Back"
                                )
                            }
                        },
                    )
                }
            } else {
                {}
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(paddingValues)
                    .padding(horizontal = 48.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight(0.85f),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(100.dp))
                        Text(
                            title,
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 48.sp,
                                lineHeight = 40.sp,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(15.dp))
                        message()
                    }
                }
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ElevatedButton(
                            buttonColor = Color(0xFFF7906E),
                            outlineColor = AppColors.secondaryDark,
                            text = buttonText,
                            onClick = onNavigateNext,
                            bottomBorderThickness = 5.dp,
                            textStyle = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0E0E2C)
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StatusScreenPreview() {
    StatusScreen(
        title = "Password changed",
        buttonText = "NEXT",
        hasTopBar = true,
        message = {
            Text(
                buildAnnotatedString {
                    append("You may now sign in to your account")
                },
                style = TextStyle(
                    fontFamily = AppFonts.instrumentSans,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        },
    )
//    StatusScreen(
//        title = "Reset Password",
//        buttonText = "NEXT",
//        message = {
//            Text(
//                buildAnnotatedString {
//                    append("We have sent a verification code to your email ")
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("gathr2025@gmail.com")
//                    }
//                },
//                style = TextStyle(
//                    fontFamily = AppFonts.instrumentSans,
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 16.sp,
//                    color = Color.White
//                )
//            )
//        },
//    )
//    StatusScreen(
//        title = "Account created",
//        buttonText = "NEXT",
//        message = {
//            Text(
//                buildAnnotatedString {
//                    append("You may now sign in to your account")
//                },
//                style = TextStyle(
//                    fontFamily = AppFonts.instrumentSans,
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 16.sp,
//                    color = Color.White
//                )
//            )
//        },
//    )
//    StatusScreen(
//        title = "Verification Code Sent",
//        buttonText = "NEXT",
//        message = {
//            Text(
//                buildAnnotatedString {
//                    append("We have sent a verification code to your email ")
//                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
//                        append("gathr2025@gmail.com")
//                    }
//                },
//                style = TextStyle(
//                    fontFamily = AppFonts.instrumentSans,
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 16.sp,
//                    color = Color.White
//                )
//            )
//        },
//    )
}