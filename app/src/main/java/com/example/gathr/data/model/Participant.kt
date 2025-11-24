package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class Participant(
    @SerialName("event_id")
    val eventId: Int,

    @SerialName("user_id")
    @Serializable(with = UuidSerializer::class)
    val userId: UUID,

    @SerialName("participant_type")
    val participantType: ParticipantType,

    @SerialName("participant_role")
    val participantRole: String? = null,

    @SerialName("status")
    val participantStatus: ParticipantStatus,

    @SerialName("check_in")
    @Serializable(with = JavaInstantSerializer::class)
    val checkIn: Instant? = null,

    val evaluation: String? = null,

    @SerialName("evaluation_submitted_at")
    @Serializable(with = JavaInstantSerializer::class)
    val evaluationSubmittedAt: Instant? = null,

    @SerialName("joined_at")
    @Serializable(with = JavaInstantSerializer::class)
    val joinedAt: Instant = Instant.now()
)

enum class ParticipantType {
    ATTENDEE, STAFF, ORGANIZER
}

enum class ParticipantStatus {
    REGISTERED, PRESENT, ABSENT, CANCELLED
}
