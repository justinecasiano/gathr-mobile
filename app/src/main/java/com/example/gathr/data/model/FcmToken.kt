package com.example.gathr.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FcmToken(
    val id: String,
    @SerialName("fcm_token")
    val fcmToken: String
)
