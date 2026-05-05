package com.example.gathr.data.repository

import com.example.gathr.data.model.Notification
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
}

class UserRepositoryImpl(
    private val auth: Auth,
    supabase: SupabaseClient
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
}