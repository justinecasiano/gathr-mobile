package com.example.gathr.presentation.moderator
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gathr.R
import com.example.gathr.ui.theme.AppFonts.rethinkSans
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import com.example.gathr.presentation.moderator.components.DialogBox
import com.example.gathr.presentation.moderator.components.EventOptionsPopup
import com.example.gathr.presentation.moderator.components.ReasonDialog

@Composable
fun EventApprovalScreen(
    event: EventData,
    onRemove: () -> Unit,
    onReject: (String) -> Unit,
    onApprove: () -> Unit,
    onBack: () -> Unit,
    onSeeRegistered: (EventData) -> Unit
) {
    // 🔹 Dialog State
    var showReasonDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(Offset.Zero) }
    val topGradient = Brush.verticalGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF7954AB),
            0.43f to Color(0xFF312245)
        )
    )

    val bottomGradient = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF312245),
            0.60f to Color(0xFF7954AB)
        )
    )

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(topGradient)
    ) {
        /* TOP IMAGE + BACK BUTTON */
        Box {
            Image(
                painter = painterResource(id = event.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .padding(start = 16.dp, top = 35.dp)
                    .size(40.dp)
                    .clickable { onBack() }
            )


            Image(
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "More Options",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 35.dp, end = 16.dp)
                    .size(40.dp)
                    .onGloballyPositioned { layout ->
                        val pos = layout.localToRoot(Offset.Zero)
                        menuOffset = pos
                    }
                    .clickable { showOptions = true }
            )

        }
        if (showOptions) {
            EventOptionsPopup(
                anchor = menuOffset,     // ⬅ Best way to align triangle
                onSeeRegistered = {
                    showOptions = false
                    onSeeRegistered(event)
                },
                onRemove = {
                    showOptions = false
                    showRemoveDialog = true
                },
                onPin = {
                    showOptions = false
                },
                onDismiss = { showOptions = false }
            )
        }


        /* MAIN CONTENT */
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 280.dp)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(topGradient)
                .verticalScroll(scrollState)
                .padding(start = 20.dp, top = 20.dp, end = 20.dp)
        ) {

            // TITLE + LOCATION + DATETIME
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = event.title.uppercase(),
                    color = Color.White,
                    fontFamily = rethinkSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "FOR APPROVAL",
                    color = Color(0xFFFFC107),
                    fontFamily = rethinkSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = event.location,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = rethinkSans,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = event.dateTime,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = rethinkSans,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                    textAlign = TextAlign.Center
                )
            }

            /* STATS ROW */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${event.capacity}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("CAPACITY", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(Color.Black)
                )

                // TYPE WITH WINGS
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_wings),
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .graphicsLayer(
                                scaleX = 2.4f,
                                scaleY = 2.6f
                            )
                            .zIndex(0f),
                        contentScale = ContentScale.Fit,
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White),
                        alpha = 1f
                    )

                    Text(
                        event.type,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = rethinkSans,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.zIndex(1f)
                    )
                }

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(Color.Black)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${event.slots}", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Slots Left", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(1.dp)
                        .background(Color.Black)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            /* HOST */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_host),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(end = 10.dp)
                )
                Column {
                    Text(
                        "Hosted by ${event.host}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Organized by ${event.organizer}",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .height(1.dp)
                        .background(Color.Black)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* ATTACHMENTS */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_attachment),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(end = 10.dp)
                )
                Column {
                    Text("Attachments", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(event.attachmentName, color = Color(0xFF5CE65C), fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /* DESCRIPTION */
            Row(verticalAlignment = Alignment.Top) {
                Image(
                    painter = painterResource(id = R.drawable.ic_description),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp).padding(end = 10.dp)
                )
                Column {
                    Text("Description", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(event.description, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }

        /* BOTTOM BAR */
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
                    .align(Alignment.TopCenter)
                    .zIndex(5f)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bottomGradient)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .navigationBarsPadding()
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {
                        Text(event.shortDate, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(event.timeRange, color = Color.White, fontSize = 11.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                        // REJECT
                        Box {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .height(46.dp)
                                    .width(100.dp)
                                    .offset(y = 4.dp, x = 0.5.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF820006))
                            ) {}

                            Button(
                                onClick = { showRejectDialog = true },
                                modifier = Modifier
                                    .height(46.dp)
                                    .width(100.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC3436))
                            ) {
                                Text("REJECT", color = Color.White, fontSize = 13.sp)
                            }
                        }

                        // APPROVE
                        Box {
                            Button(
                                onClick = {},
                                modifier = Modifier
                                    .height(46.dp)
                                    .width(110.dp)
                                    .offset(y = 4.dp, x = 0.5.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF61924B))
                            ) {}

                            Button(
                                onClick = { showApproveDialog = true },
                                modifier = Modifier
                                    .height(46.dp)
                                    .width(110.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9FC090))
                            ) {
                                Text("APPROVE", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        /* 🔹 Dialog Calls */
        DialogBox(
            show = showRemoveDialog,
            title = "Remove Event",
            description = "Are you sure you want to remove this event?",
            confirmColor = Color(0xFFFC3436),
            onDismiss = { showRemoveDialog = false },
            onConfirm = {
                showRejectDialog = false
                onRemove()
            }
        )


        DialogBox(
            show = showRejectDialog,
            title = "Reject Event",
            description = "Are you sure you want to reject this event?",
            confirmColor = Color(0xFFFC3436),
            onDismiss = { showRejectDialog = false },
            onConfirm = {
                showRejectDialog = false
                showReasonDialog = true   // ← show second dialog here
            }
        )

        ReasonDialog(
            show = showReasonDialog,
            onDismiss = { showReasonDialog = false },
            onConfirm = { reason ->
                showReasonDialog = false
                onReject(reason)
            }
        )


        DialogBox(
            show = showApproveDialog,
            title = "Approve Event",
            description = "Are you sure you want to approve this event?",
            confirmColor = Color(0xFF9FC090),
            onDismiss = { showApproveDialog = false },
            onConfirm = {
                showApproveDialog = false
                onApprove()
            }
        )

    }
}
