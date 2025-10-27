package com.example.gathr.core.ui

import android.graphics.drawable.Icon
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ElevatedButton(
    buttonColor: Color,
    outlineColor: Color = Color.Unspecified,
    text: String,
    textStyle: TextStyle,
    height: Dp = 50.dp,
    modifier: Modifier = Modifier,
    isContrast: Boolean = true,
    isEnabled: Boolean = true,
    buttonShape: RoundedCornerShape = RoundedCornerShape(14.dp),
    bottomBorderThickness: Dp = 5.dp,
    shouldAddShadow: Boolean = true,
    icon: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val shadowEffect =
        Shadow(
            color = Color.Black.copy(alpha = 0.25f),
            offset = Offset(x = 0f, y = 4f),
            blurRadius = 4f
        )

    var isClicked by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val targetBottomPadding = when {
        isContrast && isClicked -> 0.dp
        isContrast && !isClicked -> bottomBorderThickness
        !isContrast && isClicked -> 1.dp
        else -> bottomBorderThickness
    }

    val animatedBottomPadding by animateDpAsState(
        targetValue = targetBottomPadding,
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseInOutCubic
        ),
        label = "bottomPaddingAnimation"
    )
    val style = textStyle.copy(
        shadow = if (shouldAddShadow) shadowEffect else Shadow.None,
//        color = if (isEnabled) disabledTextColor else textStyle.color
    )

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .alpha(if (isEnabled) 1f else 0.5f)
                .height(height)
                .matchParentSize()
                .background(
                    color = outlineColor,
                    shape = buttonShape
                )
        )
        OutlinedButton(
            modifier = Modifier
                .alpha(if (isEnabled) 1f else 0.5f)
                .height(height)
                .fillMaxWidth()
                .then(
                    if (isContrast) Modifier.padding(bottom = if (isClicked) 0.dp else animatedBottomPadding) else Modifier.padding(
                        start = 1.dp,
                        end = 1.dp,
                        top = 1.dp,
                        bottom = if (isClicked) 1.dp else animatedBottomPadding
                    )
                ),
            onClick = {
                if (!isClicked) {
                    isClicked = true
                    coroutineScope.launch {
                        delay(100L)
                        isClicked = false
                    }
                }
                onClick()
            },
            enabled = isEnabled,
            shape = buttonShape,
            border = null,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = buttonColor,
                contentColor = Color.Unspecified,
                disabledContainerColor = buttonColor
            ),
        ) {
            icon.invoke()
            Text(text, style = textStyle)
        }
    }
}

@Preview
@Composable
private fun ElevatedButtonPreview() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        ElevatedButton(
            buttonColor = Color(0xFFF7906E),
            outlineColor = AppColors.secondaryDark,
            modifier = Modifier.padding(horizontal = 40.dp),
            text = "SAMPLE 1",
            textStyle = TextStyle(
                fontFamily = AppFonts.instrumentSans,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.Black
            ),
        )
        Spacer(modifier = Modifier.height(10.dp))
        ElevatedButton(
            buttonColor = Color(0xFF261A36),
            outlineColor = Color(0xFF574272),
            modifier = Modifier.padding(horizontal = 40.dp),
            text = "SAMPLE 2",
            isContrast = false,
            textStyle = TextStyle(
                fontFamily = AppFonts.instrumentSans,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White
            ),
        )
    }
}

