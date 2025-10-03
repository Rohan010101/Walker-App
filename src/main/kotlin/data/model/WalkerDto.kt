package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WalkerDto(
    val id: String,
    val bio: String? = null,
    val isAvailable: Boolean,
    val serviceRadiusKm: Int = 2,     // Default Radius
    val bankAccount: String? = null,
    val rating: Double = 0.0,
    val completedWalks: Int = 0,
    val languages: List<String> = emptyList(),
    val specialties: List<String> = emptyList(),
    val isAadharVerified: Boolean
)
