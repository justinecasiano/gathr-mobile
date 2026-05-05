package com.example.gathr.presentation.main

import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.Participant
import com.example.gathr.presentation.participant.ParticipantPayload
import java.io.File

sealed interface UserIntent {
    data class CurrentEventChanged(val value: Event?) : UserIntent
    data class CurrentParticipantChanged(val value: Participant) : UserIntent
    data class AddStaffsChanged(val value: List<CreateStaff>) : UserIntent

    data class CreateEventChanged(val value: CreateEvent) : UserIntent
    data class CreateEventImageFileChanged(val value: File?) : UserIntent
    data object CreateEventOnClear : UserIntent

    data class ScanParticipantChanged(val value: ParticipantPayload) : UserIntent
    data class SearchStaffChanged(val value: String) : UserIntent
    data class SearchStaffErrorChanged(val value: String) : UserIntent

    data class ActionTitleChanged(val value: String) : UserIntent
    data class ActionErrorChanged(val value: String) : UserIntent
    data class ActionOnConfirmClicked(val value: () -> Unit) : UserIntent
    data object ActionOnClear : UserIntent

    data class IsLoadingChanged(val value: Boolean) : UserIntent
    data class IsUpdateEventChanged(val value: Boolean) : UserIntent

    data class EventsSearchTextChanged(val value: String) : UserIntent
    data class MyEventsSelectedTabChanged(val value: Int) : UserIntent
    data class MyEventsSearchTextChanged(val value: String) : UserIntent
    data class ETicketsSelectedTabChanged(val value: Int) : UserIntent
    data class ETicketsSearchTextChanged(val value: String) : UserIntent

    data object FetchManagedEvents: UserIntent
    data object FetchJoinableEvents : UserIntent
    data object FetchJoinedEvents: UserIntent
    data object FetchAvailableStaff: UserIntent
    data object FetchNotifications: UserIntent
    data object FetchAttendance: UserIntent

    data object MarkAttendance: UserIntent

    // ATTENDEE MODULE
    data object RegisterEvent : UserIntent
    data object CancelEvent : UserIntent

    // ORGANIZER MODULE
    data object DeleteEvent : UserIntent
    data object UpdateEvent : UserIntent
    data object DoCreateEvent : UserIntent
    data object ValidateAddStaff : UserIntent
    data object ValidateCreateEvent : UserIntent

    data object BackClicked : UserIntent
    data object LogoutClicked: UserIntent
}

sealed interface UserEffect {
    data object NavigateNext : UserEffect
    data object NavigateBack : UserEffect
}

sealed interface MainEffect {
    data object NavigateModeratorViewEvent: MainEffect

    data object NavigateFeedback : MainEffect
    data object NavigateEditProfile : MainEffect
    data object NavigateQrCode : MainEffect
    data object NavigateQrScanner : MainEffect
    data object NavigateUpdateEvent : MainEffect
    data object NavigateCreateEvent : MainEffect
    data object NavigateParticipantViewEvent : MainEffect
    data object NavigateAttendance : MainEffect
    data object NavigateStaff : MainEffect
    data object NavigateBackUser : MainEffect
    data object NavigateLogout: MainEffect
}