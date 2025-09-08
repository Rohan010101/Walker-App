package com.example.utils

import io.ktor.http.*

sealed class AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(
        val status: HttpStatusCode,
        val message: String
    ) : AuthResult<Nothing>()
}
