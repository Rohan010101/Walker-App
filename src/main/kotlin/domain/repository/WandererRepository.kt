package com.example.domain.repository

import com.example.data.model.UpdateWandererProfileDto
import com.example.domain.model.Wanderer

interface WandererRepository {
    suspend fun create(profile: Wanderer): Wanderer?
    suspend fun findByUserId(userId: String): Wanderer?
    suspend fun findByWandererId(wandererId: String): Wanderer?
    suspend fun update(profile: UpdateWandererProfileDto): Boolean
}