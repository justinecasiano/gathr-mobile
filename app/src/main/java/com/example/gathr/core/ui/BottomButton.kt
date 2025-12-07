package com.example.gathr.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.presentation.auth.sign_up.SignUpIntent
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun BottomButton(
    bottomPadding: Dp,
    hasTextButton: Boolean = true,
    textButtonText: String = "I ALREADY HAVE AN ACCOUNT",
    textButtonStyle: TextStyle = TextStyle(
        fontFamily = AppFonts.instrumentSans,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        textAlign = TextAlign.Center,
        color = Color(0xFFF7906E)
    ),
    onTextButtonClick: () -> Unit = {},
    buttonText: String,
    buttonStyle: TextStyle = TextStyle(
        fontFamily = AppFonts.instrumentSans,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = Color.White
    ),
    isButtonEnabled: Boolean = false,
    onButtonClick: () -> Unit,
) {
    Column {
        if (hasTextButton) {
            TextButton(
                onClick = onTextButtonClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(textButtonText, style = textButtonStyle)
            }
        } else Spacer(modifier = Modifier.height(50.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .dropShadow(
                    shape = RoundedCornerShape(
                        topStart = 23.dp, topEnd = 23.dp
                    ), shadow = Shadow(
                        radius = 100.dp,
                        spread = 0.dp,
                        color = Color(0xFF000000).copy(alpha = 0.4f),
                        offset = DpOffset(x = 0.dp, (-10).dp)
                    )
                )
                .background(
                    color = Color(0xFF261A36),
                    RoundedCornerShape(topStart = 23.dp, topEnd = 23.dp)
                )
                .padding(horizontal = 48.dp)
                .padding(bottom = bottomPadding),
//                            .padding(bottom = AppMisc.screenBottomPadding),
            contentAlignment = Alignment.Center
        ) {
            ElevatedButton(
                isEnabled = isButtonEnabled,
                buttonColor = AppColors.primary,
                outlineColor = AppColors.primaryDark,
                text = buttonText,
                onClick = onButtonClick,
                bottomBorderThickness = 5.dp,
                textStyle = buttonStyle
            )
        }
    }
}

@Preview
@Composable
private fun BottomButtonPreview() {
    Scaffold { paddingValues ->
        Column(modifier = Modifier.fillMaxHeight()) {
            Column(modifier = Modifier.fillMaxHeight(0.75f)) { }
            BottomButton(
                paddingValues.calculateBottomPadding(),
                buttonText = "NEXT",
                onButtonClick = {},
                onTextButtonClick = {},
            )
        }
    }

}