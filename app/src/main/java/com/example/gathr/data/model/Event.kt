package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import java.time.Instant
import java.util.UUID


@Serializable
data class Event(
    val id: Long,

    @SerialName("parent_event_id")
    val parentEventId: Long? = null,

    val title: String,
    val description: String,
    val roles: List<String>? = null,

    @SerialName("allowed_departments")
    val allowedDepartments: List<DepartmentType>? = null,

    @SerialName("allow_non_umak")
    val allowNonUmak: Boolean,

    @SerialName("allow_alumni")
    val allowAlumni: Boolean,

    @SerialName("background_image")
    val backgroundImage: String,

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

    @SerialName("evaluation_form")
    val evaluationForm: String? = null,

    @SerialName("created_by")
    @Serializable(with = UuidSerializer::class)
    val createdBy: UUID,

    @SerialName("created_by_name")
    val createdByName: String,

    val status: EventApprovalStatus,

    @SerialName("computed_status")
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
    UPCOMING, ONGOING, COMPLETED
}