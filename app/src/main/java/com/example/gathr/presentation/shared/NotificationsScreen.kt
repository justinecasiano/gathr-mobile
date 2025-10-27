package com.example.gathr.presentation.shared

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NotificationsScreen() {
    // TODO: NotificationsScreen is present in both moderator and participant but differs
    // in background colors, use the boolean to assign the proper colors
    // TODO: use lazy column here (has scrolling and loads lazily)
//    Scaffold { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxWidth(),
//        ) {
//            item {
                Text("Notifications Screen")
//            }
//        }
//    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationScreenPreview() {
    NotificationsScreen()
}