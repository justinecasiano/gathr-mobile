package com.example.gathr.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.example.gathr.presentation.auth.PasswordValidationState
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.example.gathr.data.remote.ApiResult
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException
import java.util.EnumMap

object Utils {

    fun generateQrBitmap(
        text: String,
        width: Int = 512,
        height: Int = 512,
        margin: Int = 1
    ): Bitmap? {
        try {
            val writer = QRCodeWriter()

            val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
            hints[EncodeHintType.MARGIN] = margin

            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints)

            val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            return bmp
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun validateInput(type: String, value: String): String {
        return when (type) {
            "email" -> {
                if (value.isBlank()) "This field is required"
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value)
                        .matches()
                ) "Email is invalid"
                else ""
            }

            "username" -> {
                val usernameRegex = Regex("^[A-Za-z0-9_]{3,20}$")
                if (value.isBlank()) "This field is required"
                else if (!usernameRegex.matches(value)) "Username must have 3-20 characters, using only letters, numbers, and underscores."
                else ""
            }

            "emailForgotPassword" -> {
                if (value.isBlank()) "This field is required"
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value)
                        .matches()
                ) "Email is invalid"
                else ""
            }

            "emailLogin" -> {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value)
                        .matches()
                ) "Email is invalid"
                else ""
            }

            else -> ""
        }
    }

    fun validatePassword(value: String, confirmPassword: String = ""): PasswordValidationState {
        val passwordValidationState = PasswordValidationState()

        val hasMinLength = value.length >= 8
        val hasUppercase = value.any { it.isUpperCase() }
        val hasLowercase = value.any { it.isLowerCase() }
        val hasDigit = value.any { it.isDigit() }
        val hasSpecialChar = value.any { !it.isLetterOrDigit() }
        val hasValidationErrors =
            value.isBlank() || !(hasUppercase && hasLowercase && hasDigit && hasSpecialChar && hasMinLength)
        val hasConfirmPasswordError = value.isBlank() || value != confirmPassword

        return passwordValidationState.copy(
            hasMinLength = hasMinLength,
            hasUppercase = hasUppercase,
            hasLowercase = hasLowercase,
            hasDigit = hasDigit,
            hasSpecialChar = hasSpecialChar,
            hasValidationErrors = hasValidationErrors,
            hasConfirmPasswordError = hasConfirmPasswordError
        )
    }

    suspend fun <T> dbResponseHandler(apiCall: suspend () -> T): ApiResult<T> {
        return try {
            ApiResult.Success(apiCall())
        } catch (e: PostgrestRestException) {
            Log.d("SUPABASE_ERROR", "${e.message}")
            val errorMessage = "A database error occurred"
            ApiResult.Error(errorMessage)
        } catch (e: HttpRequestTimeoutException) {
            ApiResult.Error("The request timed out. Please check your connection and try again.")
        } catch (e: IOException) {
            ApiResult.Error("Unable to connect. Please check your internet connection.")
        } catch (e: ClientRequestException) {
            ApiResult.Error("Client error: ${e.response.status.description}")
        } catch (e: ServerResponseException) {
            ApiResult.Error("Server error: ${e.response.status.description}")
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResult.Error(e.message ?: "An unknown error occurred.")
        }
    }
}