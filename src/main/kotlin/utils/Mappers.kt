package com.example.utils

import com.example.data.model.UserResponse
import com.example.domain.model.User

// Convert User (DB entity) into detailed response (for profile, settings, chat usage, etc.)
fun User.toUserResponse() = UserResponse(
    id = this.id.toHexString(),
    username = this.username,
    name = this.name,
    email = this.email,
    phone = this.phone,
    profilePicKey = this.profilePicKey,
    profilePicVersion = this.profilePicVersion,
    roles = this.roles.map { it.name },
    createdAt = this.createdAt
)

