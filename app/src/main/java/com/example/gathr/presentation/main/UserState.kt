package com.example.gathr.presentation.main

import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.ManagedEvent
import com.example.gathr.data.model.Notification
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.User
import com.example.gathr.presentation.participant.ParticipantPayload
import kotlinx.serialization.Serializable
import java.io.File

data class UserState(
    val managedEvents: List<ManagedEvent> = emptyList(),
    val joinableEvents: List<Event> = emptyList(),
    val joinedEvents: List<Event> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val dataFetchStatus: StateFetchStatus = StateFetchStatus(),

    val currentUser: User? = null,
    val currentEvent: Event? = null,
    val currentAttendees: List<Participant> = emptyList(),
    val availableStaff: List<User> = emptyList(),

    val eventsSearchText: String = "",
    val myEventsSelectedTabIndex: Int = 0,
    val myEventsSearchText: String = "",
    val eTicketsSelectedTabIndex: Int = 0,
    val eTicketsSearchText: String = "",

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

@Serializable
data class StateFetchStatus(
    val currentUser: FetchStatus = FetchStatus.LOADING,
    val managedEvents: FetchStatus = FetchStatus.LOADING,
    val joinableEvents: FetchStatus = FetchStatus.LOADING,
    val joinedEvents: FetchStatus = FetchStatus.LOADING,
    val availableStaff: FetchStatus = FetchStatus.LOADING,
    val notifications: FetchStatus = FetchStatus.LOADING,
)

enum class FetchStatus {
    LOADING,
    DONE,
}

@Serializable
data class UserCache(
    val managedEvents: List<ManagedEvent>,
    val joinableEvents: List<Event>,
    val joinedEvents: List<Event>,
    val notifications: List<Notification>,
    val currentUser: User?,
    val dataFetchStatus: StateFetchStatus = StateFetchStatus(),
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

