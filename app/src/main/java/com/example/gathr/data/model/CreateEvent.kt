package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import kotlinx.serialization.Serializable
import java.io.File
import java.time.Instant

@Serializable
data class CreateEvent(
    val title: String = "",
    val description: String = "",
    val backgroundImage: String? = null,
    val backgroundImageSizeBytes: Long = 0,
    val capacity: Int? = null,
    val location: String = "",
    @Serializable(with = JavaInstantSerializer::class)
    val startDateAndTime: Instant = Instant.now(),
    @Serializable(with = JavaInstantSerializer::class)
    val endDateAndTime: Instant = Instant.now(),
)
