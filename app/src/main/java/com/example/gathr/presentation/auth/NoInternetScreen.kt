package com.example.gathr.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.ui.theme.AppFonts

@Composable
fun NoInternetScreen() {
    val colorStops = listOf(
        0f to Color(0xFF6E4C9C),
        0.7f to Color(0xFF261A36),
    )
    val backgroundBrush = Brush.linearGradient(
        colorStops = colorStops.toTypedArray(),
        start = Offset(x = Float.POSITIVE_INFINITY / 2f, y = 0f),
        end = Offset(x = Float.POSITIVE_INFINITY / 2f, y = Float.POSITIVE_INFINITY),
    )

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(backgroundBrush)
                .padding(paddingValues)
                .padding(horizontal = 48.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .padding(bottom = 100.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter = painterResource(R.drawable.no_internet),
                                contentDescription = "No Internet",
                                contentScale = ContentScale.FillBounds
                            )
                            Text(
                                "You're offline",
                                textAlign = TextAlign.Center,
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                "Turn on Wi-Fi or mobile data",
                                textAlign = TextAlign.Center,
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
            Column {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedButton(
                        buttonColor = Color(0xFF261A36),
                        outlineColor = Color(0xFF574272),
                        text = "TRY AGAIN",
                        onClick = {},
                        isContrast = false,
                        textStyle = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White,
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.refresh),
                                contentDescription = "Google Icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(scaleX = -1f, scaleY = 1f)
                                    .padding(end = 5.dp),
                                tint = Color.White,
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun NoInternetScreenPreview() {
    NoInternetScreen()
}