package com.example.data.model

import com.example.utils.Gender
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWandererProfileDto(
    val wandererId: String,
    val genderPreference: Gender? = null,
    val medicalInfo: String? = null,
    val medicalConditions: List<String> = emptyList(),
    val hasPet: Boolean = false,
    val pace: String? = null, // slow, medium, fast
    val languages: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val topicsOfConversation: List<String> = emptyList(),
    val isAadharVerified: Boolean
)
