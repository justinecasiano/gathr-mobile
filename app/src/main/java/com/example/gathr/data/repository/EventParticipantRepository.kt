package com.example.gathr.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.EventApprovalStatus
import com.example.gathr.data.model.FormSubmission
import com.example.gathr.data.model.ManagedEvent
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.User
import com.example.gathr.data.remote.ApiResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.ContentType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.io.File
import java.time.Instant
import java.util.UUID

interface EventParticipantRepository {
    suspend fun fetchModeratorEvents(): ApiResult<List<Event>>
    suspend fun fetchManagedEvents(): ApiResult<List<ManagedEvent>>
    suspend fun fetchJoinableEvents(): ApiResult<List<Event>>
    suspend fun fetchJoinedEvents(): ApiResult<List<Event>>
    suspend fun fetchAvailableStaff(
        organizerId: String,
        alreadySelectedStaffIds: List<String>
    ): ApiResult<List<User>>

    suspend fun fetchAttendance(eventId: Long): ApiResult<List<Participant>>
    fun observeAttendance(eventId: Long): Flow<Unit>
    suspend fun markAttendance(eventId: Long, userId: UUID): ApiResult<Unit>
    suspend fun registerEvent(eventId: Long, userId: UUID): ApiResult<Unit>
    suspend fun cancelEvent(eventId: Long, userId: UUID): ApiResult<Unit>
    suspend fun deleteEvent(eventId: Long, userId: UUID, imageUrl: String?): ApiResult<Unit>

    suspend fun createEvent(user: User, event: CreateEvent): ApiResult<ManagedEvent>
    suspend fun updateEvent(user: User, event: CreateEvent): ApiResult<ManagedEvent>
    suspend fun fetchUserEventToUpdate(eventId: Long, userId: UUID): ApiResult<ManagedEvent>

    fun observeEventsAndParticipants(): Flow<Long>
    suspend fun fetchSingleEvent(eventId: Long, userId: UUID): ApiResult<Event>
    suspend fun fetchSingleEventForModerator(eventId: Long): ApiResult<Event>
    suspend fun updateEventStatus(
        eventId: Long,
        moderatorId: UUID,
        status: EventApprovalStatus,
        comment: String?
    ): ApiResult<Unit>

    suspend fun submitFeedback(
        eventId: Long,
        userId: UUID,
        submission: FormSubmission,
        rating: Int?,
        comment: String?
    ): ApiResult<Unit>
}

