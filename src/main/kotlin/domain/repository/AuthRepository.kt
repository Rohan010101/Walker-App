package com.example.domain.repository

import com.example.data.model.*
import com.example.utils.AuthResult

interface AuthRepository {
    suspend fun signupUser(request: SignUpRequest): AuthResult<TokenPair>
    suspend fun loginUser(request: LoginRequest): AuthResult<TokenPair>
    suspend fun refreshToken(request: RefreshRequest): AuthResult<TokenPair>
    suspend fun logout(request: LogoutRequest): AuthResult<Unit>
}