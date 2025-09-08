package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.codahale.metrics.*
import com.example.security.token.JwtConfig
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

fun Application.configureSecurity(jwtConfig: JwtConfig) {
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }


    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = jwtConfig.audience
    val jwtDomain = jwtConfig.issuer
    val jwtRealm = "walker-app"
    val jwtSecret = jwtConfig.accessSecret

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                logger().info("JWT validation - userId: $userId")
                logger().info("JWT validation - audience: ${credential.payload.audience}")
                logger().info("JWT validation - issuer: ${credential.payload.issuer}")

                if (userId != null && credential.payload.audience.contains(jwtConfig.audience)) {
                    // Use DEBUG instead of INFO to avoid spam
                    logger().debug("JWT validated for userId=$userId")
                    JWTPrincipal(credential.payload)
                } else {
                    logger().warn("JWT validation failed: userId=$userId")
                    null
                }
            }
        }
    }
    routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }
}
@Serializable
data class MySession(val count: Int = 0)
