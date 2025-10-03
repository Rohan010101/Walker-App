package com.example.domain.repository

import com.example.data.model.UpdateUserDto
import com.example.domain.model.User

interface UserRepository {
    suspend fun ensureIndexes()

    suspend fun createUser(user: User): User?

    suspend fun getUserByUsername(username: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByPhone(phone: String): User?

    suspend fun getUserByUserId(userId: String): User?
    suspend fun getUserByWalkerId(walkerId: String): User?
    suspend fun getUserByWandererId(wandererId: String): User?

    suspend fun updateUserProfile(userId: String,updatedUser: UpdateUserDto): Boolean
    suspend fun updateProfilePic(userId: String, key: String): Boolean
    suspend fun deleteProfilePic(userId: String): Boolean

    suspend fun getAllUsers(): List<User>

    suspend fun isEmailTaken(email: String): Boolean
    suspend fun isUsernameTaken(username: String): Boolean
    suspend fun isPhoneTaken(phone: String): Boolean

}