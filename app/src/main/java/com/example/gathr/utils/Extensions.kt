package com.example.gathr.utils

import java.text.DecimalFormat
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
        .split(Regex("[\\s_]+"))
        .filter { it.isNotBlank() }
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

    val df = DecimalFormat("#.#")

    return when {
        absNumber < 1_000 -> number.toString()
        absNumber < 1_000_000 ->
            df.format(number / 1000.0) + "k"
        absNumber < 1_000_000_000 ->
            df.format(number / 1_000_000.0) + "M"
        else ->
            df.format(number / 1_000_000_000.0) + "B"
    }
}
