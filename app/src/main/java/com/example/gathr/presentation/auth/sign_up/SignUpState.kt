package com.example.gathr.presentation.auth.sign_up

import com.example.gathr.presentation.auth.PasswordValidationState

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
    val isLoading: Boolean = false,
    val signUpError: String = "",

    val emailError: String = "",
    val firstNameError: String = "",
    val lastNameError: String = "",
    val usernameError: String = "",
    val confirmPasswordError: String = "",
    val passwordValidation: PasswordValidationState = PasswordValidationState(),
    val emailCodeError: String = "",
)
