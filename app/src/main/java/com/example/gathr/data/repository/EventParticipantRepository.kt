package com.example.gathr.data.repository

import android.util.Log
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.model.CreateStaff
import com.example.gathr.data.model.Event
import com.example.gathr.data.model.ParticipantType
import com.example.gathr.data.model.User
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.utils.Utils.dbResponseHandler
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.File
import java.util.UUID

interface EventParticipantRepository {
    suspend fun createEvent(
        user: User,
        event: CreateEvent,
        staffs: List<CreateStaff>,
        imageFile: File?
    ): ApiResult<Event>

    suspend fun fetchEvents(): ApiResult<List<Event>>
}

class EventParticipantRepositoryImpl(
    private val postgrest: Postgrest,
    private val auth: Auth,
    private val storage: Storage,
    private val supabase: SupabaseClient
) : EventParticipantRepository {


    override suspend fun fetchEvents(): ApiResult<List<Event>> {
        return try {
            val id = auth.currentUserOrNull()?.id
            val events = supabase.from("events_with_details")
                .select {
                    filter {
                        eq("created_by", UUID.fromString(id))
                    }
                }
                .decodeList<Event>()

            ApiResult.Success(events)

        } catch (e: Exception) {
            val errorMessage = when (e) {
                is RestException -> "Database error has occurred"
                is HttpRequestException -> "Network Error: Check your connection."
                else -> "An unexpected error occurred"
            }
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

                val json = Json { ignoreUnknownKeys = true } // Safety setting
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
