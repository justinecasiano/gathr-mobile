package com.example.gathr.utils

import java.util.Locale
import kotlin.math.abs

fun String.toTitleCase(
    exclusions: List<String> = listOf(
        "of",
        "the",
        "and",
        "in",
        "on",
        "at"
    )
): String {
    return this.lowercase()
        .split(" ")
        .joinToString(" ") { word ->
            if (word in exclusions) {
                word
            } else {
                word.replaceFirstChar { it.uppercase() }
            }
        }
}

fun Int.toAbbreviatedString(): String {
    val number = this.toLong()
    val absNumber = abs(number)

    return when {
        absNumber < 1_000 -> number.toString()
        absNumber < 1_000_000 -> {
            if (absNumber % 1_000 < 100) {
                "${number / 1_000}k"
            } else {
                String.format(Locale.US, "%.1fk", number / 1000.0)
            }
        }

        absNumber < 1_000_000_000 -> {
            if (absNumber % 1_000_000 < 100_000) {
                "${number / 1_000_000}M"
            } else {
                String.format(Locale.US, "%.1fM", number / 1000000.0)
            }
        }

        else -> String.format(Locale.US, "%.1fB", number / 1000000000.0)
    }
}