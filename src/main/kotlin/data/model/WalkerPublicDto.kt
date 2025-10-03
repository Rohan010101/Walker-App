package com.example.data.model

import com.example.utils.Gender
import kotlinx.serialization.Serializable

@Serializable
data class WalkerPublicDto(
    val walkerId: String,
    val name: String,
    val phone: String,
    val gender: Gender,
    val bio: String?,
    val rating: Double,
    val completedWalks: Int,
    val languages: List<String>,
    val specialties: List<String>,
    val isAadharVerified: Boolean = false,
    val serviceRadiusKm: Int
)
