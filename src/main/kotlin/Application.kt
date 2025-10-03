package com.example

import aws.sdk.kotlin.services.s3.S3Client
import com.example.data.repository.*
import com.example.security.hashing.BcryptHasher
import com.example.security.token.JwtConfig
import com.example.security.token.JwtTokenService
import com.example.services.*
import com.example.utils.Constants.ACCOUNT_SID
import com.example.utils.Constants.AUTH_TOKEN
import com.example.utils.Constants.SERVICE_SID
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.server.application.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    val httpClient = HttpClient(CIO) { /* configure once */ }
    val s3Client = runBlocking {
        S3Client.fromEnvironment { }        // ✅ shared instance
    }

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

    val otpService = OtpService(ACCOUNT_SID,AUTH_TOKEN, SERVICE_SID)

    // REPOSITORIES
    // ✅ Repositories
    val userRepository = UserRepositoryImpl(db)
    val otpRepository = OtpRepositoryImpl(db, otpService)
    val availabilityRepository = AvailabilityRepositoryImpl(db)

    val walkerRepository = WalkerRepositoryImpl(db)
    val wandererRepository = WandererRepositoryImpl(db)
    val familyRepository = FamilyRepositoryImpl(db)


    val routingService = RoutingService(httpClient)
    val matchingService = MatchingService(availabilityRepository, routingService)

    val walkerService = WalkerService(walkerRepository)
    val wandererService = WandererService(wandererRepository)
    val familyService = FamilyService(familyRepository, wandererRepository)

    // ✅ Ensure indexes at startup
    launch {
        userRepository.ensureIndexes()
        availabilityRepository.ensureIndexes()
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
    configureRouting(authRepository, userRepository, walkerRepository, availabilityRepository, matchingService, walkerService, wandererService, familyService, s3Client, httpClient)
}
