package com.example.gathr.presentation.moderator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AccountScreen() {
    // TODO: use lazy column here (has scrolling and loads lazily)
//    LazyColumn(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        item {
    Text("Account Screen")
//        }
//    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    AccountScreen()
}