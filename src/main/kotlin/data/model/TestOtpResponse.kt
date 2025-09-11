package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TestOtpResponse (
    val message: String,
    val otp: String
)