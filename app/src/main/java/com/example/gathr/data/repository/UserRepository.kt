package com.example.gathr.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import io.ktor.http.ContentType
import com.example.gathr.data.model.Notification
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.model.User
import com.example.gathr.utils.Utils.dbResponseHandler
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.JsonObject
import androidx.core.net.toUri
import java.io.File

interface UserRepository {
    suspend fun getCurrentUserProfile(): ApiResult<User>
    suspend fun getUserById(id: String): ApiResult<User>
    suspend fun getUserByEmailOrUsername(emailOrUsername: String): ApiResult<User>
    suspend fun updateUserProfile(id: String, updates: JsonObject): ApiResult<User>
    suspend fun uploadAvatar(userId: String, uriString: String): ApiResult<String>
}

class UserRepositoryImpl(
    private val auth: Auth,
    private val supabase: SupabaseClient,
    private val context: Context
) : UserRepository {

    private val usersTable = supabase.from("users")

    override suspend fun getCurrentUserProfile(): ApiResult<User> {
        if (auth.currentUserOrNull() == null) {
            return ApiResult.Error("User not authenticated")
        }

        val currentUserId = auth.currentUserOrNull()?.id
            ?: return ApiResult.Error("Not authenticated")

        return getUserById(currentUserId)
    }

    override suspend fun getUserById(id: String): ApiResult<User> {
        return dbResponseHandler {
            usersTable
                .select { filter { eq("id", id) } }
                .decodeSingle<User>()
        }
    }

    override suspend fun getUserByEmailOrUsername(emailOrUsername: String): ApiResult<User> {
        return dbResponseHandler {
            usersTable
                .select {
                    filter {
                        or {
                            eq("email", emailOrUsername)
                            eq("display_name", emailOrUsername)
                        }
                        neq("role", "MODERATOR")
                    }
                }
                .decodeSingle<User>()
        }
    }

    override suspend fun updateUserProfile(
        id: String,
        updates: JsonObject
    ): ApiResult<User> {
        return dbResponseHandler {
            usersTable
                .update(updates)
                {
                    filter { eq("id", id) }
                    select()
                }
                .decodeSingle<User>()
        }
    }

    override suspend fun uploadAvatar(userId: String, uriString: String): ApiResult<String> {
        return try {
            val uri = uriString.toUri()

            val imageBytes = context.contentResolver.openInputStream(uri)?.use {
                it.readBytes()
            } ?: return ApiResult.Error("Could not read image data")

            val path = "$userId/profile"
            val avatarBucket = supabase.storage.from("avatars")
            avatarBucket.upload(path, imageBytes) {
                upsert = true
                contentType = ContentType.parse("image/webp")
            }

            try {
                val file = File(uri.path ?: "")
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                Log.e("REPO", "Failed to delete temp file: ${e.message}")
            }

            val url = avatarBucket.publicUrl(path)
            ApiResult.Success(url)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Upload failed")
        }
    }
}