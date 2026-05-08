package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class CreateStaff(
    @SerialName("event_id")
    val eventId: Int? = null,

    @SerialName("user_id")
    @Serializable(with = UuidSerializer::class)
    val userId: UUID? = null,

    val email: String = "",

    val displayName: String = "",

    val avatarUrl: String = "",

    val firstName: String = "",

    val lastName: String = "",

    @SerialName("participant_type")
    val participantType: ParticipantType = ParticipantType.STAFF,
)
