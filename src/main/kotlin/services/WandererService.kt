package com.example.services

import com.example.data.model.CreateWandererDto
import com.example.data.model.UpdateWandererProfileDto
import com.example.data.model.WandererDto
import com.example.domain.repository.WandererRepository
import com.example.utils.ProfileUpdateResult
import com.example.utils.toDomain
import com.example.utils.toDto

class WandererService(
    private val repository: WandererRepository
) {
    suspend fun createProfile(userId: String, dto: CreateWandererDto): WandererDto? {
        val profile = dto.toDomain(userId)
        return repository.create(profile)?.toDto()
    }

    suspend fun getProfile(userId: String): WandererDto? =
        repository.findByUserId(userId)?.toDto()

    suspend fun updateProfile(userId: String, dto: UpdateWandererProfileDto): ProfileUpdateResult {
        val wanderer = repository.findByUserId(userId) ?: return ProfileUpdateResult.NotFound

        return if (wanderer.id.toHexString() != dto.wandererId) {
            ProfileUpdateResult.Forbidden
        } else {
            val updated = repository.update(dto)
            if (updated) ProfileUpdateResult.Success
            else ProfileUpdateResult.Failed
        }
    }
}