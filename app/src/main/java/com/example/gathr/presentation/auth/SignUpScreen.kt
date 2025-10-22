package com.example.gathr.presentation.auth

import android.R.attr.translationY
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.core.ui.CustomInputField
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.core.ui.PasswordField
import com.example.gathr.core.ui.ScreenWithTopBarButton
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts

@Composable
fun SignUpScreen() {
    var isChecked by remember { mutableStateOf(false) }

    ScreenWithTopBarButton(
        containerColor = Color(0xFF261A36),
        topBarColor = Color(0xFF261A36),
        titleColor = Color(0xFFBFB6CA),
        iconColor = Color(0xFFBFB6CA),
        isBackButton = true,
        onClick = {},
        actions = {},
        content = {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Create Account", modifier = Modifier.fillMaxWidth(), style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.size(5.dp))
                Row {
                    Box(modifier = Modifier.fillMaxWidth(0.48f)) {
                        CustomInputField(placeholder = "First Name")
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    CustomInputField(placeholder = "Last Name")
                }
                CustomInputField(placeholder = "Email")
                CustomInputField(placeholder = "@Username")
                PasswordField()
                PasswordField(placeholder = "Confirm Password")
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
//                    Checkbox(
//                        checked = isChecked, // 2. Pass the current state
//                        onCheckedChange = { newCheckedState -> // 3. Update state on change
//                            isChecked = newCheckedState
//                        }
//                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.30f)
                        .fillMaxSize()
                        .padding(top = 80.dp)
                ) {
                    TextButton(
                        onClick = {
                            println("Clicked on top button!")
                        },
                        modifier = Modifier
                    ) {
                        Text(
                            "I ALREADY HAVE AN ACCOUNT",
                            modifier = Modifier.fillMaxWidth(),
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFFF7906E)
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .dropShadow(
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                                shadow = Shadow(
                                    radius = 10.dp,
                                    spread = 6.dp,
                                    color = Color(0x40000000),
                                    offset = DpOffset(x = 4.dp, 3.dp)
                                )
                            )
                            .background(
                                color = Color(0xFF261A36),
                                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                            )
                            .padding(start = 20.dp, end = 20.dp, bottom = 35.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        ElevatedButton(
                            buttonColor = AppColors.primary,
                            outlineColor = AppColors.primaryDark,
                            text = "SIGN UP",
                            bottomBorderThickness = 5.dp,
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
        }
    )
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen()
}