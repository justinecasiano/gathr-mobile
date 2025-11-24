package com.example.gathr.data.model

import java.time.Instant
import java.util.UUID

data class Participant(
    val eventId: Int,
    val userId: UUID,
    val participantType: ParticipantType,
    val participantRole: String,
    val participantStatus: ParticipantStatus,
    val checkIn: Instant,
    val evaluation: String,
    val evaluationSubmittedAt: Instant,
    val joinedAt: Instant
)

enum class ParticipantType {
    ATTENDEE, STAFF, ORGANIZER
}

enum class ParticipantStatus {
    REGISTERED, PRESENT, ABSENT, CANCELLED
}
