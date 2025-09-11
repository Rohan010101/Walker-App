package com.example.domain.repository

import com.example.data.model.*
import com.example.utils.AuthResult

interface AuthRepository {
    suspend fun requestOtp(request: SignUpRequest): AuthResult<Unit>
    suspend fun requestTestOtp(request: SignUpRequest): AuthResult<TestOtpResponse>
    suspend fun signUpUser(request: SignUpRequest): AuthResult<TokenPair>       // send OTP, return "OTP sent"
    suspend fun loginUser(request: LoginRequest): AuthResult<TokenPair>
    suspend fun refreshToken(request: RefreshRequest): AuthResult<TokenPair>
    suspend fun logout(request: LogoutRequest): AuthResult<Unit>
}