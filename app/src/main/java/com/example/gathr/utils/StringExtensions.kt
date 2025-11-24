package com.example.gathr.utils

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