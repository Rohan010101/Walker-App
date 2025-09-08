package com.example.security.hashing

import org.mindrot.jbcrypt.BCrypt

class BcryptHasher : PasswordHasher {

    // Hashing
    override fun hash(password: String): String =
        BCrypt.hashpw(password, BCrypt.gensalt())

    // Verify
    override fun verify(password: String, passwordHash: String): Boolean =
        BCrypt.checkpw(password, passwordHash)
}