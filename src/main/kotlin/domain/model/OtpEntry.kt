package com.example.domain.model

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class OtpEntry(
    val id: String = ObjectId().toHexString(),
    val target: String,        // phone or email
    val otp: String,
    val expiresAt: Long,
)