package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val id: String,
    val username: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val createdAt: Long
)
