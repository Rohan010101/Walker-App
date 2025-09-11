package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
    val otp: String? = null
)
