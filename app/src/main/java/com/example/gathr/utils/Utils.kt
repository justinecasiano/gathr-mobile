package com.example.gathr.utils

import com.example.gathr.presentation.auth.sign_up.PasswordValidationState

object Utils {
    fun validateInput(type: String, value: String): String {
        return when (type) {
            "firstname", "lastname" -> {
                if (value.isBlank() || value.length < 2) "This field is required"
                else ""
            }

            "email" -> {
                if (value.isBlank()) "This field is required"
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value)
                        .matches()
                ) "Email is invalid"
//                else if () "Email already exists"
                else ""
            }

            "username" -> {
                val usernameRegex = Regex("^[A-Za-z0-9_]{3,20}$")
                if (value.isBlank()) "This field is required"
                else if (!usernameRegex.matches(value)) "Username must have 3-20 characters, using only letters, numbers, and underscores."
//                else if () "Username already exists"
                else ""
            }

            "isUmak", "department", "school", "isAlumni" -> {
                if (value.isBlank()) "This field is required"
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
}