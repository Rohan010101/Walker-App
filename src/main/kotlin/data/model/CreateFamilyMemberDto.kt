package com.example.data.model

import com.example.utils.Gender
import kotlinx.serialization.Serializable

@Serializable
data class CreateFamilyMemberDto(
    val wandererId: String,
    val name: String,
    val phone: String?,
    val email: String?,
    val profilePicKey: String? = null,
    val profilePicVersion: Long? = null,
    val dob: String,
    val gender: Gender,
    val genderPreference: Gender? = null,
    val medicalInfo: String? = null,
    val medicalConditions: List<String> = emptyList(),
    val hasPet: Boolean = false,
    val pace: String? = null, // slow, medium, fast
    val languages: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val topicsOfConversation: List<String> = emptyList()
)
