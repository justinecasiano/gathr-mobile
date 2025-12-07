package com.example.gathr.presentation.shared

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.fallback
import com.example.gathr.R
import com.example.gathr.data.model.Event
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toPrettyString
import com.example.gathr.utils.toSimpleTime

@Composable
fun SmallEventCard(
    event: Event,
    isDetailed: Boolean = false,
    cardWidth: Dp = 188.dp,
    onClick: () -> Unit
) {
    val width = if (isDetailed) cardWidth else 148.dp

    Column(
        Modifier
            .clickable { onClick() }
            .padding(5.dp)
            .width(width)
    )
    {
        AsyncImage(
            modifier = Modifier
                .dropShadow(
                    shape = RoundedCornerShape(20.dp),
                    shadow = Shadow(
                        radius = 10.dp,
                        spread = 0.dp,
                        color = Color(0xFF000000).copy(alpha = 0.25f),
                        offset = DpOffset(x = 0.dp, (6).dp)
                    )
                )
                .clip(RoundedCornerShape(20.dp))
                .height(width),
            model = ImageRequest.Builder(LocalContext.current)
                .fallback(R.drawable.placeholder_landscape)
                .data(event.backgroundImage)
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
        Spacer(Modifier.height(10.dp))
        if (!isDetailed) {
            Text(
                text = event.title,
                style = TextStyle(
                    fontFamily = AppFonts.rethinkSans,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF313131)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                        append("${event.startTime.toPrettyString("MMM. d, yyyy")} | ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (event.remainingSlots <= 20) Color(0xFF820006)
                            else Color(0xFF9FC090)
                        )
                    ) {
                        append("${event.remainingSlots} slots")
                    }
                },
                maxLines = 1,
            )
        } else {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF313131)
                        )
                    ) {
                        append("${event.title}\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF313131)
                        )
                    ) {
                        append("${event.location}\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF676767)
                        )
                    ) {
                        append(
                            "${
                                event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy")
                                    .uppercase()
                            } |\n${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}\n"
                        )
                        append("Organized by ${event.createdByName}\n")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (event.remainingSlots <= 30) Color(0xFF820006)
                            else Color(0xFF9FC090),
                        )
                    ) {
                        append("${event.remainingSlots} slots left")
                    }
                },
            )
        }
    }
}
