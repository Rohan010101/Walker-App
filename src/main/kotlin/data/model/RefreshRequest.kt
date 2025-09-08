package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RefreshRequest(val refreshToken: String)
