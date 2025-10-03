package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OtpRequest (
    val phone: String
)