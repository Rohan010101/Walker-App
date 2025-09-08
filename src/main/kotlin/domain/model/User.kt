package com.example.domain.model

import com.example.utils.UserRole
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val username: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val profilePicKey: String? = null,
    val profilePicVersion: Long? = null,
    val passwordHash: String,
    val roles: List<UserRole> = listOf(UserRole.WANDERER), // default as consumer
    val createdAt : Long = System.currentTimeMillis()
)
