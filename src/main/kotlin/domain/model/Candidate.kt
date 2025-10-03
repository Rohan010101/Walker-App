package com.example.domain.model

import com.example.data.model.WalkerAvailability

data class Candidate(
    val availability: WalkerAvailability,
    val distanceMetersFromRequester: Double?,
    val durationSeconds: Double?,
    val score: Double
)
