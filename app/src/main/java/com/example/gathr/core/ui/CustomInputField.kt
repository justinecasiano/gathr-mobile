package com.example.gathr.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.example.gathr.ui.theme.AppColors
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.R

@Composable
fun CustomInputField(
    text: String = "",
    placeholder: String,
    shape: RoundedCornerShape = RoundedCornerShape(15.dp),
    outlineColor: Color = Color(0xFF574272),
    visualTransformation: VisualTransformation? = null,
    onValueChange: (String) -> Unit = {},
    iconButton: @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val shouldAddIcon = iconButton == null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.5.dp,
                color = if (isFocused) AppColors.primaryDark else outlineColor,
                shape = shape
            )
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            textStyle = TextStyle(
                fontFamily = AppFonts.rethinkSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
            ),
            placeholder = {
                Text(
                    placeholder, style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W600,
                        color = Color.White.copy(alpha = 0.2f),
                    )
                )
            },
            shape = shape,
            singleLine = true,
            trailingIcon = if (shouldAddIcon) {
                { if (text.isNotEmpty()) iconButton?.invoke() }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White.copy(alpha = 0.9f),
                unfocusedTextColor = Color.White.copy(alpha = 0.8f),
                cursorColor = Color.White,
                unfocusedContainerColor = Color(0xFF312245),
                focusedContainerColor = Color(0xFF312245)
            ),
        )
    }
}

@Composable
fun PasswordField(
    text: String = "",
    placeholder: String = "Password",
    outlineColor: Color = Color(0xFF574272),
    iconColor: Color = Color(0xFFF6835E),
    onValueChange: (String) -> Unit = {}
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    CustomInputField(
        text,
        placeholder,
        outlineColor = outlineColor,
        onValueChange = onValueChange,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        iconButton = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(if (isPasswordVisible) R.drawable.visibility else R.drawable.visibility_off),
                    contentDescription = "Toggle Password Visibility",
                    tint = iconColor
                )
            }
        })
}

@Composable
fun ClearTextField(
    text: String = "",
    placeholder: String = "",
    outlineColor: Color = Color(0xFF574272),
    iconColor: Color = Color(0xFFF6835E),
    onValueChange: (String) -> Unit = {},
    onClick: () -> Unit = {},
) {
    CustomInputField(
        text,
        placeholder,
        outlineColor = outlineColor,
        onValueChange = onValueChange,
        iconButton = {
            IconButton(onClick = onClick) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.clear),
                    contentDescription = "Clear Input Text",
                    tint = iconColor
                )
            }
        })
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomInputField() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF261A36)),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CustomInputField(placeholder = "First Name")
            CustomInputField(placeholder = "Last Name")
            CustomInputField(placeholder = "Email")
            CustomInputField(placeholder = "@Username")
            PasswordField() { }
            PasswordField(placeholder = "Confirm Password") { }
            PasswordField("Hello") { }
            ClearTextField("Hello") { }
        }
    }
}