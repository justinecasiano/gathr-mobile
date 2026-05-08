package com.example.gathr.data.repository

import com.example.gathr.data.model.Notification
import com.example.gathr.data.remote.ApiResult
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

interface NotificationRepository {
    suspend fun fetchNotifications(): ApiResult<List<Notification>>
    suspend fun markAsRead(notificationId: Long): ApiResult<Unit>
    suspend fun markAllAsRead(): ApiResult<Unit>
}

class NotificationRepositoryImpl(
    private val auth: Auth,
    private val supabase: SupabaseClient
) : NotificationRepository {

    private val notificationsTable = supabase.from("notifications")

    override suspend fun fetchNotifications(): ApiResult<List<Notification>> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return ApiResult.Error("User session not found")

            val response = notificationsTable.select {
                filter {
                    eq("user_id", currentUser.id)
                }
                order(column = "created_at", order = Order.DESCENDING)
            }

            val notifications = response.decodeList<Notification>()
            ApiResult.Success(notifications)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Failed to fetch notifications")
        }
    }

    override suspend fun markAsRead(notificationId: Long): ApiResult<Unit> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return ApiResult.Error("User session not found")

            notificationsTable.update(
                {
                    set("is_read", true)
                }
            ) {
                filter {
                    eq("id", notificationId)
                    eq("user_id", currentUser.id)
                }
            }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Failed to mark notification as read")
        }
    }

    override suspend fun markAllAsRead(): ApiResult<Unit> {
        return try {
            val currentUser = auth.currentUserOrNull()
                ?: return ApiResult.Error("User session not found")

            notificationsTable.update(
                {
                    set("is_read", true)
                }
            ) {
                filter {
                    eq("user_id", currentUser.id)
                    eq("is_read", false)
                }
            }
            ApiResult.Success(Unit)
        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Failed to mark all as read")
        }
    }
}
