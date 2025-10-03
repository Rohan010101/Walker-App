package com.example.domain.repository

import com.example.data.model.UpdateWalkerProfileDto
import com.example.domain.model.Walker

interface WalkerRepository {
    suspend fun create(profile: Walker): Walker?
    suspend fun findByUserId(userId: String): Walker?
    suspend fun update(profile: UpdateWalkerProfileDto): Boolean
}