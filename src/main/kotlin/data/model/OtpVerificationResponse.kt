package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OtpVerificationResponse(
    val otpVerified: Boolean,
    val userExists: Boolean,
    val existingPhone: String?,
    val tokenPair: TokenPair?
)
