package com.example.gathr.presentation.shared

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.SearchTextField
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.dummyEvents

data class Tab(
    val title: String,
    @DrawableRes val icon: Int,
)

val events = dummyEvents

@Composable
fun EventsScreen(isParticipant: Boolean = true) {
    val tabs = listOf(
        Tab("Upcoming", R.drawable.upcoming_icon),
        Tab("Ongoing", R.drawable.ongoing_icon),
        Tab("Completed", R.drawable.completed_icon)
    )
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxHeight()
            .padding(top = 30.dp)
            .padding(horizontal = 30.dp)
    ) {
        SearchTextField(
            "",
            onValueChange = {},
            onClearValue = {},
            placeholderText = "Search for events",
        )
        Spacer(Modifier.height(10.dp))
        SecondaryTabRow(
            containerColor = Color(0xFFF6F6F6),
            selectedTabIndex = selectedTabIndex,
            indicator = {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(selectedTabIndex)
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(7.5.dp)),
                    height = 5.dp,
                    color = Color(0xFF473163)
                )
            },
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = (selectedTabIndex == index),
                    selectedContentColor = Color(0xFF473163),
                    unselectedContentColor = Color(0xFF473163),
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Column {
                            Image(
                                painterResource(tab.icon),
                                contentDescription = tab.title,
                                modifier = Modifier.size(65.dp)
                            )
                            Text(
                                text = tab.title,
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                ),
                            )
                        }
                    }
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier.height(135.dp),
                painter = painterResource(R.drawable.events_banner),
                contentDescription = "Event Banner",
                contentScale = ContentScale.FillHeight
            )
            Text(
                "Welcome to Gathr, Angela Mae!",
                style = TextStyle(
                    fontFamily = AppFonts.instrumentSans,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    color = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .align(Alignment.CenterStart)
                    .padding(start = 30.dp, top = 25.dp)
            )
        }
        Spacer(Modifier.height(3.dp))
        Text(
            "Popular",
            style = TextStyle(
                fontFamily = AppFonts.instrumentSans,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                color = Color(0xFF232222)
            ),
        )
        Spacer(Modifier.height(15.dp))
        LazyColumn{
        }
        LargeEventCard(
            dummyEvents[1],
            onCardClicked = {},
            onTextButtonClick = {}
        )
        Spacer(Modifier.height(20.dp))
        LargeEventCard(
            dummyEvents[2],
            onCardClicked = {},
            onTextButtonClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EventsScreenPreview() {
    EventsScreen()
}
