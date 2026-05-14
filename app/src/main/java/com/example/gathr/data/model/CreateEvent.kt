package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import kotlinx.serialization.Serializable
import java.io.File
import java.time.Instant


@Serializable
data class CreateEvent(
    val id: Long? = null,
    val staffs: List<CreateStaff> = emptyList(),
    val title: String = "",
    val description: String = "",
    val backgroundImage: String? = null,
    val oldBackgroundImageUrl: String? = null,
    val backgroundImageSizeBytes: Long = 0,
    val capacity: Int? = 10,
    val location: String = "",
    val allowedDepartments: List<DepartmentType> = listOf(DepartmentType.ALL),
    var allowAlumni: Boolean = false,
    @Serializable(with = JavaInstantSerializer::class)
    val startDateAndTime: Instant = Instant.now(),
    @Serializable(with = JavaInstantSerializer::class)
    val endDateAndTime: Instant = Instant.now(),
    val createEventValidationState: CreateEventValidationState = CreateEventValidationState(),
    val createEventStep: CreateEventStep = CreateEventStep.BASIC_INFO,
)


@Serializable
data class CreateEventValidationState(
    val titleError: String = "",
    val descriptionError: String = "",
    val backgroundImageError: String = "",
    val capacityError: String = "",
    val locationError: String = "",
    val startDateAndTimeError: String = "",
    val endDateAndTimeError: String = "",
    val staffError: String = "",
    val hasErrors: Boolean = false
)

enum class CreateEventStep { BASIC_INFO, ADD_STAFF }
