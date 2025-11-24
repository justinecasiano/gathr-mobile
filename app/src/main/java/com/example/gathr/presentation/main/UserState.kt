package com.example.gathr.presentation.main

import com.example.gathr.data.model.User

data class UserState(
    val currentUser: User? = null,
    val actionError: String = "",
    val isLoading: Boolean = false
)
