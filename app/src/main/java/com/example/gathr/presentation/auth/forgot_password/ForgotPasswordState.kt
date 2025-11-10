package com.example.gathr.presentation.auth.forgot_password

data class ForgotPasswordState(
    val email: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
)