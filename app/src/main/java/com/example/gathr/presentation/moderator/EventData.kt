package com.example.gathr.presentation.moderator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class EventData(
    val id : Int,
    val title: String,
    val location: String,
    val dateTime: String,
    val shortDate: String,
    val timeRange: String,
    val capacity: Int,
    val slots: Int,
    val countRegistered: Int,
    val countCancelled: Int,
    val type: String,
    val host: String,
    val organizer: String,
    val attachmentName: String,
    val description: String,
    val imageRes: Int,
    val status: String
) : Parcelable
