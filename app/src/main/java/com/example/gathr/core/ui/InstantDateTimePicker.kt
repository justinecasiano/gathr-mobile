package com.example.gathr.core.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.gathr.R
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstantDateTimePicker(
    label: String,
    value: Instant?,
    isError: Boolean? = null,
    supportingText: String = "",
    onValueChange: (Instant) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    var tempDate by remember { mutableStateOf<LocalDate?>(null) }

    val formattedDateTime = remember(value) {
        value?.let {
            val localDateTime = LocalDateTime.ofInstant(it, ZoneId.of("Asia/Manila"))
            DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a").format(localDateTime)
        } ?: ""
    }

    Box {
        CustomTextField(
            text = formattedDateTime,
            onValueChange = {},
            labelText = label,
            isError = isError,
            supportingText = supportingText,
            contentColor = Color.Black,
            containerColor = Color.White,
            outlineColor = Color(0xFF777777),
            readOnly = true,
            modifier = Modifier.clickable { showDatePicker = true },
            iconButton = {
                Icon(painterResource(R.drawable.calendar_today_icon), contentDescription = null)
            }
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePicker = true }
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = value?.toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            tempDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("Asia/Manila"))
                                .toLocalDate()
                            showDatePicker = false
                            showTimePicker = true
                        }
                    }
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val initialTime =
            value?.atZone(ZoneId.of("Asia/Manila"))?.toLocalTime() ?: LocalTime.now()

        val timePickerState = rememberTimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedTime =
                            LocalTime.of(timePickerState.hour, timePickerState.minute)

                        if (tempDate != null) {
                            val localDateTime = LocalDateTime.of(tempDate, selectedTime)
                            val zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Manila"))
                            onValueChange(zonedDateTime.toInstant())
                        }
                        showTimePicker = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Select Time",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}
