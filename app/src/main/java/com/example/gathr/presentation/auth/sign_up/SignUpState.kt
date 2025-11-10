package com.example.gathr.presentation.auth.sign_up

data class SignUpState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isUmak: Boolean? = null,
    val department: String = "",
    val school: String = "",
    val isAlumni: Boolean? = null,
    val emailCode: String = "",

    val firstNameError: String = "",
    val lastNameError: String = "",
    val emailError: String = "",
    val emailCodeError: String = "",
    val usernameError: String = "",
    val passwordValidation: PasswordValidationState = PasswordValidationState(),
)

data class PasswordValidationState(
    val hasMinLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasDigit: Boolean = false,
    val hasSpecialChar: Boolean = false,
    val hasValidationErrors: Boolean = false,
    val hasConfirmPasswordError: Boolean = false
)