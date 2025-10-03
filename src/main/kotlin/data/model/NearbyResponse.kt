package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class NearbyResponse(
    val candidates: List<NearbyCandidateDto>
)
