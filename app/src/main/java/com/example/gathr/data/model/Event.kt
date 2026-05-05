package com.example.gathr.data.model

import com.example.gathr.utils.EventApprovalStatusSerializer
import com.example.gathr.utils.EventComputedStatusSerializer
import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.ParticipantTypeSerializer
import com.example.gathr.utils.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.util.UUID


@Serializable
data class Event(
    val id: Long,

    val title: String,
    val description: String,

    @SerialName("allowed_departments")
    val allowedDepartments: List<DepartmentType>? = null,

    @SerialName("allow_non_umak")
    val allowNonUmak: Boolean,

    @SerialName("allow_alumni")
    val allowAlumni: Boolean,

    @SerialName("background_image")
    val backgroundImage: String?,

    val location: String,

    @SerialName("start_time")
    @Serializable(with = JavaInstantSerializer::class)
    val startTime: Instant,

    @SerialName("end_time")
    @Serializable(with = JavaInstantSerializer::class)
    val endTime: Instant,

    val capacity: Int,

    @SerialName("remaining_slots")
    val remainingSlots: Int,

    @SerialName("feedback_form")
    val feedbackForm: JsonElement? = null,

    @SerialName("is_form_active")
    val isFormActive: Boolean = false,

    @SerialName("form_title")
    val formTitle: String? = null,

    @SerialName("created_by")
    @Serializable(with = UuidSerializer::class)
    val createdBy: UUID,

    @Serializable(with = EventApprovalStatusSerializer::class)
    val status: EventApprovalStatus,

    @SerialName("organizerName")
    val organizerName: String,

    @SerialName("user_role")
    @Serializable(with = ParticipantTypeSerializer::class)
    val userRole: ParticipantType,

    @SerialName("is_registered")
    val isRegistered: Boolean = false,

    @SerialName("computed_status")
    @Serializable(with = EventComputedStatusSerializer::class)
    val computedStatus: EventComputedStatus,

    @SerialName("submitted_at")
    @Serializable(with = JavaInstantSerializer::class)
    val submittedAt: Instant,

    @SerialName("updated_at")
    @Serializable(with = JavaInstantSerializer::class)
    val updatedAt: Instant? = null,

    @SerialName("approved_by")
    @Serializable(with = UuidSerializer::class)
    val approvedBy: UUID? = null,

    val comment: String? = null,

    @SerialName("approved_at")
    @Serializable(with = JavaInstantSerializer::class)
    val approvedAt: Instant? = null,

    @SerialName("is_archive")
    val isArchive: Boolean,
)

enum class EventApprovalStatus {
    PENDING, REJECTED, APPROVED
}

enum class EventComputedStatus {
    UPCOMING, ONGOING, ENDED
}