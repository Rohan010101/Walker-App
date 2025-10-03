package com.example.data.model

import com.example.utils.Gender
import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val gender: Gender,
    val dob: String,        // store as ISO string (2005-02-31)
//    val password: String
)
