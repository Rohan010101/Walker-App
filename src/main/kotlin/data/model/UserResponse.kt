package com.example.data.model

import com.example.utils.Gender
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val username: String,
    val name: String,
    val phone: String,
    val email: String?,
    val profilePicKey: String?,
    val profilePicVersion: Long? = null,
    val dob: String,
    val gender: Gender,
    val roles: List<String> = emptyList(),
    val createdAt: Long
)
