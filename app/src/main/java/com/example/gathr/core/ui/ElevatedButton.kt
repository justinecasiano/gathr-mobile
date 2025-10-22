package com.example.gathr.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun ElevatedButton(
    buttonColor: Color,
    outlineColor: Color,
    text: String,
    textStyle: TextStyle,
    height: Dp = 50.dp,
    modifier: Modifier = Modifier,
    isContrast: Boolean = true,
    buttonShape: RoundedCornerShape = RoundedCornerShape(14.dp),
    bottomBorderThickness: Dp = 5.dp,
    shouldAddShadow: Boolean = true,
    onClick: () -> Unit = {}
) {
    val shadowEffect =
        Shadow(
            color = Color.Black.copy(alpha = 0.25f),
            offset = Offset(x = 0f, y = 4f),
            blurRadius = 4f
        )

    val style = if (shouldAddShadow) textStyle.copy(shadow = shadowEffect) else textStyle

    Box(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .height(height)
                .matchParentSize()
                .background(
                    color = outlineColor,
                    shape = buttonShape
                )
        )

        OutlinedButton(
            modifier = Modifier
                .height(height)
                .fillMaxWidth()
                .then(
                    if (isContrast) Modifier.padding(bottom = bottomBorderThickness) else Modifier.padding(
                        start = 1.dp,
                        end = 1.dp,
                        top = 1.dp,
                        bottom = bottomBorderThickness
                    )
                ),
            onClick = onClick,
            shape = buttonShape,
            border = null,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = buttonColor,
            ),
        ) {
            Text(text, style = style)
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