class EventParticipantRepositoryImpl(
    private val context: Context,
    private val auth: Auth,
    private val storage: Storage,
    private val realtime: Realtime,
    private val supabase: SupabaseClient
) : EventParticipantRepository {

    private val jsonConfig = Json { ignoreUnknownKeys = true }

    override suspend fun fetchModeratorEvents(): ApiResult<List<Event>> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_user_id", auth.currentUserOrNull()?.id.toString())
            }

            val response = supabase.postgrest.rpc(
                function = "get_moderator_management_events",
                parameters = rpcParams
            )

            val events = jsonConfig.decodeFromString<List<Event>>(response.data)
            ApiResult.Success(events)
        } catch (e: Exception) {
            Log.e("FETCH_MODERATOR_EVENTS", "Failed", e)
            ApiResult.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun fetchManagedEvents(): ApiResult<List<ManagedEvent>> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_user_id", auth.currentUserOrNull()?.id)
            }

            val response = supabase.postgrest.rpc(
                function = "get_user_management_events",
                parameters = rpcParams
            )

            val events = jsonConfig.decodeFromString<List<ManagedEvent>>(response.data)
            ApiResult.Success(events)
        } catch (e: Exception) {
            Log.d("FETCH_MANAGED_EVENTS", e.message.toString())
            ApiResult.Error(e.message.toString())
        }
    }

    override suspend fun fetchJoinableEvents(): ApiResult<List<Event>> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_user_id", auth.currentUserOrNull()?.id)
            }

            val response = supabase.postgrest.rpc(
                function = "get_joinable_events",
                parameters = rpcParams
            )

            val events = jsonConfig.decodeFromString<List<Event>>(response.data)
            ApiResult.Success(events)
        } catch (e: Exception) {
            Log.d("FETCH_JOINABLE_EVENTS", e.message.toString())
            ApiResult.Error(e.message.toString())
        }
    }

    override suspend fun fetchJoinedEvents(): ApiResult<List<Event>> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_user_id", auth.currentUserOrNull()?.id)
            }

            val response = supabase.postgrest.rpc(
                function = "get_joined_events_as_attendee",
                parameters = rpcParams
            )

            val events = jsonConfig.decodeFromString<List<Event>>(response.data)
            ApiResult.Success(events)
        } catch (e: Exception) {
            Log.d("FETCH_JOINED_EVENTS", e.message.toString())
            ApiResult.Error(e.message.toString())
        }
    }

    override suspend fun fetchAvailableStaff(
        organizerId: String,
        alreadySelectedStaffIds: List<String>
    ): ApiResult<List<User>> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_organizer_id", organizerId)
                put("p_excluded_ids", buildJsonArray {
                    alreadySelectedStaffIds.forEach { add(it) }
                })
            }

            val response = supabase.postgrest.rpc(
                function = "get_available_staff_simple",
                parameters = rpcParams
            )

            val availableStaff = jsonConfig.decodeFromString<List<User>>(response.data)
            ApiResult.Success(availableStaff)
        } catch (e: Exception) {
            Log.d("FETCH_AVAILABLE_STAFF", e.message.toString())
            ApiResult.Error(e.message.toString())
        }
    }

    override suspend fun fetchAttendance(eventId: Long): ApiResult<List<Participant>> {
        return try {

            val rpcParams = buildJsonObject {
                put("p_event_id", eventId)
            }

            val response = supabase.postgrest.rpc(
                function = "get_event_participants",
                parameters = rpcParams
            )

            val participants = jsonConfig.decodeFromString<List<Participant>>(response.data)
            ApiResult.Success(participants)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message.toString())
        }
    }

    override fun observeAttendance(eventId: Long): Flow<Unit> = callbackFlow {
        val channel = supabase.channel("attendance_${eventId}_${System.currentTimeMillis()}")

        val dataChangeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "participants"
            filter("event_id", FilterOperator.EQ, eventId)
        }

        val job = launch {
            dataChangeFlow.collect { action ->
                send(Unit)
            }
        }

        channel.subscribe()

        awaitClose {
            launch {
                channel.unsubscribe()
                supabase.realtime.removeAllChannels()
                supabase.realtime.removeChannel(channel)
            }
            job.cancel()
        }
    }

    override suspend fun markAttendance(eventId: Long, userId: UUID): ApiResult<Unit> {
        return try {
            val updateParticipant = buildJsonObject {
                put("status", "PRESENT")
                put("check_in", Instant.now().toString())
            }

            supabase.from("participants").update(updateParticipant) {
                filter {
                    eq("event_id", eventId)
                    eq("user_id", userId)
                    eq("status", "REGISTERED")
                }
            }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is RestException -> "Database error has occurred"
                is HttpRequestException -> "Network Error: Check your internet connection."
                else -> "An unexpected error occurred"
            }
            Log.e("CANCEL_EVENT", "Operation failed", e)
            ApiResult.Error(errorMessage)
        }
    }

    override suspend fun registerEvent(eventId: Long, userId: UUID): ApiResult<Unit> {
        return try {
            val params = buildJsonObject {
                put("p_event_id", eventId)
                put("p_user_id", userId.toString())
            }

            supabase.postgrest.rpc("register_for_event", params)

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is RestException -> {
                    e.message
                        ?: "Registration failed: The event might be full or you are already registered."
                }

                is HttpRequestTimeoutException -> "Request timed out. Please try again."
                is HttpRequestException -> "Network Error: Check your internet connection."
                else -> e.message ?: "An unexpected error occurred"
            }

            Log.e("REGISTER_EVENT", "Operation failed", e)
            ApiResult.Error(errorMessage)
        }
    }

    override suspend fun cancelEvent(eventId: Long, userId: UUID): ApiResult<Unit> {
        return try {
            val params = buildJsonObject {
                put("p_event_id", eventId)
                put("p_user_id", userId.toString())
            }

            supabase.postgrest.rpc("cancel_event_registration", params)

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is RestException -> e.message ?: "Failed to cancel registration."
                else -> "An unexpected error occurred while canceling."
            }
            ApiResult.Error(errorMessage)
        }
    }

    override suspend fun deleteEvent(
        eventId: Long,
        userId: UUID,
        imageUrl: String?
    ): ApiResult<Unit> {
        return try {
            if (!imageUrl.isNullOrBlank()) {
                try {
                    val path = imageUrl.substringAfterLast("/public/event-images/")

                    storage.from("events-background-image").delete(path)
                    Log.d("DELETE_STORAGE", "Successfully removed image: $path")
                } catch (e: Exception) {
                    Log.e("DELETE_STORAGE", "Failed to delete image from bucket: ${e.message}")
                }
            }

            supabase.from("events").update(
                {
                    set("is_archive", true)
                    set("deleted_at", Instant.now().toString())
                }
            ) {
                filter {
                    eq("id", eventId)
                    eq("created_by", userId)
                }
            }

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is RestException -> "Database error has occurred"
                is HttpRequestException -> "Network Error: Check your internet connection."
                else -> "An unexpected error occurred"
            }
            Log.e("DELETE_EVENT", "Operation failed", e)
            ApiResult.Error(errorMessage)
        }
    }

    override suspend fun createEvent(user: User, event: CreateEvent): ApiResult<ManagedEvent> {
        val imageUriString = event.backgroundImage ?: return ApiResult.Error("Image is missing")
        val imageUri = imageUriString.toUri()

        val fileName = "${UUID.randomUUID()}.webp"
        val bucketName = "events-background-image"
        val bucket = storage.from(bucketName)

        return try {
            val imageBytes =
                context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                    ?: return ApiResult.Error("Could not read image file")

            bucket.upload(path = fileName, data = imageBytes) {
                upsert = false
                contentType = ContentType.parse("image/webp")
            }
            val imageUrl = bucket.publicUrl(fileName)

            try {
                val file = File(imageUri.path ?: "")
                if (file.exists()) {
                    file.delete()
                    Log.d("CREATE_EVENT", "Local cache file deleted: ${imageUri.path}")
                }
            } catch (cleanupError: Exception) {
                Log.e("CREATE_EVENT", "Cleanup failed", cleanupError)
            }

            try {
                val allParticipants = event.staffs.map {
                    buildJsonObject {
                        put("user_id", it.userId.toString())
                        put("participant_type", it.participantType.toString())
                    }
                } + buildJsonObject {
                    put("user_id", user.id.toString())
                    put("participant_type", ParticipantType.ORGANIZER.toString())
                }

                val rpcParams = buildJsonObject {
                    put("p_title", event.title)
                    put("p_description", event.description)
                    put("p_background_image", imageUrl)
                    put("p_capacity", event.capacity)
                    put("p_location", event.location)
                    put("p_start_time", event.startDateAndTime.toString())
                    put("p_end_time", event.endDateAndTime.toString())
                    put("p_created_by", user.id.toString())
                    put("p_allow_alumni", event.allowAlumni)
                    put("p_allow_non_umak", true)
                    put("p_allowed_departments", buildJsonArray {
                        event.allowedDepartments.forEach { add(it.name) }
                    })
                    put("p_participants", buildJsonArray {
                        allParticipants.forEach { add(it) }
                    })
                }

                val response = supabase.postgrest.rpc("create_event_with_participants", rpcParams)

                val json = Json { ignoreUnknownKeys = true }
                val createdEvent = json.decodeFromString<ManagedEvent>(response.data)

                ApiResult.Success(createdEvent)
            } catch (dbError: Exception) {
                bucket.delete(listOf(fileName))
                throw dbError
            }
        } catch (e: Exception) {
            Log.e("CREATE_EVENT", "Failed", e)
            ApiResult.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun updateEvent(user: User, event: CreateEvent): ApiResult<ManagedEvent> {
        val eventId = event.id ?: return ApiResult.Error("Event ID is missing")

        val oldImageUrl = event.oldBackgroundImageUrl
        var currentImageUrl = event.backgroundImage ?: ""
        val bucketName = "events-background-image"
        val bucket = storage.from(bucketName)
        var newFileName: String? = null
        var isNewImageUploaded = false

        return try {
            if (currentImageUrl.isNotBlank() && !currentImageUrl.startsWith("http")) {
                val imageUri = currentImageUrl.toUri()
                val fileName = "${UUID.randomUUID()}.webp"
                newFileName = fileName

                val imageBytes =
                    context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                        ?: return ApiResult.Error("Could not read image file")

                bucket.upload(path = fileName, data = imageBytes) {
                    upsert = false
                    contentType = ContentType.parse("image/webp")
                }

                currentImageUrl = bucket.publicUrl(fileName)
                isNewImageUploaded = true

                try {
                    val file = File(imageUri.path ?: "")
                    if (file.exists()) file.delete()
                } catch (e: Exception) {
                    Log.e("STORAGE_CLEANUP", "Failed to delete local temp file")
                }
            }

            try {
                val allStaff = event.staffs.map {
                    buildJsonObject {
                        put("user_id", it.userId.toString())
                        put("participant_type", it.participantType.toString())
                    }
                }

                val rpcParams = buildJsonObject {
                    put("p_event_id", eventId)
                    put("p_title", event.title)
                    put("p_description", event.description)
                    put("p_background_image", currentImageUrl)
                    put("p_capacity", event.capacity)
                    put("p_location", event.location)
                    put("p_start_time", event.startDateAndTime.toString())
                    put("p_end_time", event.endDateAndTime.toString())
                    put("p_allow_alumni", event.allowAlumni)
                    put("p_allow_non_umak", true)
                    put("p_allowed_departments", buildJsonArray {
                        event.allowedDepartments.forEach { add(it.name) }
                    })
                    put("p_participants", buildJsonArray {
                        allStaff.forEach { add(it) }
                    })
                }

                val response = supabase.postgrest.rpc("update_event_with_participants", rpcParams)

                val json = Json { ignoreUnknownKeys = true }
                val updatedManagedEvent = json.decodeFromString<ManagedEvent>(response.data)

                if (isNewImageUploaded && !oldImageUrl.isNullOrBlank()) {
                    try {
                        val oldFileName = oldImageUrl.substringAfterLast("/")
                        val cleanOldFileName = oldFileName.substringBefore("?")
                        bucket.delete(listOf(cleanOldFileName))
                    } catch (e: Exception) {
                        Log.e(
                            "STORAGE_CLEANUP",
                            "Failed to delete old image from bucket: $oldImageUrl"
                        )
                    }
                }

                Log.e("UPDATE_EVENT_INPUT", event.toString())
                Log.e("UPDATE_EVENT_UPDATED", updatedManagedEvent.toString())
                ApiResult.Success(updatedManagedEvent)
            } catch (dbError: Exception) {
                newFileName?.let { bucket.delete(listOf(it)) }
                throw dbError
            }
        } catch (e: Exception) {
            Log.e("UPDATE_EVENT", "Failed to update event", e)
            ApiResult.Error(e.message ?: "An unexpected error occurred")
        }
    }

    override suspend fun fetchUserEventToUpdate(
        eventId: Long,
        userId: UUID
    ): ApiResult<ManagedEvent> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_event_id", eventId)
                put("p_user_id", userId.toString())
            }

            val response = supabase.postgrest.rpc(
                function = "get_user_event_to_update",
                parameters = rpcParams
            )

            val events = jsonConfig.decodeFromString<ManagedEvent>(response.data)
            ApiResult.Success(events)
        } catch (e: Exception) {
            Log.d("FETCH_USER_EVENT_TO_UPDATE", e.message.toString())
            ApiResult.Error(e.message.toString())
        }
    }

    override fun observeEventsAndParticipants(): Flow<Long> = callbackFlow {
        val channel = supabase.channel("global_broadcast_${System.currentTimeMillis()}")

        val eventChangeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "events"
        }

        val participantChangeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "participants"
        }

        val job = launch {
            merge(eventChangeFlow, participantChangeFlow).collect { action ->
                val affectedEventId = when (action) {
                    is PostgresAction.Update -> action.record["id"]?.jsonPrimitive?.longOrNull
                        ?: action.record["event_id"]?.jsonPrimitive?.longOrNull

                    is PostgresAction.Insert -> action.record["id"]?.jsonPrimitive?.longOrNull
                        ?: action.record["event_id"]?.jsonPrimitive?.longOrNull

                    is PostgresAction.Delete -> action.oldRecord["id"]?.jsonPrimitive?.longOrNull
                        ?: action.oldRecord["event_id"]?.jsonPrimitive?.longOrNull

                    else -> null
                }

                if (affectedEventId != null) {
                    send(affectedEventId)
                }
            }
        }

        channel.subscribe()

        awaitClose {
            launch {
                realtime.removeAllChannels()
                realtime.removeChannel(channel)
                channel.unsubscribe()
            }
            job.cancel()
        }
    }

    override suspend fun fetchSingleEvent(eventId: Long, userId: UUID): ApiResult<Event> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_event_id", eventId)
                put("p_user_id", userId.toString())
            }

            val response = supabase.postgrest.rpc("get_single_event_details", rpcParams)
            val event = jsonConfig.decodeFromString<Event>(response.data)
            ApiResult.Success(event)
        } catch (e: Exception) {
            ApiResult.Error(e.message.toString())
        }
    }

    override suspend fun fetchSingleEventForModerator(eventId: Long): ApiResult<Event> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_event_id", eventId)
            }

            val response = supabase.postgrest.rpc("get_event_details_for_moderator", rpcParams)
            val event = jsonConfig.decodeFromString<Event>(response.data)
            ApiResult.Success(event)
        } catch (e: Exception) {
            Log.e("FETCH_MODERATOR_SINGLE", "Failed", e)
            ApiResult.Error(e.message ?: "Failed to fetch event details")
        }
    }

    override suspend fun updateEventStatus(
        eventId: Long,
        moderatorId: UUID,
        status: EventApprovalStatus,
        comment: String?
    ): ApiResult<Unit> {
        return try {
            val updates = buildJsonObject {
                put("status", status.name)
                put("approved_by", moderatorId.toString())
                put("comment", comment)
                if (status == EventApprovalStatus.APPROVED) {
                    put("approved_at", Instant.now().toString())
                }
                put("updated_at", Instant.now().toString())
            }

            supabase.from("events").update(updates) {
                filter { eq("id", eventId) }
            }

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("MODERATOR_ACTION", "Failed to update event status", e)
            ApiResult.Error(e.message ?: "Failed to update event status")
        }
    }

    override suspend fun submitFeedback(
        eventId: Long,
        userId: UUID,
        submission: FormSubmission,
        rating: Int?,
        comment: String?
    ): ApiResult<Unit> {
        return try {
            val updates = buildJsonObject {
                put("feedback_submission", Json.encodeToJsonElement(submission))
                put("feedback_submitted_at", Instant.now().toString())
                put("response_status", "ANSWERED")
                put("status", "PRESENT")
                put("rating", rating)
                put("comment", comment)
            }

            supabase.from("participants").update(updates) {
                filter {
                    eq("event_id", eventId)
                    eq("user_id", userId.toString())
                }
            }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            Log.e("SUBMIT_FEEDBACK", "Failed", e)
            ApiResult.Error(e.message ?: "Failed to submit feedback")
        }
    }
}
