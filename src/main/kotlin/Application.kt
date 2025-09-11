package com.example

import com.example.data.repository.AuthRepositoryImpl
import com.example.data.repository.OtpRepositoryImpl
import com.example.data.repository.RefreshTokenRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.domain.repository.OtpRepository
import com.example.security.hashing.BcryptHasher
import com.example.security.token.JwtConfig
import com.example.security.token.JwtTokenService
import com.example.services.OtpService
import com.example.utils.Constants.OTP_API_KEY
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val mongoPw = System.getenv("MONGO_PW")
    val dbName = "Walker-App"
    val db = KMongo.createClient(
        connectionString = "mongodb+srv://rohansinghrawat05:$mongoPw@cluster0.zdqfp8f.mongodb.net/$dbName?retryWrites=true&w=majority&appName=Cluster0"
    ).coroutine
        .getDatabase(dbName)
    logger().info("Database Connected")


    val jwtConfig = JwtConfig(
        issuer = "ktor.io",
        audience = "jwt-audience",
        accessExpiresMs = 15 * 60 * 1000L,     // 15 min
        refreshExpiresMs = 14L * 24 * 60 * 60 * 1000, // 14 days
        accessSecret = System.getenv("JWT_ACCESS_SECRET") ?: "dev-access-secret",
        refreshSecret = System.getenv("JWT_REFRESH_SECRET") ?: "dev-refresh-secret"
    )


    val tokenService = JwtTokenService(jwtConfig)
    val passwordHasher = BcryptHasher()

    val otpService = OtpService(OTP_API_KEY)

    // REPOSITORIES
    // ✅ Repositories
    val userRepository = UserRepositoryImpl(db)
    val otpRepository = OtpRepositoryImpl(db, otpService)


    // ✅ Ensure indexes at startup
    launch {
        userRepository.ensureIndexes()
    }
    val refreshTokenRepository = RefreshTokenRepositoryImpl(db)


    // ✅ Auth repository depends on User + RefreshToken repo
    val authRepository = AuthRepositoryImpl(
        userRepository = userRepository,
        otpRepository = otpRepository,
        refreshTokens = refreshTokenRepository,
        tokenService = tokenService,
        hasher = passwordHasher,
        jwtConfig = jwtConfig
    )


    configureMonitoring()
    configureHTTP()
    configureSerialization()
    configureSecurity(jwtConfig)
    configureSockets()
    configureRouting(authRepository, userRepository)
}
