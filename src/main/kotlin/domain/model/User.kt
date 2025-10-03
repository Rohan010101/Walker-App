package com.example.domain.model

import com.example.utils.Gender
import com.example.utils.UserRole
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val username: String,
    val name: String,
    val phone: String,
    val email: String? = null,
    val profilePicKey: String? = null,
    val profilePicVersion: Long? = null,
//    val passwordHash: String,
    val dob: String,
    val gender: Gender,
    val roles: List<UserRole> = listOf(UserRole.WANDERER), // default as consumer
    val isAadharVerified: Boolean = false,
    val createdAt : Long = System.currentTimeMillis()
)
