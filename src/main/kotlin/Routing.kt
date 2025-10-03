package com.example

import aws.sdk.kotlin.services.s3.S3Client
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.codahale.metrics.*
import com.example.domain.repository.AuthRepository
import com.example.domain.repository.AvailabilityRepository
import com.example.domain.repository.UserRepository
import com.example.domain.repository.WalkerRepository
import com.example.routing.authRoutes
import com.example.routing.geoRoutes
import com.example.routing.profileRoutes
import com.example.routing.userRoutes
import com.example.services.FamilyService
import com.example.services.MatchingService
import com.example.services.WalkerService
import com.example.services.WandererService
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.metrics.dropwizard.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import org.slf4j.event.*

fun Application.configureRouting(
    authRepository: AuthRepository,
    userRepository: UserRepository,
    walkerRepository: WalkerRepository,
    availabilityRepository: AvailabilityRepository,
    matchingService: MatchingService,

    walkerService: WalkerService,
    wandererService: WandererService,
    familyService: FamilyService,

    s3: S3Client,
    client: HttpClient
) {
    routing {
        authRoutes(authRepository)
        userRoutes(s3, client, userRepository)
        profileRoutes(walkerService, wandererService, familyService)
        geoRoutes(walkerRepository, availabilityRepository, matchingService)
    }
}
