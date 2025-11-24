package com.example.gathr.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.core.ui.ElevatedButton
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.Event
import com.example.gathr.ui.theme.AppFonts
import com.example.gathr.utils.toPrettyString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewEventScreen(
    event: CreateEvent? = null,
    isOrganizer: Boolean = false,
    buttonText: String = "REGISTER",
    onButtonClick: () -> Unit = {}
) {
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp),
            title = {},
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            ), navigationIcon = {
                Surface(
                    onClick = {},
                    shape = CircleShape,
                    color = Color(0xFFD9D9D9).copy(alpha = 0.76f),
                    modifier = Modifier.size(35.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.back_outline),
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            },

            actions = {
                ThreeDotMenu()
            })
    }, bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .border(2.dp, color = Color(0xFFD7D7D7))
                .padding(top = 20.dp, start = 25.dp, end = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(6.5f)) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black.copy(alpha = 0.8f)
                            )
                        ) {
//                        append(event.startTime.toPrettyString(pattern = "MMM'.' d, yyyy").uppercase())
                            append("OCT.5, 2025\n")
                        }
//                                        append("${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}")
                        append("9:00 AM to 5:00 PM")
                    }, style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
            ElevatedButton(
                text = buttonText,
                onClick = onButtonClick,
                isEnabled = true,
                shouldFill = false,
                buttonColor = Color(0xFF7B55A3),
                outlineColor = Color(0xFF4C2576),
                textStyle = TextStyle(
                    fontFamily = AppFonts.instrumentSans,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                ),
//                    height = TODO(),
//                    modifier = TODO(),
                isContrast = false,
                buttonShape = RoundedCornerShape(15.dp),
                shouldAddShadow = false,
            )
        }
    }) { outerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxHeight(0.6f),
                painter = painterResource(R.drawable.infotech_placeholder_landscape),
                contentScale = ContentScale.Crop,
                contentDescription = "Event Background Image"
            )
            BottomScreenSheet(Modifier.padding(outerPadding))
        }
    }
}

@Composable
fun ThreeDotMenu() {
    var expanded by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier.wrapContentSize(Alignment.TopStart)
    ) {
        Column(verticalArrangement = Arrangement.Top) {
            Surface(
                onClick = {},
                shape = CircleShape,
                color = Color(0xFFD9D9D9).copy(alpha = 0.76f),
                modifier = Modifier
                    .size(35.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.option_outline),
                        contentDescription = "Option menu",
                        tint = Color.Black
                    )
                }
            }
            DropdownMenu(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF7B55A3), Color(0xFF583181)),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    ),
                expanded = true,
                onDismissRequest = {}) {
                DropdownMenuItem(
                    onClick = {},
                    text = {
                        Row {
                            Icon(
                                painter = painterResource(R.drawable.attendees),
                                contentDescription = "Attendees",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "See who registered",
                                style = TextStyle(
                                    fontFamily = AppFonts.rethinkSans,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                            )
                        }
                    })
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    color = Color.Black,
                    thickness = 0.8.dp
                )
                DropdownMenuItem(onClick = {}, text = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = "Delete Event",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Delete Event",
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        )
                    }
                })
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    color = Color.Black,
                    thickness = 0.8.dp
                )
                DropdownMenuItem(onClick = {}, text = {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = "Edit Event",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Edit Event",
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        )
                    }
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomScreenSheet(modifier: Modifier = Modifier, event: Event? = null) {
    val density = LocalDensity.current

    val halfScreenHeightInPixels = LocalWindowInfo.current.containerSize.height / 2

    val peekHeight: Dp = with(density) {
        halfScreenHeightInPixels.toDp()
    }

    val sheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.PartiallyExpanded,
        skipHiddenState = true
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )
    BottomSheetScaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        sheetShape = RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp),
        scaffoldState = scaffoldState,
        sheetPeekHeight = peekHeight,
        sheetContent = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = peekHeight)
                    .background(Color.White)
                    .padding(vertical = 8.dp, horizontal = 36.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(
                    Modifier
                        .width(80.dp)
                        .clip(RoundedCornerShape(7.5.dp))
                        .align(Alignment.CenterHorizontally),
                    thickness = 5.dp,
                    color = Color.Black,
                )
                Text(
//                    event.title.uppercase(),
                    "UNIVERSITY OF MAKATI'S INFOTECHNOLYMPICS", style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF6835E)
                            )
                        ) {
//                        append("${event.computedStatus.uppercase()}\n")
                            append("UPCOMING\n")
                        }
