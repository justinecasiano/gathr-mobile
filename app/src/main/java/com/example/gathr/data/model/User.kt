package com.example.gathr.data.model

import com.example.gathr.utils.DepartmentSerializer
import com.example.gathr.utils.JavaInstantSerializer
import com.example.gathr.utils.UserRoleSerializer
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
    val displayName: String?,

    @SerialName("first_name")
    val firstName: String,

    val email: String,

    @SerialName("last_name")
    val lastName: String,

    @SerialName("avatar_url")
    val avatarUrl: String? = null,

    @Serializable(with = UserRoleSerializer::class)
    val role: UserRole = UserRole.PARTICIPANT,

    @Serializable(with = DepartmentSerializer::class)
    val department: DepartmentType? = null,

    val school: String,

    @SerialName("is_umak")
    val isUmak: Boolean,

    @SerialName("is_alumni")
    val isAlumni: Boolean,

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
