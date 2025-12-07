package com.example.gathr.presentation.main

import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.User
import com.example.gathr.presentation.participant.ParticipantPayload
import java.io.File

data class UserState(
    val currentUser: User? = null,
    val currentEvent: Event? = null,
    val currentEvents: List<Event> = emptyList(),
    val currentParticipant: Participant? = null,
    val currentParticipants: List<Participant> = emptyList(),
    val scanParticipant: ParticipantPayload? = null,
    val createEvent: CreateEvent = CreateEvent(),
    val createEventImageFile: File? = null,
    val addStaffs: List<CreateStaff> = emptyList(),
    val searchStaff: String = "",
    val searchStaffError: String = "",
    val isUpdateEvent: Boolean = false,
    val actionTitle: String = "",
    val actionError: String = "",
    val actionOnConfirm: () -> Unit = {},
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