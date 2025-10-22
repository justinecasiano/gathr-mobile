package com.example.gathr.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.ui.theme.AppFonts


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithTopBarButton(
    modifier: Modifier = Modifier,
    containerColor: Color,
    topBarColor: Color,
    titleColor: Color,
    iconColor: Color = Color.Black,
    title: String = "",
    titleStyle: TextStyle = TextStyle(
        color = titleColor,
        fontFamily = AppFonts.rethinkSans,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
    ),
    isBackButton: Boolean = true,
    onClick: () -> Unit,
    actions: @Composable (RowScope.() -> Unit) = {},
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = containerColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(title, style = titleStyle)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = topBarColor,
                    titleContentColor = titleColor,
                ),
                navigationIcon = {
                    if (isBackButton)
                        IconButton(onClick = onClick) {
                            Icon(
                                modifier = Modifier.size(32.dp),
                                tint = iconColor,
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    else
                        IconButton(onClick = onClick) {
                            Icon(
                                modifier = Modifier.size(35.dp),
                                tint = iconColor,
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close"
                            )
                        }
                },
                actions = { actions() }
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}

@Preview
@Composable
private fun ScreenWithButtonPreview() {
    ScreenWithTopBarButton(
        containerColor = Color(0xFF261A36),
        topBarColor = Color(0xFF261A36),
        titleColor = Color(0xFFBFB6CA),
        iconColor = Color(0xFFBFB6CA),
        isBackButton = true,
        title = "Enter your details",
        onClick = {},
        actions = {},
        content = {
            Box(modifier = Modifier.padding(20.dp)) {
                CustomInputField(placeholder = "Email")
            }
        }
    )
}