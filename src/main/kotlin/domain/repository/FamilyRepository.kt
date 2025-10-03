package com.example.domain.repository

import com.example.data.model.UpdateFamilyMemberDto
import com.example.domain.model.FamilyMember

interface FamilyRepository {
    suspend fun create(profile: FamilyMember): FamilyMember?
    suspend fun findByWandererId(wandererId: String): List<FamilyMember>
    suspend fun update(profile: UpdateFamilyMemberDto): Boolean
}