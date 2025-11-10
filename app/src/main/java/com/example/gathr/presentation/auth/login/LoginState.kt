package com.example.gathr.presentation.auth.login

data class LoginState(
    val email: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
)