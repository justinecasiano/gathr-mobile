package com.example.gathr.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gathr.R
import com.example.gathr.ui.theme.AppMisc
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    isWhiteBackground: Boolean = false,
    onLoaded: () -> Unit
) {
    val colorStops = listOf(
        0.5f to Color(0xFF412962),
        1f to Color(0xFF73483A),
        1f to Color(0xFF9A5D63),
    )
    val backgroundBrush = Brush.linearGradient(
        colorStops = colorStops.toTypedArray(),
        start = Offset(x = Float.POSITIVE_INFINITY / 2f, y = 0f),
        end = Offset(x = Float.POSITIVE_INFINITY / 2f, y = Float.POSITIVE_INFINITY)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 700,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(3000L)
        onLoaded()
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isWhiteBackground) Modifier.background(Color.White)
                    else Modifier.background(backgroundBrush)
                )
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gathr_logo),
                contentDescription = "Pulsing App Logo",
                modifier = Modifier
                    .size(200.dp)
                    .scale(scale)
            )

            val logo = if (isWhiteBackground)
                R.drawable.gathr_text
            else
                R.drawable.gathr_text_white

            Image(
                painter = painterResource(id = logo),
                contentDescription = "Brand Name",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AppMisc.screenBottomPadding)
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(onLoaded = { })
    }
}