//                        append("${event.startTime.toPrettyString("MMMM d, yyyy")} | ${event.startTime.toSimpleTime()} to ${event.endTime.toSimpleTime()}")
//                        append(event.location)
                        append("UMak Auditorium, University of Makati, Makati City\n")
                        append("October 5, 2025 | 9:00 AM to 5:00 PM")
                    },
                    style = TextStyle(
                        fontFamily = AppFonts.rethinkSans,
                        fontSize = 12.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                    ),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            ) {
//                        append("${event.capacity}\n")
                                append("1200\n")
                            }
                            append("Capacity")
                        }, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        ), modifier = Modifier.weight(2.8f)
                    )
                    VerticalDivider(Modifier.height(40.dp), color = Color(0xFFDEDEDE))
                    Box(
                        modifier = Modifier.weight(4.4f), contentAlignment = Alignment.Center
                    ) {
                        Image(
                            modifier = Modifier.height(35.dp),
                            painter = painterResource(R.drawable.highlight_event),
                            contentScale = ContentScale.FillHeight,
                            contentDescription = "Event Background Image"
                        )
                    }
                    VerticalDivider(Modifier.height(40.dp), color = Color(0xFFDEDEDE))
                    Text(
                        buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = AppFonts.instrumentSans,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF820006)
                                )
                            ) {
//                        append("${event.remainingSlots}\n")
                                append("5\n")
                            }
                            append("Slots Left")
                        }, style = TextStyle(
                            fontFamily = AppFonts.rethinkSans,
                            fontSize = 12.sp,
                            lineHeight = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        ), modifier = Modifier.weight(2.8f)
                    )
                }
                Column(
                    Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start
                ) {
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 0.81.dp)
                    Spacer(Modifier.height(15.dp))
                    Row {
                        Spacer(Modifier.width(2.dp))
                        Icon(
                            modifier = Modifier.size(28.dp),
                            painter = painterResource(R.drawable.profile_icon),
                            tint = Color.Black,
                            contentDescription = "Profile Icon"
                        )
                        Spacer(Modifier.width(20.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                ) {
                                    append("Organizer\n")
                                }
//                        append("${event.createdBy}")
                                append("Prof. Era Gannaban")
                            },
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                    Spacer(Modifier.height(15.dp))
                    Row {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(R.drawable.description),
                            tint = Color.Black,
                            contentDescription = "Description Icon"
                        )
                        Spacer(Modifier.width(20.dp))
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = AppFonts.instrumentSans,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                ) {
                                    append("Description\n")
                                }
//                        append("${event.createdBy}")
                                append(
                                    """The UMak Infotechnolympics is an annual event organized by the University of Makati that highlights the talents of students in the IT field. It serves as a platform for showcasing innovation, creativity, and technical expertise."
                                        
Through various competitions, exhibits, and workshops, the event encourages collaboration and healthy competition among participants. It aims to inspire future IT professionals to apply their knowledge, explore new technologies, and contribute to the advancement of the digital world."""
                                )
                            },
                            style = TextStyle(
                                fontFamily = AppFonts.rethinkSans,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                textAlign = TextAlign.Start,
                                color = Color.Black,
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    }
                }
            }
        },
        sheetDragHandle = {

        },
    ) { innerPadding ->
    }
}

@Preview
@Composable
private fun ViewEventScreenPreview() {
    ViewEventScreen()
}