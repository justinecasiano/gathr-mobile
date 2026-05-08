package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class Notification(
    val id: Long,
    val message: String,

    @SerialName("is_read")
    val isRead: Boolean,

    @SerialName("created_at")
    @Serializable(with = JavaInstantSerializer::class)
    val createdAt: Instant
)
