package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class WalkerAvailability(
    @BsonId
    val id: String = ObjectId().toHexString(),
    val walkerId: String,
    val location: GeoPoint,
    val isAvailable: Boolean = true,
    val serviceRadiusKm: Int = 5,
    val updatedAt: Long = System.currentTimeMillis()
)