package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.ParticipantStatusSerializer
import com.example.gathr.utils.ParticipantTypeSerializer
import com.example.gathr.utils.ResponseStatusSerializer
import com.example.gathr.utils.UserRoleSerializer
import com.example.gathr.utils.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import java.time.Instant
import java.util.UUID

@Serializable
data class Participant(
    @SerialName("event_id")
    val eventId: Long,

    @SerialName("user_id")
    @Serializable(with = UuidSerializer::class)
    val userId: UUID,

    @SerialName("full_name")
    val fullName: String?,

    @SerialName("email")
    val email: String,

    @SerialName("display_name")
    val displayName: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @SerialName("participant_type")
    @Serializable(with = ParticipantTypeSerializer::class)
    val participantType: ParticipantType,

    @SerialName("participant_role")
    val participantRole: String? = null,

    @SerialName("status")
    @Serializable(with = ParticipantStatusSerializer::class)
    val participantStatus: ParticipantStatus,

    @SerialName("check_in")
    @Serializable(with = JavaInstantSerializer::class)
    val checkIn: Instant? = null,

    @SerialName("feedback_submission")
    val feedbackSubmission: JsonElement? = null,

    @SerialName("feedback_submitted_at")
    @Serializable(with = JavaInstantSerializer::class)
    val feedbackSubmittedAt: Instant? = null,

    @SerialName("response_status")
    @Serializable(with = ResponseStatusSerializer::class)
    val responseStatus: ResponseStatus,

    val rating: Int? = null,
    val comment: String? = null,

    @SerialName("joined_at")
    @Serializable(with = JavaInstantSerializer::class)
    val joinedAt: Instant = Instant.now()
)

enum class ResponseStatus {
    ANSWERED, NO_RESPONSE, ABSENT
}

enum class ParticipantType {
    ATTENDEE, STAFF, ORGANIZER
}

enum class ParticipantStatus {
    REGISTERED, PRESENT, ABSENT, CANCELLED, CHECKED_IN
}
