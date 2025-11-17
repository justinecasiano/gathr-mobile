package com.example.gathr.presentation.moderator

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class NotificationItem(
    val event: EventData,
    val message: String,
    val timestamp: String,
    val isUnread: Boolean = true,
    val category: String,       // "New" or "Today"
    val iconRes: Int            // R.drawable.whatever_icon
) : Parcelable
