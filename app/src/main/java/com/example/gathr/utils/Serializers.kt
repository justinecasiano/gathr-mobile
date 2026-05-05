package com.example.gathr.utils

import com.example.gathr.data.model.DepartmentType
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.EventComputedStatus
import com.example.gathr.data.model.ParticipantStatus
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.ResponseStatus
import com.example.gathr.data.model.UserRole
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID
import java.time.Instant

object JavaInstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("java.time.Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

object UuidSerializer : KSerializer<UUID> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }
}

class SafeEnumSerializer<T : Enum<T>>(
    private val enumValues: Array<T>,
    private val defaultValue: T
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("SafeEnum", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T {
        val raw = decoder.decodeString()
            .replace("'", "")
            .split("::")[0]
            .uppercase()

        return enumValues.find { it.name == raw } ?: defaultValue
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.name)
    }
}

object UserRoleSerializer : KSerializer<UserRole> by SafeEnumSerializer(
    UserRole.entries.toTypedArray(),
    UserRole.PARTICIPANT
)

object DepartmentSerializer : KSerializer<DepartmentType> by SafeEnumSerializer(
    DepartmentType.entries.toTypedArray(),
    DepartmentType.ALL
)

object ResponseStatusSerializer : KSerializer<ResponseStatus> by SafeEnumSerializer(
    ResponseStatus.entries.toTypedArray(),
    ResponseStatus.NO_RESPONSE
)

object ParticipantTypeSerializer : KSerializer<ParticipantType> by SafeEnumSerializer(
    ParticipantType.entries.toTypedArray(),
    ParticipantType.ATTENDEE
)

object ParticipantStatusSerializer : KSerializer<ParticipantStatus> by SafeEnumSerializer(
    ParticipantStatus.entries.toTypedArray(),
    ParticipantStatus.REGISTERED
)

object EventApprovalStatusSerializer: KSerializer<EventApprovalStatus> by SafeEnumSerializer(
    EventApprovalStatus.entries.toTypedArray(),
    EventApprovalStatus.PENDING
)

object EventComputedStatusSerializer: KSerializer<EventComputedStatus> by SafeEnumSerializer(
    EventComputedStatus.entries.toTypedArray(),
    EventComputedStatus.UPCOMING
)
