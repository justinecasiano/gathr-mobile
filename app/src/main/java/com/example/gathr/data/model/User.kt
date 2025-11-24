package com.example.gathr.data.model

import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.UuidSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UuidSerializer::class)
    val id: UUID,

    @SerialName("display_name")
    val displayName: String? = null,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val role: String,

    val department: String? = null,
    val school: String,

    @SerialName("is_umak")
    val isUmak: Boolean,

    @SerialName("is_alumni")
    val isAlumni: Boolean,

    @SerialName("fcm_token")
    val fcmToken: String? = null,

    @SerialName("created_at")
    @Serializable(with = JavaInstantSerializer::class)
    val createdAt: Instant
)

enum class UserRole {
    MODERATOR,
    PARTICIPANT,
}

enum class DepartmentType {
    ALL,
    CCIS,
    CCAPS,
    CAL,
    CBFS,
    IOA,
    CCSE,
    CHK,
    CGPP,
    ION,
    IIHS,
    CITE,
    COS,
    CTHM,
    IDEM,
    ISW,
    CET,
    SOL,
    HSU
}
