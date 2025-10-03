package com.example.utils

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

suspend fun ApplicationCall.userIdOrRespond(): String? {
    val principal = principal<JWTPrincipal>()
    val userId = principal?.payload?.getClaim("userId")?.asString()

    if (userId.isNullOrBlank()) {
        respond(HttpStatusCode.Unauthorized, ErrorResponse("Missing/Invalid UserId/Token"))
        return null
    }
    return userId
}