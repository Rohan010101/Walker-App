package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OtpVerificationRequest(
    val phone: String,
    val otp: String
)
