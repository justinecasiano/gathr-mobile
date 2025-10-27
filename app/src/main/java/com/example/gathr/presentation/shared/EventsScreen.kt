package com.example.gathr.presentation.shared

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun EventsScreen(isParticipant: Boolean = true) {
    // TODO: EventsScreen is present in both moderator and participant but differs
    // in background colors, use the boolean to assign the proper colors
    // TODO: use column for upper part (sticks on scroll)
//    Column() {
    // TODO: use lazy column here (has scrolling and loads lazily)
//        LazyColumn(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            item {
    Text("Events Screen")
//            }
//        }
//    }
}

@Preview(showBackground = true)
@Composable
private fun EventsScreenPreview() {
    EventsScreen()
}