package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DirectionResponse(
    val status: String,
    val routes: List<Route>
)
