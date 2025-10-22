package com.example.gathr.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.gathr.R
import com.example.gathr.ui.theme.AppFonts
import kotlinx.coroutines.delay

@Composable
fun FadingPopup(
    title: String,
    message: String?,
    showPopup: Boolean,
    onDismiss: () -> Unit,
    displayDurationMillis: Long = 4000,
    titleColor: Color,
    progressColor: Color = Color(0xFFD9D9D9),
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        LaunchedEffect(message) {
            if (message != null) {
                delay(displayDurationMillis)
                onDismiss()
            }
        }
//        if (showPopup) Popup(
//            onDismissRequest = onDismiss,
//        ) {

        val colorStops = listOf(
            0f to Color(0xFF312245),
            1f to Color(0xFF7954AB),
        )
        val backgroundBrush = Brush.linearGradient(
            colorStops = colorStops.toTypedArray(),
            start = Offset(x = Float.POSITIVE_INFINITY / 2f, y = 0f),
            end = Offset(x = Float.POSITIVE_INFINITY / 2f, y = Float.POSITIVE_INFINITY)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundBrush)
                .padding(start = 15.dp, end = 8.dp, top = 15.dp),
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    modifier = Modifier
                        .size(22.dp)
                        .padding(top = 5.dp),
                    painter = painterResource(R.drawable.info),
                    contentDescription = "Info Icon",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(top = 2.5.dp),
                ) {
                    Text(
                        title, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        message!!, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 13.sp,
                            letterSpacing = TextUnit(1f, TextUnitType.Sp),
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        )
                    )
                }
                IconButton(
                    onClick = onDismiss,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(top = 2.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Icon(
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Close notification",
                            tint = Color.White
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            ShrinkingLine(
                modifier = Modifier
                    .height(10.dp)
                    .padding(start = 10.dp, end = 17.dp),
                lineColor = progressColor,
                strokeWidth = 12f,
                durationMillis = 5000
            )
        }
    }
}
//}

@Composable
fun ShrinkingLine(
    modifier: Modifier = Modifier, durationMillis: Int = 3000, // How long the shrink takes
    lineColor: Color = Color.Red, strokeWidth: Float = 3f
) {
    // 1. Animatable state for the length fraction (1.0 = full, 0.0 = zero)
    val lengthFraction = remember { Animatable(initialValue = 1f) }

    // 2. Launch the animation when the composable appears
    LaunchedEffect(Unit) {
        lengthFraction.animateTo(
            targetValue = 0f, animationSpec = tween(durationMillis = durationMillis)
        )
    }

    // 3. Draw on Canvas
    Canvas(modifier = modifier.fillMaxWidth()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val currentFraction = lengthFraction.value // Get current animation value

        // 4. Calculate line properties based on fraction
        val currentLineWidth = canvasWidth * currentFraction
        val startX = (canvasWidth - currentLineWidth) / 2f
        val endX = startX + currentLineWidth
        val yPos = size.height - (strokeWidth / 2f)

        // Draw the line
        drawLine(
            color = lineColor,
            start = Offset(x = startX, y = yPos),
            end = Offset(x = endX, y = yPos),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round // Optional: makes ends rounded
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FadingPopupPreview() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FadingPopup(
            title = "Google Already Linked",
            message = "This Google account is already linked. Please log in.",
            onDismiss = {},
            showPopup = true,
            titleColor = Color(0xFFF36F44),
        )
        Spacer(modifier = Modifier.height(10.dp))
        FadingPopup(
            title = "Account Created",
            message = "Your account is created successfully. Please Login.",
            onDismiss = {},
            showPopup = true,
            titleColor = Color(0xFF7BD751),
            progressColor = Color(0xFF7BD751)
        )
    }
}

