package com.example.gathr.presentation.participant

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.BottomButton
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.Participant
import com.example.gathr.presentation.auth.sign_up.SignUpIntent
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.Utils.generateQrBitmap
import com.example.gathr.utils.toLocalDateTime
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrCodeScreen(participant: Participant? = null, event: Event? = null) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp),
            title = {
                Text(
                    "QR Code",
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    ),
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
            ),
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(
                        modifier = Modifier.size(37.dp),
                        tint = Color.Black,
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back"
                    )
                }
            },
        )
    }
    ) { paddingValues ->
        Column(Modifier.fillMaxHeight()) {
            Column(
                Modifier
                    .fillMaxHeight(0.84f)
                    .background(Color(0xFFF0F0F0))
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                        end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                    )
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .offset(y = (-12).dp)
                        .padding(horizontal = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.calendar_icon),
                        contentDescription = "Event name",
                        tint = Color(0xFF5D5D5D),
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        //                    "${event.title}"
                        "University of Makati's Infotechnolympics", style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5D5D5D),
                        ), modifier = Modifier.weight(1f, fill = false)
                    )
                }
                LazyColumn {
                    item {
                        Spacer(Modifier.height(3.dp))
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 30.dp)
                        ) {
                            Image(
                                modifier = Modifier.size(630.dp),
                                painter = painterResource(R.drawable.ticket_holder),
                                contentDescription = "QR Code ticket",
                                contentScale = ContentScale.FillBounds
                            )
                            Column(
                                modifier = Modifier
                                    .size(630.dp)
                                    .padding(vertical = 40.dp, horizontal = 40.dp)
                            ) {
                                Column(Modifier.weight(3f)) {
                                    Text(
                                        buildAnnotatedString {
                                            append("Attendee\n")
                                            withStyle(
                                                style = SpanStyle(
                                                    fontFamily = AppFonts.rethinkSans,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            ) {
                                                //                                append("${event.}")
                                                append("Angela Mae Cabrera")
                                            }
                                        }, style = TextStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Start,
                                            color = Color.White.copy(alpha = 0.8f)
                                        ), maxLines = 3, overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        buildAnnotatedString {
                                            append("Date & Time\n")
                                            withStyle(
                                                style = SpanStyle(
                                                    fontFamily = AppFonts.rethinkSans,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            ) {
                                                append("Oct 15, 2025 || 2:00 AM to 5:00 PM")
                                            }
                                        }, style = TextStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White.copy(alpha = 0.8f)
                                        ), modifier = Modifier.weight(1.2f, fill = false)
                                    )
                                }
                                Box(
                                    Modifier
                                        .padding(top = 50.dp)
                                        .weight(7.4f)
                                        .border(
                                            width = 5.dp,
                                            color = Color(0xFFF7906E),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    QrCodeDisplay("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyX2FiY18xMjMiLCJldmVudF9pZCI6ImV2dF83ODkiLCJ0aWVyIjoiVklQIiwiZXhwIjoxNzY1MTgwODAwfQ.f8A-g_TjUsy63jOM-fK2fT5vB-jYjYJ8qjZ-pXqJqXs")
                                }
                                Column(Modifier.weight(3.5f)) {
                                    Spacer(Modifier.height(20.dp))
                                    Text(
                                        //                            "${event.title}\n",
                                        "University of Makati's Infotechnolympics",
                                        style = TextStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 17.sp,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        ),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        modifier = Modifier.padding(horizontal = 20.dp),
                                        //                            "${event.location}"
                                        text = "UMak Auditorium, University of Makati, Makati City",
                                        style = TextStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            textAlign = TextAlign.Center
                                        ),
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Column(Modifier.background(Color.White)) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .border(2.dp, color = Color(0xFFD7D7D7))
                        .padding(horizontal = 55.dp)
                        .padding(top = 20.dp)
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    contentAlignment = Alignment.Center
                ) {
                    ElevatedButton(
                        text = "DOWNLOAD",
                        onClick = {},
                        isEnabled = true,
                        buttonColor = Color(0xFF7B55A3),
                        outlineColor = Color(0xFF4C2576),
                        textStyle = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        ),
                        isContrast = false,
                        buttonShape = RoundedCornerShape(20.dp),
                        shouldAddShadow = false,
                    )
                }
            }
        }
    }
}

@Composable
fun QrCodeDisplay(text: String) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(text) {
        qrBitmap = generateQrBitmap(text, margin = 3)
    }

    qrBitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "QR Code for an event",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )
    }
}

@Preview
@Composable
private fun QrCodeScreenPreview() {
    QrCodeScreen()
}