package com.example.gathr.presentation.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.data.model.Event
import com.example.gathr.ui.theme.AppFonts

@Composable
fun LargeEventCard(
    event: Event? = null,
    onCardClick: () -> Unit,
    onTextButtonClick: () -> Unit,
    image: (@Composable () -> Unit)? = null,
) {
    Card(
        modifier = Modifier
            .dropShadow(
                shape = RoundedCornerShape(20.dp),
                shadow = Shadow(
                    radius = 20.dp,
                    spread = 0.dp,
                    color = Color(0xFF000000).copy(alpha = 0.5f),
                    offset = DpOffset(x = 0.dp, (10).dp)
                )
            )
            .fillMaxWidth()
            .height(250.dp),
        shape = RoundedCornerShape(20.dp),
        onClick = onCardClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (image != null) image()
            else
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(R.drawable.infotech_placeholder_landscape),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "Event Background Image"
                )
//            else
//                AsyncImage(
//                    model = ImageRequest.Builder(LocalContext.current).data(event.backgroundImage)
//                        .crossfade(true)
//                        .build(),
//                    contentDescription = "Event Background Image",
//                    placeholder = painterResource(id = R.drawable.placeholder_landscape),
//                    contentScale = ContentScale.Crop
//                )
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp, bottom = 18.dp, start = 27.dp, end = 27.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(vertical = 12.dp, horizontal = 15.dp)
                        .align(Alignment.TopStart)
                ) {
//            val date = event.startTime.toLocalDateTime()
                    Text(
                        buildAnnotatedString {
//                    append(date.toMonthAbbreviation())
                            append("Oct\n")
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
//                        append(date.toDayOfMonth())
                                append("22")
                            }
                        },
                        style = TextStyle(
                            fontFamily = AppFonts.instrumentSans,
                            fontSize = 16.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(vertical = 12.dp, horizontal = 15.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(8f)) {
                            Text(
//                                text="${event.title}\n",
                                text = "5th International Hackathon",
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF232222)
                                ), maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF676767)
                                        )
                                    ) {
//                                        append("${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()} | ")
                                        append("9:00 AM to 5:00 PM | ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = AppFonts.rethinkSans,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF558042)
                                        )
                                    ) {
//                                        append("${event.remainingSlots}")
                                        append("256 slots left")
                                    }
                                },
                                maxLines = 1,
                            )
                        }
                        Spacer(Modifier.width(2.dp))
                        VerticalDivider(color = Color.Black)
                        TextButton(
                            modifier = Modifier
                                .weight(3f)
                                .defaultMinSize(minHeight = 1.dp),
                            contentPadding = PaddingValues(
                                vertical = 0.dp
                            ),
                            onClick = onTextButtonClick
                        ) {
                            Text(
                                buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = AppFonts.rethinkSans,
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
                                    lineHeight = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFF36F44)
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LargeEventCardPreview() {
    Scaffold() { paddingValues ->
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(10.dp))
            LargeEventCard(onCardClick = {}, onTextButtonClick = {})
        }
    }
}