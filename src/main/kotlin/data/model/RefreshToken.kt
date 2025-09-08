package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class RefreshToken(
    @BsonId
    val id: String = ObjectId().toHexString(),
    val jti: String,                 // unique token id (UUID)
    val familyId: String,            // one family per login session
    val userId: String,
    val tokenHash: String,           // store hash of refresh token (never plaintext)
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long,
    val revoked: Boolean = false,
    val replacedByJti: String? = null
)
