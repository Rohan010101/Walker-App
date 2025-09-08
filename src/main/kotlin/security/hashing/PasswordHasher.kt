package com.example.security.hashing

interface PasswordHasher {
    fun hash(password: String): String
    fun verify(password: String, passwordHash: String): Boolean
}