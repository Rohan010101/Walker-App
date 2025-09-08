package com.example.domain.repository

import com.example.data.model.RefreshToken

interface RefreshTokenRepository {
    suspend fun ensureIndexes()
    suspend fun insert(rt: RefreshToken): Boolean
    suspend fun findByJti(jti: String): RefreshToken?
    suspend fun revokeJti(jti: String, replacedByJti: String? = null): Boolean
    suspend fun revokeFamily(familyId: String): Boolean
}