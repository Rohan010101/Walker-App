package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LogoutRequest(val refreshToken: String)
