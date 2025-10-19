package com.example.gathr.presentation.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(paddingValues: PaddingValues, onNavigate: () -> Unit) {
    Column(modifier = Modifier.padding(paddingValues)) {
        Text("Welcome to the Login Screen")
    }
}