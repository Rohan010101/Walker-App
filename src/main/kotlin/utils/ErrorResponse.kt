package com.example.utils

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String
)
