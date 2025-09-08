package com.example.security.token

import com.example.domain.model.User

interface TokenService {
    fun generateAccessToken(user: User): String
    fun generateRefreshToken(userId: String, jti: String, familyId: String): String
    fun decodeAccessUserId(token: String): String?
}