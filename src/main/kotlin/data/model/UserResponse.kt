package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val profilePicKey: String?,
    val profilePicVersion: Long? = null,
    val roles: List<String> = emptyList(),
    val createdAt: Long
)
