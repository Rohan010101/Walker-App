package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateWalkerProfileDto(
    val walkerId: String,
    val bio: String? = null,
    val isAvailable: Boolean,
    val serviceRadiusKm: Int,     // Default Radius
    val bankAccount: String?,
    val rating: Double,
    val completedWalks: Int,
    val languages: List<String> = emptyList(),
    val specialties: List<String> = emptyList(),
    val isAadharVerified: Boolean
)
