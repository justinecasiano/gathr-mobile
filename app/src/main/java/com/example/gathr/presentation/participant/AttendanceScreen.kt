package com.example.gathr.presentation.participant

import com.example.gathr.data.model.Event
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gathr.core.ui.Alert
import com.example.gathr.core.ui.LoadingOverlay
import com.example.gathr.presentation.main.UserEffect
import com.example.gathr.presentation.main.UserIntent
import com.example.gathr.presentation.main.UserState
import com.example.gathr.presentation.main.UserViewModel
import com.example.gathr.presentation.main.ViewEventContent

data class RegisteredUser(
    val name: String,
    val date: String,
    val ticket: String
)

@Composable
fun AttendanceScreen(viewModel: UserViewModel, onNavigateBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                UserEffect.NavigateToNext -> {}
                UserEffect.NavigateBack -> onNavigateBack()
                UserEffect.NavigateLogout -> {}
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        AttendanceContent(
            state = state,
            onIntent = viewModel::handleIntent,
        )

        if (state.isLoading) {
            LoadingOverlay()
        }
        when {
            state.actionError.isNotBlank() -> {
                Alert(
                    title = state.actionTitle.ifBlank { "Error" },
                    message = state.actionError,
                    onDismissRequest = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                    confirmButtonText = "Ok",
                    onConfirmClicked = { viewModel.handleIntent(UserIntent.ActionErrorChanged("")) },
                )
            }
        }
    }
}

@Composable
fun AttendanceContent(
    state: UserState,
    onIntent: (UserIntent) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val registeredList = remember {
        listOf(
            RegisteredUser("Angela Cabrera", "October 13, 2025", "1U7SJLEJYK"),
            RegisteredUser("Juan Dela Cruz", "October 12, 2025", "1A2B3C4D5E"),
            RegisteredUser("Maria Santos", "October 10, 2025", "7H8G9F0KLM"),
            RegisteredUser("Peter Reyes", "October 08, 2025", "Z9Y8X7W6V5"),
            RegisteredUser("Anne Lopez", "October 09, 2025", "AA12BB34CC"),
            RegisteredUser("Ben Villanueva", "October 11, 2025", "VV55WW66XX"),
            RegisteredUser("Catherine Uy", "October 07, 2025", "CC11DD22EE"),
            RegisteredUser("Daniel Ramirez", "October 06, 2025", "RR44TT55YY"),
            RegisteredUser("Louise Tan", "October 05, 2025", "LT98PL34MN"),
            RegisteredUser("Zack Navarro", "October 04, 2025", "ZN45HY67QP")
        ).sortedBy { it.name }
    }

    val filteredList = remember(searchText, registeredList) {
        if (searchText.isBlank()) registeredList
        else registeredList.filter {
            it.name.contains(searchText, ignoreCase = true) ||
                    it.ticket.contains(searchText, ignoreCase = true) ||
                    it.date.contains(searchText, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.20f)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        ),
                        clip = false
                    )
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 4.dp)
                                .size(30.dp)
                                .clickable { onIntent(UserIntent.BackClicked) }
                        )

                        Text(
                            text = "Registered",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = rethinkSans
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Icon(
                            painter = painterResource(id = R.drawable.events_icon),
                            tint = Color.Black.copy(0.8f),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
//                            text = event.title,
                            text = "University of Makati's Infotechnolympics",
                            fontSize = 14.sp,
                            fontFamily = rethinkSans,
                            color = Color(0xFF333333)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .offset(y = (-35).dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatCard(
//                    number = "${event.countRegistered}",
                    number = "789",
                    label = "Coming",
                    bgColor = Brush.verticalGradient(
                        colors = listOf(Color(0xFF7B55A3), Color(0xFF583181))
                    ),
                    modifier = Modifier.width(100.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))

                StatCard(
//                    number = "${event.countCancelled}",
                    number = "192",
                    label = "Cancelled",
                    bgColor = Brush.verticalGradient(
                        colors = listOf(Color(0xFFFFBBA6), Color(0xFFF6835E))
                    ),
                    modifier = Modifier.width(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {

                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search in ${registeredList.size} participants") },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF1F1F1),
                        unfocusedContainerColor = Color(0xFFF1F1F1),
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(onClick = { }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter_icon),
                        contentDescription = "Filter",
                        tint = Color(0xFF5A2DA6),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .background(Color(0xFF6A3BA8), RoundedCornerShape(10.dp))
                    .padding(vertical = 12.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Name",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Date Registered",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Ticket",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .heightIn(max = 430.dp),
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredList) { user ->
                    RegisteredRow(user)
                }
            }

        }

        FloatingActionButton(
            onClick = { },
            containerColor = Color(0xFF6A3BA8),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 26.dp, bottom = 90.dp)
                .size(70.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.scan_icon),
                contentDescription = "Scan",
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )
        }


    }
}

@Composable
fun StatCard(
    number: String,
    label: String,
    bgColor: Brush,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                clip = false
            )
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(horizontal = 15.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun RegisteredRow(user: RegisteredUser) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(user.name, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(user.date, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(
                user.ticket,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }
        HorizontalDivider(
            color = Color(0xFFCCCCCC),
            thickness = 1.dp
        )
    }
}