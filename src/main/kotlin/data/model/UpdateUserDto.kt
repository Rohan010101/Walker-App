package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserDto(
    val name: String?,
)
