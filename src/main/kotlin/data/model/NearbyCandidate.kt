package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NearbyCandidateDto(
    val userId: String,
    val lng: Double,
    val lat: Double,
    val distanceMeters: Double? = null,
    val durationSeconds: Double? = null,
    val serviceRadiusKm: Int,
    val score: Double
)
