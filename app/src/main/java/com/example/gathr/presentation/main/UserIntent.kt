package com.example.gathr.presentation.main

import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.Participant
import java.io.File

sealed interface UserIntent {
    data class CurrentEventChanged(val value: Event?) : UserIntent
    data class CurrentParticipantChanged(val value: Participant) : UserIntent
    data class CreateEventChanged(val value: CreateEvent) : UserIntent
    data class CreateEventImageFileChanged(val value: File?) : UserIntent
    data class SearchStaffChanged(val value: String) : UserIntent
    data class SearchStaffErrorChanged(val value: String) : UserIntent
    data class AddStaffsChanged(val value: List<CreateStaff>) : UserIntent
    data class ActionTitleChanged(val value: String) : UserIntent
    data class ActionErrorChanged(val value: String) : UserIntent
    data class ActionOnConfirmClicked(val value: () -> Unit) : UserIntent
    data class IsLoadingChanged(val value: Boolean) : UserIntent
    data class IsUpdateEventChanged(val value: Boolean) : UserIntent

    data object FetchEvents : UserIntent
    data object SaveModifiedEvent : UserIntent
    data object RegisterEvent : UserIntent
    data object DeleteEvent : UserIntent
    data object ViewEvent : UserIntent
    data object DoCreateEvent : UserIntent
    data object ValidateAddStaff : UserIntent
    data object ValidateCreateEvent : UserIntent
    data object BackClicked : UserIntent
    data object LogoutClicked : UserIntent
}

sealed interface UserEffect {
    data object NavigateToNext : UserEffect
    data object NavigateBack : UserEffect
    data object NavigateLogout : UserEffect
}