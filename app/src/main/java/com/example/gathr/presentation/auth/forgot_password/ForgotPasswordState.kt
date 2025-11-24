package com.example.gathr.presentation.auth.forgot_password

import com.example.gathr.presentation.auth.PasswordValidationState

data class ForgotPasswordState(
    val email: String = "",
    val emailCode: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val isLoading: Boolean = false,

    val emailError: String = "",
    val emailCodeError: String = "",
    val confirmNewPasswordError: String = "",
    val passwordValidation: PasswordValidationState = PasswordValidationState(),
)