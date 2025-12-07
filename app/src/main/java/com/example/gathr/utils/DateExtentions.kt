package com.example.gathr.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDateTime
import java.util.Locale

fun Instant.toPrettyString(pattern: String = "MMMM d, yyyy | h:mm a"): String {
    val javaInstant = Instant.parse(this.toString())
    val formatter = DateTimeFormatter
        .ofPattern(pattern)
        .withZone(ZoneId.of("Asia/Manila"))
    return formatter.format(javaInstant)
}

fun Instant.toLocalDateTime(): LocalDateTime {
    val javaInstant = Instant.parse(this.toString())
    return javaInstant.atZone(ZoneId.of("Asia/Manila")).toLocalDateTime()
}

fun Instant.toSimpleTime(): String {
    val dateTime = this.toLocalDateTime()
    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
    return dateTime.format(formatter)
}

fun LocalDateTime.toDayOfMonth(): String {
    return this.dayOfMonth.toString()
}

fun LocalDateTime.toMonthAbbreviation(): String {
    val formatter = DateTimeFormatter.ofPattern("MMM", Locale.US)
    return this.format(formatter)
}
