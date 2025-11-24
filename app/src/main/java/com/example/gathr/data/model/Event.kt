package com.example.gathr.data.model

import kotlin.time.ExperimentalTime
import java.time.Instant
import java.util.UUID


data class Event (
    val id: Long,
    val parentEventId: Long,
    val title: String,
    val description: String,
    val allowedDepartments: List<DepartmentType>,
    val allowNonUmak: Boolean,
    val allowAlumni: Boolean,
    val backgroundImage: String,
    val location: String,
    val startTime: Instant,
    val endTime: Instant,
    val capacity: Int,
    val remainingSlots: Int,
//    val evaluationForm: EvaluationForm
    val createdBy: UUID,
    val status: EventApprovalStatus,
    val computedStatus: EventComputedStatus,
    val submittedAt: Instant,
    val updatedAt: Instant,
    val approvedBy: UUID,
    val comment: String,
    val approvedAt: Instant,
    val isArchive: Boolean,
)

enum class EventApprovalStatus {
    PENDING, REJECTED, APPROVED
}

enum class EventComputedStatus {
    UPCOMING, ONGOING, COMPLETED
}