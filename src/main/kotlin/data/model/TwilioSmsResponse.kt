package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TwilioSmsResponse(
    val sid: String? = null,
    val accountSid: String? = null,
    val from: String? = null,
    val to: String? = null,
    val body: String? = null,
    val status: String? = null,
    val errorCode: Int? = null,
    val errorMessage: String? = null,
    val dateCreated: String? = null,
    val dateUpdated: String? = null,
    val dateSent: String? = null,
    val uri: String? = null
)
