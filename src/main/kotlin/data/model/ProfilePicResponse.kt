package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProfilePicResponse(
    val profilePicUrl: String?,
    val profilePicVersion: Long?
)
