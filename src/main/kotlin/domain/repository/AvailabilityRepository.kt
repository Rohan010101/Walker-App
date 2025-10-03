package com.example.domain.repository

import com.example.data.model.WalkerAvailability

interface AvailabilityRepository {
    suspend fun ensureIndexes()

    suspend fun upsertAvailability(av: WalkerAvailability): Boolean
    suspend fun getAvailabilityByUser(userId: String): WalkerAvailability?
    suspend fun removeAvailability(userId: String): Boolean
    suspend fun findNearby(lng: Double, lat: Double, maxDistanceMeters: Int, limit: Int = 50): List<WalkerAvailability>
}