package com.example.domain.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Walker(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: ObjectId,       // User
    val bio: String? = null,
    val isAvailable: Boolean,
    val serviceRadiusKm: Int = 2,     // Default Radius
    val bankAccount: String? = null,
    val rating: Double = 0.0,
    val completedWalks: Int = 0,
    val languages: List<String> = emptyList(),
    val specialties: List<String> = emptyList(),
    val isAadharVerified: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
