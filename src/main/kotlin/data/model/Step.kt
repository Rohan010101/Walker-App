package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val instructions: String? = null,
    val distance: Double? = null,
    val duration: Double? = null
)
