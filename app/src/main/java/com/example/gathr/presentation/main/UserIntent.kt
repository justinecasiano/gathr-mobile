package com.example.gathr.presentation.main

import android.content.Context
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.Event
import com.example.gathr.presentation.participant.ParticipantPayload

sealed interface UserIntent {
    data class CurrentEventChanged(val value: Event?) : UserIntent
    data class CurrentCreateEventChanged(val value: CreateEvent) : UserIntent
    data class UpdateUserProfile(
        val firstName: String,
        val lastName: String,
        val displayName: String,
        val newAvatarUri: String?
    ) : UserIntent

    data class ScanParticipantChanged(val value: ParticipantPayload) : UserIntent
    data class SearchStaffChanged(val value: String) : UserIntent

    data class ActionTitleChanged(val value: String) : UserIntent
    data class ActionErrorChanged(val value: String) : UserIntent
    data class ActionOnConfirmClicked(val value: () -> Unit) : UserIntent
    data object ActionOnClear : UserIntent

    data class IsLoadingChanged(val value: Boolean) : UserIntent

    data class ActiveEventsFilterChanged(val value: String?) : UserIntent
    data class EventsSearchTextChanged(val value: String) : UserIntent
    data class MyEventsSelectedTabChanged(val value: Int) : UserIntent
    data class MyEventsSearchTextChanged(val value: String) : UserIntent
    data class ETicketsSelectedTabChanged(val value: Int) : UserIntent
    data class ETicketsSearchTextChanged(val value: String) : UserIntent
    data class StaffSelectedTabChanged(val value: Int) : UserIntent
    data class StaffSearchTextChanged(val value: String) : UserIntent
    data class MarkNotificationAsRead(val value: Long) : UserIntent

    data object FetchManagedEvents : UserIntent
    data object FetchJoinableEvents : UserIntent
    data object FetchJoinedEvents : UserIntent
    data object FetchEventToUpdate : UserIntent
    data object FetchAvailableStaff : UserIntent
    data object FetchNotifications : UserIntent

    data object MarkAttendance : UserIntent
    data object MarkAllNotificationsAsRead : UserIntent

    data object RegisterEvent : UserIntent
    data object CancelEvent : UserIntent

    data object DeleteEvent : UserIntent
    data object UpdateEvent : UserIntent
    data object DoCreateEvent : UserIntent
    data object VerifyCreateEvent : UserIntent

    data object CreateEventNextStepClicked : UserIntent
    data object CreateEventPreviousStepClicked : UserIntent
    data object BackClicked : UserIntent
    data object LogoutClicked : UserIntent
}

sealed interface UserEffect {
    data object NavigateNext : UserEffect
    data object NavigateBack : UserEffect
}

sealed interface MainEffect {
    data object NavigateModeratorViewEvent : MainEffect

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
    data object NavigateLogout : MainEffect
}