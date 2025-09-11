package com.example.domain.repository

import com.example.domain.model.OtpEntry

interface OtpRepository {
    suspend fun saveOtp(target: String, otp: String, ttlMs: Long)
    suspend fun verifyOtp(target: String, otp: String): Boolean
    suspend fun deliverOtp(target: String, otp: String)
    suspend fun deliverTestOtp(target: String, otp: String): String

}