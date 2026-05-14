package com.example.gathr.presentation.shared

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import coil3.size.Size
import com.example.gathr.R
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toAbbreviatedString
import com.example.gathr.utils.toDayOfMonth
import com.example.gathr.utils.toLocalDateTime
import com.example.gathr.utils.toMonthAbbreviation
import com.example.gathr.utils.toSimpleTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LargeEventCard(
    event: Event,
    onCardClicked: () -> Unit,
    onUpdateClicked: () -> Unit = {},
    onTextButtonClick: () -> Unit,
    role: String = "ATTENDEE",
    isETicket: Boolean = true,
    isRejectedOrRemoved: Boolean = false,
    isModerator: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(0.1f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 12.dp
        ),
        shape = RoundedCornerShape(20.dp),
        onClick = onCardClicked,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .fallback(R.drawable.placeholder_landscape)
                    .data(event.backgroundImage)
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .listener(
                        onStart = { request -> Log.d("IMAGE_LOAD", "Image started loading") },
                        onError = { request, result ->
                            Log.e(
                                "IMAGE_LOAD",
                                "FAILED: ${result.throwable.message}"
                            )
                        }
                    )
                    .build(),
                contentDescription = "Event Card Image",
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.placeholder_landscape)
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 18.dp, start = 27.dp, end = 27.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .background(
                                if (isModerator) Color(0xFF312245).copy(alpha = 0.9f) else Color.White.copy(
                                    alpha = 0.9f
                                )
                            )
                            .padding(vertical = 12.dp, horizontal = 18.dp)
                    ) {
                        val date = event.startTime.toLocalDateTime()
                        Text(
                            buildAnnotatedString {
                                append("${date.toMonthAbbreviation()}\n")
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (role == "ORGANIZER" || role == "STAFF" || isETicket) Color(
                                            0xFF232222
                                        ) else Color(
                                            0xFFF36F44
                                        )
                                    )
                                ) {
                                    append(date.toDayOfMonth())
                                }
                            },
                            style = TextStyle(
                                fontFamily = AppFonts.instrumentSans,
                                fontSize = 16.sp,
                                lineHeight = 26.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = if (isModerator) Color.White else Color(0xFF232222)
                            )
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    if (role == "ORGANIZER" && !isRejectedOrRemoved && event.computedStatus == EventComputedStatus.UPCOMING)
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    onUpdateClicked()
                                }
                                .padding(10.dp))
                        {
                            Icon(
                                painter = painterResource(R.drawable.update_icon),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = Color.Black
                            )
                            Icon(
                                painterResource(R.drawable.update_icon),
                                contentDescription = "Update event",
                                modifier = Modifier
                                    .size(33.dp),
                                tint = Color.White
                            )
                        }
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .border(
                            width = 1.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(
                            if (isModerator) Color(0xFF312245).copy(alpha = 0.9f)
                            else Color.White.copy(alpha = 0.9f)
                        )
                        .padding(start = 5.dp, end = 5.dp)
                        .padding(vertical = 12.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            Modifier.weight(8f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = event.title,
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = if (isModerator) Color.White else Color(0xFF232222)
                                ),
                                modifier = Modifier.padding(horizontal = 2.dp),
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isModerator) Color.White else Color(
                                                0xFF676767
                                            )
                                        )
                                    ) {
                                        append("${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()} | ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF558042)
                                        )
                                    ) {
                                        append("${event.remainingSlots.toAbbreviatedString()} slots")
                                    }
                                },
                                maxLines = 1,
                            )
                        }
                        if (!isRejectedOrRemoved && event.status != EventApprovalStatus.PENDING &&
                            ((event.computedStatus == EventComputedStatus.ONGOING && isETicket || role == "ORGANIZER" || role == "STAFF") || (event.computedStatus != EventComputedStatus.ENDED && role == "ATTENDEE"))
                        ) {
                            Spacer(Modifier.width(5.dp))
                            VerticalDivider(
                                color = Color.Black,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 4.dp)
                            )
                            TextButton(
                                modifier = Modifier
                                    .weight(3f),
                                contentPadding = PaddingValues(
                                    vertical = 5.dp
                                ),
                                onClick = onTextButtonClick
                            ) {
                                if (isETicket)
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    fontFamily = AppFonts.instrumentSans,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFF36F44)
                                                )
                                            ) {
                                                append("Show\n")
                                            }
                                            append("QR Code")
                                        },
                                        style = TextStyle(
                                            fontFamily = AppFonts.instrumentSans,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 12.sp,
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFFF36F44)
                                        ),
                                    )
                                else if (role == "ORGANIZER" || role == "STAFF")
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    fontFamily = AppFonts.instrumentSans,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFF36F44)
                                                )
                                            ) {
                                                append("Scan\n")
                                            }
                                            append("QR Code")
                                        },
                                        style = TextStyle(
                                            fontFamily = AppFonts.instrumentSans,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 12.sp,
                                            textAlign = TextAlign.Center,
                                            color = Color(0xFFF36F44)
                                        ),
                                    )
                                else
                                    Text(
                                        buildAnnotatedString {
                                            withStyle(
                                                style = SpanStyle(
                                                    fontFamily = AppFonts.instrumentSans,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isModerator) Color.White else Color(
                                                        0xFF2A2A2A
                                                    )
                                                )
                                            ) {
                                                append("${event.capacity.toAbbreviatedString()}\n")
                                            }
                                            append("Capacity")
                                        },
                                        style = TextStyle(
                                            fontFamily = AppFonts.instrumentSans,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 12.sp,
                                            textAlign = TextAlign.Center,
                                            color = if (isModerator) Color.White else Color(
                                                0xFF2A2A2A
                                            )
                                        ),
                                    )
                            }
                        }
                    }
                }
            }
        }
    }
}