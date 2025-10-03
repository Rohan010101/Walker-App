package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Leg(
    val steps: List<Step> = emptyList(),
    val distance: Double,            // plain number
    val readable_distance: String,
    val duration: Double,            // plain number
    val readable_duration: String,
    val start_location: Location,
    val end_location: Location,
    val start_address: String,
    val end_address: String
)


//val distance: Distance,
//val duration: Duration
