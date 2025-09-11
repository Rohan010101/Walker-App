package com.example.data.model

sealed class OtpResult {
    data class Success(val messageSid: String, val status: String, val response: String) : OtpResult()
    data class Error(val message: String, val errorCode: Int? = null) : OtpResult()
}
