package com.example.gathr.core.ui
//
//import androidx.annotation.DrawableRes
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.gathr.R
//import com.example.gathr.ui.theme.AppColors
//import com.example.gathr.ui.theme.AppFonts
//
//@Composable
//fun StatusView(
//    title: String,
//    buttonText: String,
//    isBackButton: Boolean = true,
//    @DrawableRes id: Int = R.drawable.parachute,
//    message: @Composable () -> Unit,
//    onClick: () -> Unit = {},
//) {
//    Box(modifier = Modifier.fillMaxWidth()) {
//        Image(
//            painter = painterResource(id = id),
//            contentDescription = "Background",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.FillBounds
//        )
//        ScreenWithTopBarButton(
//            containerColor = Color.Transparent,
//            topBarColor = Color.Transparent,
//            titleColor = Color(0xFFBFB6CA),
//            iconColor = Color(0xFFBFB6CA),
//            isBackButton = isBackButton,
//            onClick = {},
//            actions = {},
//            content = {
//                Box(
//                    modifier = Modifier
//                        .fillMaxHeight(0.55f)
//                        .padding(horizontal = 60.dp),
//                    contentAlignment = Alignment.BottomCenter
//                ) {
//                    Column {
//                        Text(
//                            title,
//                            style = TextStyle(
//                                fontFamily = AppFonts.rethinkSans,
//                                fontWeight = FontWeight.Bold,
//                                fontSize = 50.sp,
//                                lineHeight = 40.sp,
//                                color = Color.White
//                            )
//                        )
//                        Spacer(modifier = Modifier.height(20.dp))
//                        message()
//                    }
//                }
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(bottom = 35.dp, start = 40.dp, end = 40.dp),
//                    contentAlignment = Alignment.BottomCenter
//                ) {
//                    ElevatedButton(
//                        buttonColor = Color(0xFFF7906E),
//                        outlineColor = AppColors.secondaryDark,
//                        text = buttonText,
//                        bottomBorderThickness = 5.dp,
//                        textStyle = TextStyle(
//                            fontFamily = AppFonts.instrumentSans,
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 14.sp,
//                            color = Color.Black
//                        ),
//                    )
//                }
//            }
//        )
//    }
//}
//
//@Preview
//@Composable
//private fun StatusViewPreview() {
////    StatusView(
////        title = "No Account Found",
////        buttonText = "GO BACK TO LOGIN",
////        isBackButton = false,
////        onClick = {},
////        message = {
////            Text(
////                buildAnnotatedString {
////                    append("We couldn't find an account associated with ")
////                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
////                        append("umak@umak.edu.ph")
////                    }
////                },
////                style = TextStyle(
////                    fontFamily = AppFonts.instrumentSans,
////                    fontWeight = FontWeight.Normal,
////                    fontSize = 15.sp,
////                    color = Color.White
////                )
////            )
////        })
//
//    StatusView(
//        title = "Verification Code Sent",
//        buttonText = "VERIFY EMAIL",
//        isBackButton = true,
//        onClick = {},
//        message = {
//            Text(
//                buildAnnotatedString {
//                    append("We have sent a verification code to your email ")
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
//
////    StatusView(
////        title = "Password Reset Link",
////        buttonText = "GO BACK TO LOGIN",
////        isBackButton = false,
////        onClick = {},
////        message = {
////            Text(
////                buildAnnotatedString {
////                    append("We have sent a change password link to your email ")
////                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
////                        append("umak@umak.edu.ph")
////                    }
////                },
////                style = TextStyle(
////                    fontFamily = AppFonts.instrumentSans,
////                    fontWeight = FontWeight.Normal,
////                    fontSize = 15.sp,
////                    color = Color.White
////                )
////            )
////        })
//}