package com.example.data.repository

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.*
import com.example.domain.model.User
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.OtpRepository
import com.example.domain.repository.RefreshTokenRepository
import com.example.domain.repository.UserRepository
import com.example.logger
import com.example.security.hashing.PasswordHasher
import com.example.security.token.JwtConfig
import com.example.security.token.TokenService
import com.example.utils.AuthResult
import io.ktor.http.*
import org.bson.types.ObjectId
import java.security.MessageDigest
import java.time.Instant
import java.util.*

class AuthRepositoryImpl(
    private val userRepository: UserRepository,
    private val otpRepository: OtpRepository, // NEW
    private val refreshTokens: RefreshTokenRepository,
    private val hasher: PasswordHasher,
    private val tokenService: TokenService,
    private val jwtConfig: JwtConfig
): AuthRepository {

    private val usernameRegex = Regex("^(?!.*[_.]{2})(?![_.])[a-zA-Z0-9._]{3,20}(?<![_.])$")
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val phoneRegex = Regex("^\\d{10,15}$")


    // OTP REQUEST
    override suspend fun requestOtp(request: OtpRequest): AuthResult<Map<String, String>> {
        val phone = request.phone.trim()

        if (!phone.matches(phoneRegex)) {
            return AuthResult.Error(HttpStatusCode.BadRequest, "Invalid phone number")
        }

        // generate OTP
        val otp = (100000..999999).random().toString()
//        otpRepository.saveOtp(phone, otp, ttlMs = 5 * 60 * 1000) // 5 min expiry

        // send OTP via SMS/Email (adapter pattern)
        otpRepository.deliverOtp(phone, otp)
//        otpRepository.deliverTestOtp(phone, otp)

        return AuthResult.Success(mapOf("message" to "OTP sent successfully"))
    }


    // VERIFY OTP
    override suspend fun verifyOtp(request: OtpVerificationRequest): AuthResult<OtpVerificationResponse> {

        val phone = request.phone.trim()
        val otp = request.otp.trim()
        logger().info("1")
        // verify OTP
        val result = otpRepository.verifyOtp(phone, otp)
        if (!result) {
            logger().warn("Failed OTP verification for phone=$phone")
            return AuthResult.Error(HttpStatusCode.Unauthorized, "Invalid or expired OTP")
        }

        logger().info("2")
        val user = userRepository.getUserByPhone(phone)
        logger().info("success")
        if (user == null) {
            logger().info("3")
            val response = OtpVerificationResponse(
                otpVerified = true,
                userExists = false,
                existingPhone = null,
                tokenPair = null
            )
            return AuthResult.Success(response)
        }
        else {
            logger().info("4")
            val familyId = UUID.randomUUID().toString()
            val jti = UUID.randomUUID().toString()
            val refresh = tokenService.generateRefreshToken(user.id.toHexString(), jti, familyId)
            val rtHash = sha256(refresh)

            refreshTokens.insert(
                RefreshToken(
                    jti = jti,
                    familyId = familyId,
                    userId = user.id.toHexString(),
                    tokenHash = rtHash,
                    expiresAt = Instant.now().toEpochMilli() + jwtConfig.refreshExpiresMs
                )
            )

            logger().info("5")
            val access = tokenService.generateAccessToken(user)
            val tokenPair = TokenPair(access, refresh)

            // Direct Login
            val response = OtpVerificationResponse(
                otpVerified = true,
                userExists = true,
                existingPhone = user.phone,
                tokenPair = tokenPair
            )
            logger().info("6")
            return AuthResult.Success(response)
        }

    }

    // SIGNUP USER
    override suspend fun signUpUser(request: SignUpRequest): AuthResult<TokenPair> {

        val name = request.name.trim()
        val username = request.username.trim()
        val phone = request.phone.trim()
        val email = request.email.trim()
//        val password = request.password.trim()
        val dob = request.dob.trim()
        val gender = request.gender

        // username validation
        if (!username.matches(usernameRegex)) {
            return AuthResult.Error(HttpStatusCode.BadRequest, "Invalid username format")
        }
        if (!phone.matches(phoneRegex)) {
            return AuthResult.Error(HttpStatusCode.BadRequest, "Invalid phone number")
        }
        if (email.isNotBlank() && !email.matches(emailRegex)) {
            return AuthResult.Error(HttpStatusCode.BadRequest, "Invalid email format")
        }


        // uniqueness checks
        if (userRepository.isUsernameTaken(username)) {
            return AuthResult.Error(HttpStatusCode.Conflict, "Username already taken")
        }
        /*TODO: NO NEED*/
        if (userRepository.isPhoneTaken(phone)) {
            return AuthResult.Error(HttpStatusCode.Conflict, "Phone already registered")
        }
        if (email.isNotBlank() && userRepository.isEmailTaken(email)) {
            return AuthResult.Error(HttpStatusCode.Conflict, "Email already registered")
        }




        val user = userRepository.createUser(
            User(
                id = ObjectId(),
                name = name,
                username = username,
                email = email,
                phone = phone,
//                passwordHash = hasher.hash(password),
                dob = dob,
                gender = gender
            )
        ) ?: return AuthResult.Error(HttpStatusCode.InternalServerError, "Failed to create user")

        // token family
        val familyId = UUID.randomUUID().toString()
        val jti = UUID.randomUUID().toString()
        val refresh = tokenService.generateRefreshToken(user.id.toHexString(), jti, familyId)
        val rtHash = sha256(refresh)

        refreshTokens.insert(
            RefreshToken(
                jti = jti,
                familyId = familyId,
                userId = user.id.toHexString(),
                tokenHash = rtHash,
                expiresAt = Instant.now().toEpochMilli() + jwtConfig.refreshExpiresMs
            )
        )

        val access = tokenService.generateAccessToken(user)
        return AuthResult.Success(TokenPair(access, refresh))


    }


    /*TODO: STATE TOKEN FOR LOGIN*/
    // LOGIN USER
    override suspend fun loginUser(request: LoginRequest): AuthResult<TokenPair> {
        val phone = request.phone.trim()

        val user = userRepository.getUserByPhone(phone)
            ?: return AuthResult.Error(HttpStatusCode.BadRequest, "Invalid identifier format")

//        val user = when {
//            identifier.matches(emailRegex) -> userRepository.getUserByEmail(identifier)
//            identifier.matches(phoneRegex) -> userRepository.getUserByPhone(identifier)
//            identifier.matches(usernameRegex) -> userRepository.getUserByUsername(identifier)
//            else -> return AuthResult.Error(HttpStatusCode.BadRequest, "Invalid identifier format")
//        } ?: return AuthResult.Error(HttpStatusCode.Unauthorized, "Invalid credentials")

//        if (!hasher.verify(request.password, user.passwordHash)) {
//            return AuthResult.Error(HttpStatusCode.Unauthorized, "Invalid credentials")
//        }

        val familyId = UUID.randomUUID().toString()
        val jti = UUID.randomUUID().toString()
        val refresh = tokenService.generateRefreshToken(user.id.toHexString(), jti, familyId)
        val rtHash = sha256(refresh)

        refreshTokens.insert(
            RefreshToken(
                jti = jti,
                familyId = familyId,
                userId = user.id.toHexString(),
                tokenHash = rtHash,
                expiresAt = Instant.now().toEpochMilli() + jwtConfig.refreshExpiresMs
            )
        )

        val access = tokenService.generateAccessToken(user)
        return AuthResult.Success(TokenPair(access, refresh))
    }




    // REFRESH TOKEN
    override suspend fun refreshToken(request: RefreshRequest): AuthResult<TokenPair> {
        val encoded = request.refreshToken
        val decoded = try {
            JWT
                .require(Algorithm.HMAC256(jwtConfig.refreshSecret))
                .withIssuer(jwtConfig.issuer)
                .withAudience(jwtConfig.audience)
                .build()
                .verify(encoded)
        } catch (e: Exception) {
            return AuthResult.Error(HttpStatusCode.Unauthorized, "Invalid refresh token")
        }

        val userId = decoded.getClaim("userId").asString() ?: return AuthResult.Error(HttpStatusCode.Unauthorized, "Invalid refresh token")
        val jti = decoded.id ?: return AuthResult.Error(HttpStatusCode.Unauthorized, "Invalid refresh token")
        val familyId = decoded.getClaim("familyId").asString() ?: ""


        val rt = refreshTokens.findByJti(jti)
        if (rt == null) {
            // CHANGE (#5): log suspected reuse; revoke entire family
            logger().warn("Refresh token reuse/unknown detected for familyId=$familyId, jti=$jti, userId=$userId")
            if (familyId.isNotBlank()) refreshTokens.revokeFamily(familyId)
            return AuthResult.Error(HttpStatusCode.Unauthorized, "Refresh reuse detected")
        }

        // CHANGE (#4): verify stored hash matches the incoming token to detect tampering/reuse
        val incomingHash = sha256(encoded)
        if (rt.tokenHash != incomingHash) {
            logger().warn("Refresh token hash mismatch (possible reuse) for familyId=${rt.familyId}, jti=${rt.jti}, userId=${rt.userId}")
            refreshTokens.revokeFamily(rt.familyId)
            return AuthResult.Error(HttpStatusCode.Unauthorized, "Refresh reuse detected")
        }

        val now = Instant.now().toEpochMilli()
        if (rt.revoked || rt.expiresAt < now) {
            // CHANGE: add clear path for revoked/expired refresh; revoke family
            logger().info("Refresh token expired or revoked for familyId=${rt.familyId}, jti=${rt.jti}")
            refreshTokens.revokeFamily(rt.familyId)
            return AuthResult.Error(HttpStatusCode.Unauthorized, "Refresh expired or revoked")
        }
//        val rt = refreshTokens.findByJti(jti)
//            ?: return AuthResult.Error(HttpStatusCode.Unauthorized, "Refresh reuse detected").also {
//                if (familyId.isNotBlank()) refreshTokens.revokeFamily(familyId)
//            }
//
//        if (rt.revoked || rt.expiresAt < Instant.now().toEpochMilli()) {
//            refreshTokens.revokeFamily(familyId)
//            return AuthResult.Error(HttpStatusCode.Unauthorized, "Refresh expired or revoked")
//        }


        // ROTATION
        val newJti = UUID.randomUUID().toString()
        val newRefresh = tokenService.generateRefreshToken(userId, newJti, rt.familyId)
        val newHash = sha256(newRefresh)

        refreshTokens.revokeJti(jti, replacedByJti = newJti)
        refreshTokens.insert(
            RefreshToken(
                jti = newJti,
                familyId = rt.familyId,
                userId = userId,
                tokenHash = newHash,
                expiresAt = Instant.now().toEpochMilli() + jwtConfig.refreshExpiresMs
            )
        )

        val user = userRepository.getUserByUserId(userId)
            ?: return AuthResult.Error(HttpStatusCode.Unauthorized, "User no longer exists")

        val access = tokenService.generateAccessToken(user)
        return AuthResult.Success(TokenPair(access, newRefresh))
    }



    // LOGOUT
    override suspend fun logout(request: LogoutRequest): AuthResult<Unit> {
        val encoded = request.refreshToken
        val decoded = try {
            JWT
                .require(Algorithm.HMAC256(jwtConfig.refreshSecret))
                .withIssuer(jwtConfig.issuer)
                .withAudience(jwtConfig.audience)
                .build()
                .verify(encoded)
        } catch (e: Exception) {
            logger().info("Logout called with invalid refresh token (idempotent success)")
            return AuthResult.Success(Unit) // idempotent
        }

        val familyId = decoded.getClaim("familyId").asString()
        if (!familyId.isNullOrBlank()) {
            logger().info("Revoking refresh token familyId=$familyId (logout)")
            refreshTokens.revokeFamily(familyId)
        }
        return AuthResult.Success(Unit)
    }





    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(text.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}