package com.example.security.token

data class JwtConfig(
    val issuer: String,
    val audience: String,
    val accessExpiresMs: Long,
    val refreshExpiresMs: Long,
    val accessSecret: String,
    val refreshSecret: String,
    val accessKid: String = "access-v1",
    val refreshKid: String = "refresh-v1"
)
