package com.example.gathr.presentation.main

import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.User

data class UserState(
    val currentUser: User? = null,
    val createEvent: CreateEvent = CreateEvent(),
    val actionError: String = "",
    val isUpdateEvent: Boolean = true,
    val isLoading: Boolean = false,
    val createEventValidationState: CreateEventValidationState = CreateEventValidationState()
)

data class CreateEventValidationState(
    val titleError: String = "",
    val descriptionError: String = "",
    val backgroundImageError: String = "",
    val capacityError: String = "",
    val locationError: String = "",
    val startDateAndTimeError: String = "",
    val endDateAndTimeError: String = "",
    val hasErrors: Boolean = false
)