package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val legs: List<Leg>
)
