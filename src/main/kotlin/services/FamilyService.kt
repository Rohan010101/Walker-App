package com.example.services

import com.example.data.model.CreateFamilyMemberDto
import com.example.data.model.FamilyMemberDto
import com.example.data.model.UpdateFamilyMemberDto
import com.example.domain.repository.FamilyRepository
import com.example.domain.repository.WandererRepository
import com.example.utils.ProfileUpdateResult
import com.example.utils.toDomain
import com.example.utils.toDto

class FamilyService(
    private val familyRepository: FamilyRepository,
    private val wandererRepository: WandererRepository
) {

    suspend fun addFamilyMember(userId: String, dto: CreateFamilyMemberDto): FamilyMemberDto? {
        val wanderer = wandererRepository.findByUserId(userId) ?: return null
        val member = dto.toDomain(wanderer.id.toHexString())
        return familyRepository.create(member)?.toDto()
    }

    suspend fun updateFamilyMember(userId: String, dto: UpdateFamilyMemberDto): ProfileUpdateResult {
        val wanderer = wandererRepository.findByUserId(userId) ?: return ProfileUpdateResult.NotFound

        return if (wanderer.id.toHexString() != dto.wandererId) {
            ProfileUpdateResult.Forbidden
        } else {
            val updated = familyRepository.update(dto)
            if (updated) ProfileUpdateResult.Success
            else ProfileUpdateResult.Failed
        }
    }


    suspend fun listFamily(userId: String): List<FamilyMemberDto> {
        val wanderer = wandererRepository.findByUserId(userId) ?: return emptyList()
        return familyRepository.findByWandererId(wanderer.id.toHexString()).map { it.toDto() }
    }

}