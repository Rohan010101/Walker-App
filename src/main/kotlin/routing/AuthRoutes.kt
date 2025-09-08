package com.example.routing

import com.example.data.model.LoginRequest
import com.example.data.model.LogoutRequest
import com.example.data.model.RefreshRequest
import com.example.data.model.SignUpRequest
import com.example.domain.repository.AuthRepository
import com.example.utils.AuthResult
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    authRepository: AuthRepository,
) {
    route("/auth") {

        // SIGN UP
        post("/signup") {
            val request = call.receive<SignUpRequest>()
            when (val result = authRepository.signupUser(request)) {
                is AuthResult.Success -> call.respond(HttpStatusCode.Created, result.data)
                is AuthResult.Error -> call.respond(result.status, mapOf("error" to result.message))
            }
        }


        // LOGIN
        post("/login") {
            val request = call.receive<LoginRequest>()
            when (val result = authRepository.loginUser(request)) {
                is AuthResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is AuthResult.Error -> call.respond(result.status, mapOf("error" to result.message))
            }

        }

        // REFRESH (delegated)
        post("/refresh") {
            val request = call.receive<RefreshRequest>()
            when (val result = authRepository.refreshToken(request)) {
                is AuthResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is AuthResult.Error -> call.respond(result.status, mapOf("error" to result.message))
            }
        }

        // LOGOUT (delegated)
        post("/logout") {
            val request = call.receive<LogoutRequest>()
            when (val result = authRepository.logout(request)) {
                is AuthResult.Success -> call.respond(HttpStatusCode.NoContent)
                is AuthResult.Error -> call.respond(result.status, mapOf("error" to result.message))
            }
        }
    }

}

