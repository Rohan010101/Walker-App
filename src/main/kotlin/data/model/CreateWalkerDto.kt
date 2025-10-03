package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateWalkerDto(
    val bio: String? = null,
    val isAvailable: Boolean,
    val serviceRadiusKm: Int = 2,
    val bankAccount: String? = null,
    val languages: List<String> = emptyList(),
    val specialties: List<String> = emptyList(),
    val isAadharVerified: Boolean
)
