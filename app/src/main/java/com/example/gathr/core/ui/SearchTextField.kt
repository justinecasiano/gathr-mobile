package com.example.gathr.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.ui.theme.AppFonts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTextField(
    text: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "",
    onClearValue: () -> Unit,
    modifier: Modifier = Modifier,
    isModerator: Boolean = false
) {
    val moderatorPurpleDark = Color(0xFF312245)
    val moderatorPurpleLight = Color(0xFF7954AB)

    val backgroundModifier = if (isModerator) {
        Modifier
            .border(2.dp, moderatorPurpleDark, RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(moderatorPurpleDark, moderatorPurpleLight)
                ),
                shape = RoundedCornerShape(20.dp)
            )
    } else {
        Modifier
    }

    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .then(backgroundModifier),
        textStyle = TextStyle(
            fontFamily = AppFonts.rethinkSans,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = if (isModerator) Color.White else Color.Black,
        ),
        placeholder = {
            Text(
                placeholderText,
                style = TextStyle(
                    fontFamily = AppFonts.rethinkSans,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isModerator) Color.White.copy(alpha = 0.6f) else Color.Gray,
                ),
            )
        },
        shape = RoundedCornerShape(20.dp),

        leadingIcon = {
            Icon(
                painter = painterResource(R.drawable.search_icon),
                tint = if (isModerator) Color.White else Color.Black,
                contentDescription = "Search"
            )
        },

        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(
                    onClick = onClearValue
                ) {
                    Icon(
                        painter = painterResource(R.drawable.clear_icon_filled),
                        tint = if (isModerator) Color.White.copy(alpha = 0.7f) else Color(0xFFA9A9A9),
                        contentDescription = "Clear Text"
                    )
                }
            }
        },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = if (isModerator) Color.White else Color.Black,

            unfocusedBorderColor = if (isModerator) moderatorPurpleDark else Color(0xFFD7D7D7),
            focusedBorderColor = if (isModerator) moderatorPurpleDark else Color(0xFFD7D7D7),

            focusedTextColor = if (isModerator) Color.White else Color.Black,
            unfocusedTextColor = if (isModerator) Color.White.copy(alpha = 0.9f) else Color.Black.copy(
                alpha = 0.9f
            ),

            unfocusedContainerColor = if (isModerator) Color.Transparent else Color(0xFFFCFCFC),
            focusedContainerColor = if (isModerator) Color.Transparent else Color(0xFFFCFCFC),
        ),
    )
}
