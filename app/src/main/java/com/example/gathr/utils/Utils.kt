package com.example.gathr.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.example.gathr.presentation.auth.PasswordValidationState
import com.google.zxing.qrcode.QRCodeWriter
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.example.gathr.data.model.CreateEvent
import com.example.gathr.data.remote.ApiResult
import com.example.gathr.presentation.main.CreateEventValidationState
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.io.IOException
import java.time.Clock
import java.time.Instant
import java.util.EnumMap
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object Utils {
    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        filename: String = "Gathr_${System.currentTimeMillis()}.png"
    ) {

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }

        var outputStream: OutputStream? = null
        var uri: Uri? = null

        try {
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (uri == null) {
                throw java.io.IOException("Failed to create new MediaStore record.")
            }

            outputStream = resolver.openOutputStream(uri)
            if (outputStream == null) {
                throw java.io.IOException("Failed to get output stream.")
            }

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()

            Toast.makeText(context, "Ticket saved to Gallery", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e("SAVE_IMAGE", "Error saving bitmap: ${e.message}", e)
            if (uri != null) {
                resolver.delete(uri, null, null)
            }
            Toast.makeText(context, "Failed to save ticket", Toast.LENGTH_LONG).show()
        } finally {
            outputStream?.close()
        }
    }

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

    fun getFileSize(context: Context, uriString: String?): Long {
        if (uriString == null) return 0L

        val uri = uriString.toUri()
        val cursor = context.contentResolver.query(uri, null, null, null, null)

        return cursor?.use {
            if (it.moveToFirst()) {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    it.getLong(sizeIndex)
                } else 0L
            } else 0L
        } ?: 0L
    }

    fun createTempFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver

            val mimeType = contentResolver.getType(uri)

            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

            val tempFile = File.createTempFile("upload_", ".$extension", context.cacheDir)

            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

    fun validateCreateEvent(event: CreateEvent): CreateEventValidationState {
        val currentMoment = Instant.now()

        val titleError = if (event.title.trim().length < 5) {
            "Title is too short (minimum 5 characters)"
        } else ""

        val descriptionError = if (event.description.trim().length < 10) {
            "Description is too short (minimum 10 characters)"
        } else ""

        val locationError = if (event.location.trim().length < 3) {
            "Location is too short (minimum 3 characters)"
        } else ""

        val capacityError = if (event.capacity == null || event.capacity <= 2) {
            "Please enter a valid capacity greater than 2"
        } else ""

        val MAX_IMAGE_SIZE = 5_242_880
        val backgroundImageError = when {
            event.backgroundImage.isNullOrBlank() -> "Background image is required."
            event.backgroundImageSizeBytes > MAX_IMAGE_SIZE -> "Image is too large (Max 5MB)."
            else -> ""
        }

        val startDateError = if (event.startDateAndTime < currentMoment) {
            "Start time cannot be in the past"
        } else ""

        val endDateError = if (event.endDateAndTime <= event.startDateAndTime) {
            "End time must be after the start time"
        } else ""

        val hasErrors = listOf(
            titleError,
            descriptionError,
            locationError,
            capacityError,
            backgroundImageError,
            startDateError,
            endDateError
        ).any { it.isNotBlank() }

        return CreateEventValidationState(
            titleError = titleError,
            descriptionError = descriptionError,
            backgroundImageError = backgroundImageError,
            capacityError = capacityError,
            locationError = locationError,
            startDateAndTimeError = startDateError,
            endDateAndTimeError = endDateError,
            hasErrors = hasErrors
        )
    }

    suspend fun <T> dbResponseHandler(apiCall: suspend () -> T): ApiResult<T> {
        return try {
            ApiResult.Success(apiCall())
        } catch (e: PostgrestRestException) {
            Log.d("SUPABASE_ERROR", "${e.message}")
            val errorMessage = "A database error occurred"
            ApiResult.Error(errorMessage)
        } catch (e: NoSuchElementException) {
            ApiResult.Error("Not found")
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