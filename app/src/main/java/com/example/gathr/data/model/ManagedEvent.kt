package com.example.gathr.data.model

import com.example.gathr.utils.ParticipantTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ManagedEvent(
    val event: Event,

    @SerialName("user_participant_type")
    @Serializable(with = ParticipantTypeSerializer::class)
    val userParticipantType: ParticipantType,

    @SerialName("participants")
    val participantList: List<Participant>,

    @SerialName("staff")
    val staffList: List<Participant>,
)
