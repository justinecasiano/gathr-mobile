package com.example.gathr.data.repository

import android.util.Log
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.ManagedEvent
import com.example.gathr.data.model.Participant
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.User
import com.example.gathr.data.remote.ApiResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import java.time.Instant
import java.util.UUID

interface EventParticipantRepository {
    suspend fun fetchManagedEvents(): ApiResult<List<ManagedEvent>>
    suspend fun fetchJoinableEvents(): ApiResult<List<Event>>
    suspend fun fetchJoinedEvents(): ApiResult<List<Event>>
    suspend fun fetchAvailableStaff(eventId: Long): ApiResult<List<User>>
    suspend fun fetchAttendance(eventId: Long): ApiResult<List<Participant>>
    fun observeAttendance(eventId: Long): Flow<Unit>
    suspend fun markAttendance(eventId: Long, userId: UUID): ApiResult<Unit>
    suspend fun registerEvent(eventId: Long, userId: UUID): ApiResult<Unit>
    suspend fun cancelEvent(eventId: Long, userId: UUID): ApiResult<Unit>
    suspend fun deleteEvent(eventId: Long, userId: UUID): ApiResult<Unit>

    suspend fun createEvent(
        user: User,
        event: CreateEvent,
        staffs: List<CreateStaff>,
        imageFile: File?
    ): ApiResult<Event>

}

class EventParticipantRepositoryImpl(
    private val auth: Auth,
    private val storage: Storage,
    private val realtime: Realtime,
    private val supabase: SupabaseClient
) : EventParticipantRepository {

    private val jsonConfig = Json { ignoreUnknownKeys = true }

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

    override suspend fun fetchAvailableStaff(eventId: Long): ApiResult<List<User>> {
        return try {
            val rpcParams = buildJsonObject {
                put("p_event_id", eventId)
            }

            val response = supabase.postgrest.rpc(
                function = "get_available_staff",
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

    override suspend fun cancelEvent(eventId: Long, userId: UUID): ApiResult<Unit> {
        return try {
            val updateParticipant = buildJsonObject {
                put("status", "CANCELLED")
            }

            supabase.from("participants").update(updateParticipant) {
                filter {
                    eq("event_id", eventId)
                    eq("user_id", userId)
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

            val newParticipant = buildJsonObject {
                put("event_id", eventId)
                put("user_id", userId.toString())
                put("participant_type", "ATTENDEE")
                put("status", "REGISTERED")
            }

            supabase.from("participants").insert(newParticipant)

            ApiResult.Success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is RestException -> "Database error has occurred"
                is HttpRequestException -> "Network Error: Check your internet connection."
                else -> "An unexpected error occurred"
            }
            Log.e("REGISTER_EVENT", "Operation failed", e)
            ApiResult.Error(errorMessage)
        }
    }

    override suspend fun deleteEvent(eventId: Long, userId: UUID): ApiResult<Unit> {
        return try {
            supabase.from("events").delete {
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

    override suspend fun createEvent(
        user: User,
        event: CreateEvent,
        staffs: List<CreateStaff>,
        imageFile: File?
    ): ApiResult<Event> {
        if (imageFile == null) return ApiResult.Error("Image file is missing")

        val fileName = "${UUID.randomUUID()}.${imageFile.extension}"
        val bucketName = "events-background-image"
        val bucket = storage.from(bucketName)

        return try {
            bucket.upload(
                path = fileName,
                data = imageFile.readBytes()
            ) {
                upsert = false
            }

            val imageUrl = bucket.publicUrl(fileName)

            try {
                val staffsWithOrganizer = staffs + CreateStaff(
                    userId = user.id,
                    participantType = ParticipantType.ORGANIZER
                )
                val jsonParticipants = buildJsonArray {
                    staffsWithOrganizer.forEach { staff ->
                        addJsonObject {
                            put("user_id", staff.userId.toString())
                            put("participant_type", staff.participantType.toString())
                        }
                    }
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

                    put("p_participants", jsonParticipants)
                }

                val response = supabase.postgrest.rpc(
                    function = "create_event_with_participants",
                    parameters = rpcParams
                )

                val json = Json { ignoreUnknownKeys = true }
                val createdEvent = json.decodeFromString<Event>(response.data)

                Log.d("CREATE_EVENT", "Created Event: ${createdEvent.toString()}")

                ApiResult.Success(createdEvent)

            } catch (dbError: Exception) {
                Log.d("CREATE_EVENT", "DB Transaction failed. Rolling back storage...")
                try {
                    bucket.delete(listOf(fileName))
                } catch (e: Exception) {
                    Log.e("CREATE_EVENT", "Critical: Failed to rollback image: $fileName", e)
                }
                throw dbError
            }
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is RestException -> "Database error has occurred"
                is HttpRequestException -> "Network Error: Check your internet connection."
                else -> "An unexpected error occurred"
            }
            Log.e("CREATE_EVENT", "Operation failed", e)
            ApiResult.Error(errorMessage)
        } finally {
            if (imageFile.exists()) {
                imageFile.delete()
            }
        }
    }
}
