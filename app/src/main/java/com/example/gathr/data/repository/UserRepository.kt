package com.example.gathr.data.repository

import com.example.gathr.data.model.FcmToken
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.data.model.User
import com.example.gathr.utils.Utils.dbResponseHandler
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonObject

interface UserRepository {
    suspend fun getCurrentUserProfile(): ApiResult<User>
    suspend fun getUserById(id: String): ApiResult<User>
    suspend fun getUserByEmailOrUsername(emailOrUsername: String): ApiResult<User>
    suspend fun updateUserProfile(id: String, updates: JsonObject): ApiResult<User>
    suspend fun updateFcmToken(newToken: String): ApiResult<Unit>
}

class UserRepositoryImpl(
    private val auth: Auth,
    supabase: SupabaseClient
) : UserRepository {

    private val usersTable = supabase.from("users")

    override suspend fun getCurrentUserProfile(): ApiResult<User> {
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

    override suspend fun updateFcmToken(newToken: String): ApiResult<Unit> {
        return dbResponseHandler {
            val currentUserId = auth.currentUserOrNull()?.id
                ?: throw Exception("User not logged in")

            val existingRow = usersTable.select(
                columns = Columns.list("id", "fcm_token")
            ) {
                filter { eq("id", currentUserId) }
            }.decodeList<FcmToken>().firstOrNull()

            if (existingRow == null) {
                val payload = FcmToken(id = currentUserId, fcmToken = newToken)
                usersTable.insert(payload)
            } else {
                if (existingRow.fcmToken != newToken) {
                    usersTable.update({
                        set("fcm_token", newToken)
                    }) {
                        filter { eq("id", currentUserId) }
                    }
                }
            }
            Unit
        }
    }
}