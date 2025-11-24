package com.example.gathr.presentation.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SmallEventCard(
    imageResId: Int,
    eventName: String,
    eventDate: String,
    slotsLeft: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Image container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp) // Adjust height as needed
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = eventName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            }

            // Event details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = eventName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = eventDate,
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = " | ",
                        fontSize = 14.sp,
                        color = Color.LightGray
                    )
                    Text(
                        text = "$slotsLeft slots left",
                        fontSize = 14.sp,
                        color = Color(0xFFFF4D4D), // Red color for slots
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF222222) // Dark background for preview
@Composable
fun SmallEventCardPreview() {
    // Replace with an actual image resource in your project
    val sampleImageResId = android.R.drawable.ic_menu_gallery

    Box(modifier = Modifier.padding(16.dp)) {
        SmallEventCard(
            imageResId = sampleImageResId, // Use the image from the prompt
            eventName = "4th CCIS Hackathon",
            eventDate = "Oct. 6, 2025",
            slotsLeft = 12
        )
    }
}
