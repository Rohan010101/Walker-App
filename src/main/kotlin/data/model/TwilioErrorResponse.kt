package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TwilioErrorResponse(
    val code: Int? = null,
    val message: String? = null,
    val moreInfo: String? = null,
    val status: Int? = null
)
