package com.example.gathr.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun AuthScreen(paddingValues: PaddingValues, onNavigate: () -> Unit) {

    val colorStops = listOf(
        0f to Color(0xFF6E4C9C),
        0.7f to Color(0xFF261A36),
    )
    val backgroundBrush = Brush.linearGradient(
        colorStops = colorStops.toTypedArray(),
        start = Offset(x = Float.POSITIVE_INFINITY / 2f, y = 0f),
        end = Offset(x = Float.POSITIVE_INFINITY / 2f, y = Float.POSITIVE_INFINITY)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Box(contentAlignment = Alignment.TopCenter) {

            Image(
                painter = painterResource(id = R.drawable.gathr_logo_ellipse),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(200.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.gathr_text),
                contentDescription = "Brand Name",
                modifier = Modifier
                    .size(150.dp)
                    .offset(y = 135.dp)
            )
        }
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            "One app. Every event.",
            textAlign = TextAlign.Center,
            fontFamily = AppFonts.rethinkSans,
            fontSize = 18.sp,
            fontWeight = FontWeight.W600,
            color = Color.White
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            ElevatedButton(
                buttonColor = Color(0xFFF7906E),
                outlineColor = AppColors.secondaryDark,
                modifier = Modifier.padding(horizontal = 40.dp),
                text = "GET STARTED",
                textStyle = TextStyle(
                    fontFamily = AppFonts.instrumentSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            ElevatedButton(
                buttonColor = Color(0xFF261A36),
                outlineColor = Color(0xFF574272),
                modifier = Modifier.padding(horizontal = 40.dp),
                text = "I ALREADY HAVE AN ACCOUNT",
                isContrast = false,
                textStyle = TextStyle(
                    fontFamily = AppFonts.instrumentSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                ),
            )
        }
    }
}

@Preview
@Composable
private fun AuthScreenPreview() {
    MaterialTheme {
        Scaffold { paddingValues ->
            AuthScreen(paddingValues, onNavigate = { })
        }
    }
}
