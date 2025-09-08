package com.example.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.domain.model.User
import java.util.*

class JwtTokenService(
    private val config: JwtConfig
): TokenService {

    override fun generateAccessToken(user: User): String {
        val algorithm = Algorithm.HMAC256(config.accessSecret)
        return JWT.create()
            .withKeyId(config.accessSecret)
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withClaim("userId", user.id.toHexString())
            .withClaim("username", user.username)
            .withExpiresAt(Date(System.currentTimeMillis() + config.accessExpiresMs))
            .sign(algorithm)
    }

    override fun generateRefreshToken(userId: String, jti: String, familyId: String): String {
        val algorithm = Algorithm.HMAC256(config.refreshSecret)
        return JWT.create()
            .withKeyId(config.refreshSecret)
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withJWTId(jti)
            .withClaim("userId", userId)
            .withClaim("familyId", familyId)
            .withExpiresAt(Date(System.currentTimeMillis() + config.refreshExpiresMs))
            .sign(algorithm)
    }

    override fun decodeAccessUserId(token: String): String? = try {
        val verifier = JWT
            .require(Algorithm.HMAC256(config.accessSecret))
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()
        val decoded = verifier.verify(token)
        decoded.getClaim("userId").asString()
    } catch (e: Exception) {
        null
    }
}