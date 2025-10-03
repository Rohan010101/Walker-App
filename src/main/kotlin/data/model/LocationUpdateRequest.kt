package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class LocationUpdateRequest(
    val walkerId: String,
    val lng: Double,
    val lat: Double,
    val isAvailable: Boolean = true,
    val serviceRadiusKm: Int = 5
)
