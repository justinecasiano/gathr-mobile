package com.example.gathr.presentation.moderator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import com.example.gathr.R
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity


@Composable
fun DialogBox(
    show: Boolean,
    title: String,
    description: String,
    confirmColor: Color,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    if (!show) return

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF6A3BA8),
                            Color(0xFF3A245F)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // CONTENT WITH PADDING
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = description,
                        fontSize = 15.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                // FULL-WIDTH HORIZONTAL DIVIDER
                Divider(
                    color = Color.Black.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                // FOOTER BUTTONS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Cancel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Cancel",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    // FULL-HEIGHT VERTICAL DIVIDER
                    Divider(
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    )

                    // Confirm
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onConfirm() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Confirm",
                            color = confirmColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReasonDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!show) return
    val leftIndent = 34.dp
    var selectedReason by remember { mutableStateOf("") }
    var otherReason by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFF6A3BA8),
                            Color(0xFF3A245F)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .padding(start = 20.dp, end = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Reason",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ReasonRadio(
                        text = "Inappropriate content",
                        selected = selectedReason == "Inappropriate content",
                        onClick = { selectedReason = "Inappropriate content" }
                    )

                    ReasonRadio(
                        text = "Policy violation",
                        selected = selectedReason == "Policy violation",
                        onClick = { selectedReason = "Policy violation" }
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = "Others" }
                    ) {

                        ReasonRadio(
                            text = "Others (specify)",
                            selected = selectedReason == "Others",
                            onClick = { selectedReason = "Others" }
                        )

                        BasicTextField(
                            value = otherReason,
                            onValueChange = { otherReason = it },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.White,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier
                                .padding(start = leftIndent - 7.dp)
                                .fillMaxWidth(0.7f)
                        )

                        Box(
                            modifier = Modifier
                                .padding(start = leftIndent - 7.dp)
                                .fillMaxWidth(0.7f)
                                .height(1.dp)
                                .background(Color.White)
                        )
                    }
                }

                // FULL-WIDTH HORIZONTAL DIVIDER (NO PADDING)
                Divider(
                    color = Color.Black.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                // FOOTER BUTTONS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Cancel", color = Color.White, fontSize = 16.sp)
                    }

                    // FULL HEIGHT VERTICAL DIVIDER
                    Divider(
                        color = Color.Black.copy(alpha = 0.5f),
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                val finalReason =
                                    if (selectedReason == "Others") otherReason else selectedReason

                                if (finalReason.isNotBlank()) onConfirm(finalReason)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Confirm",
                            color = Color(0xFFFC3436),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReasonRadio(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Color.White,
                unselectedColor = Color.White,
            ),
            modifier = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp
        )
    }
}

@Composable
fun EventOptionsPopup(
    anchor: Offset,
    onSeeRegistered: () -> Unit,
    onRemove: () -> Unit,
    onPin: () -> Unit,
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current

    val screenWidth = config.screenWidthDp.dp
    val popupWidth = 200.dp
    val triangleSize = 22.dp
    val verticalOffset = 30.dp // distance below button

    // Convert px -> dp
    val anchorX = with(density) { anchor.x.toDp() }
    val anchorY = with(density) { anchor.y.toDp() }

    // Base popup X (center under button)
    var popupX = anchorX - (popupWidth / 2)

    // Keep popup inside screen edges
    if (popupX < 8.dp) popupX = 8.dp
    if (popupX + popupWidth > screenWidth - 8.dp) popupX = screenWidth - popupWidth - 8.dp


    val manualNudge = 18.dp
    val triangleX = (anchorX - popupX - (triangleSize / 2)) + manualNudge

    val popupY = anchorY + verticalOffset

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = onDismiss,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        // place popup at popupX / popupY
        Column(
            modifier = Modifier
                .absoluteOffset(x = popupX, y = popupY)
                .width(popupWidth)
        ) {
            // TRIANGLE: use offset so it moves inside popup bounds
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(
                    modifier = Modifier
                        .size(triangleSize)
                        .offset(x = triangleX) // <-- offset inside the popup
                ) {
                    val path = Path().apply {
                        moveTo(size.width / 2f, 0f)
                        lineTo(0f, size.height)
                        lineTo(size.width, size.height)
                        close()
                    }
                    drawPath(path, Color(0xFF6A3BA8))
                }
            }


            // Popup body
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFF6A3BA8), Color(0xFF46286B))
                        )
                    )
                    .width(popupWidth)
            ) {
                PopupItem(R.drawable.attendees, "See Participants", onSeeRegistered)
                Divider(color = Color.White.copy(alpha = 0.15f))
                PopupItem(R.drawable.delete, "Remove Event", onRemove)
            }
        }
    }
}


@Composable
fun PopupItem(iconRes: Int, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

private fun DrawScope.drawTriangle(color: Color) {
    val path = Path().apply {
        moveTo(size.width / 2f, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(path = path, color = color)
}