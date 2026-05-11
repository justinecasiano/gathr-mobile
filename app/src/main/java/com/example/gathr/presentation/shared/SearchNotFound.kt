package com.example.gathr.presentation.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.ui.theme.AppFonts

@Composable
fun SearchNotFound(modifier: Modifier = Modifier, isModerator: Boolean = false) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painterResource(R.drawable.not_found_moderator),
            contentDescription = "No result found",
            Modifier.size(110.dp),
            contentScale = ContentScale.FillHeight,
        )
        Text(
            "No result found",
            style = TextStyle(
                fontFamily = AppFonts.rethinkSans,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isModerator) Color.White else Color.Black,
            ),
        )
        Text(
            "We can't find any event matching your search.",
            style = TextStyle(
                fontFamily = AppFonts.rethinkSans,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isModerator) Color.White else Color.Black,
            ),
        )
    }
}