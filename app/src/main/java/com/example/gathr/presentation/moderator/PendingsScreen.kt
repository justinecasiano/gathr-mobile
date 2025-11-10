package com.example.gathr.presentation.moderator

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PendingsScreen() {
    // TODO: use column for upper part (sticks on scroll)
//    Scaffold { paddingValues ->
//        Column(modifier = Modifier.padding(paddingValues)) {
    // TODO: use lazy column here (has scrolling and loads lazily)
//            LazyColumn(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                item {
    Text("Pendings Screen")
//                }
//            }
//        }
//    }
}

@Preview(showBackground = true)
@Composable
private fun PendingsScreenPreview() {
    PendingsScreen()
}
