package com.example.gathr.presentation.main

import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateEventValidationState
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.ManagedEvent
import com.example.gathr.data.model.Notification
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.User
import com.example.gathr.presentation.participant.ParticipantPayload
import kotlinx.serialization.Serializable

data class UserState(
    val moderatorEvents: List<Event> = emptyList(),
    val managedEvents: List<ManagedEvent> = emptyList(),
    val joinableEvents: List<Event> = emptyList(),
    val joinedEvents: List<Event> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val dataFetchStatus: StateFetchStatus = StateFetchStatus(),

    val currentUser: User? = null,
    val currentEvent: Event? = null,
    val currentCreateEvent: CreateEvent = CreateEvent(),
    val currentAttendees: List<Participant> = emptyList(),
    val availableStaff: List<User> = emptyList(),

    val activeEventsFilter: String? = null,
    val eventsSearchText: String = "",
    val myEventsSelectedTabIndex: Int = 0,
    val myEventsSearchText: String = "",
    val eTicketsSelectedTabIndex: Int = 0,
    val eTicketsSearchText: String = "",
    val staffSelectedTabIndex: Int = 0,
    val staffSearchText: String = "",

    val currentParticipants: List<Participant> = emptyList(),
    val scanParticipant: ParticipantPayload? = null,
    val searchStaff: String = "",

    val actionTitle: String = "",
    val actionError: String = "",
    val actionOnConfirm: () -> Unit = {},
    val isLoading: Boolean = false,
)

@Serializable
data class StateFetchStatus(
    val currentUser: FetchStatus = FetchStatus.LOADING,
    val moderatorEvents: FetchStatus = FetchStatus.LOADING,
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
    val moderatorEvents: List<Event>,
    val managedEvents: List<ManagedEvent>,
    val joinableEvents: List<Event>,
    val joinedEvents: List<Event>,
    val notifications: List<Notification>,
    val currentUser: User?,
    val dataFetchStatus: StateFetchStatus = StateFetchStatus(),
)

@Serializable
data class ProfileValidationState(
    val firstNameError: String = "",
    val lastNameError: String = "",
    val displayNameError: String = "",
    val hasErrors: Boolean = false
)

