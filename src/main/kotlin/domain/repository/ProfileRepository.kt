package com.example.domain.repository

import com.example.data.model.UpdateWalkerProfileDto
import com.example.data.model.UpdateWandererProfileDto
import com.example.domain.model.Walker
import com.example.domain.model.Wanderer

interface ProfileRepository {
    suspend fun createWandererProfile(profile: Wanderer): Boolean
    suspend fun createWalkerProfile(profile: Walker): Boolean
    suspend fun getWandererProfile(userId: String): Wanderer?
    suspend fun getWalkerProfile(userId: String): Walker?
    suspend fun updateWandererProfile(profile: UpdateWandererProfileDto): Boolean
    suspend fun updateWalkerProfile(profile: UpdateWalkerProfileDto): Boolean
}