package com.example.gathr.utils

import com.example.gathr.data.remote.ApiResult

fun <T> ApiResult<T>.getOrThrow(): T {
    return when (this) {
        is ApiResult.Success -> this.data
        is ApiResult.Error -> throw RuntimeException(this.message)
    }
}
