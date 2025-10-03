package com.example.services

import com.example.data.model.CreateWalkerDto
import com.example.data.model.UpdateWalkerProfileDto
import com.example.data.model.WalkerDto
import com.example.domain.repository.WalkerRepository
import com.example.utils.ProfileUpdateResult
import com.example.utils.toDomain
import com.example.utils.toDto

class WalkerService(
    private val repository: WalkerRepository
) {

    suspend fun createProfile(userId: String, dto: CreateWalkerDto): WalkerDto? {
        val profile = dto.toDomain(userId)
        return repository.create(profile)?.toDto()
    }

    suspend fun getProfile(userId: String): WalkerDto? =
        repository.findByUserId(userId)?.toDto()

    suspend fun updateProfile(userId: String, dto: UpdateWalkerProfileDto): ProfileUpdateResult {
        val walker = repository.findByUserId(userId) ?: return ProfileUpdateResult.NotFound

        return if (walker.id.toHexString() != dto.walkerId) {
            ProfileUpdateResult.Forbidden
        } else {
            val updated = repository.update(dto)
            if (updated) ProfileUpdateResult.Success
            else ProfileUpdateResult.Failed
        }
    }
}