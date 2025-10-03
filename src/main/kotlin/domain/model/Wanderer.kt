package com.example.domain.model

import com.example.utils.Gender
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Wanderer(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: ObjectId,       // User
    val genderPreference: Gender? = null,
    val medicalInfo: String? = null,
    val medicalConditions: List<String> = emptyList(),
    val hasPet: Boolean = false,
    val pace: String? = null, // slow, medium, fast
    val languages: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val topicsOfConversation: List<String> = emptyList(),
    val isAadharVerified: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
