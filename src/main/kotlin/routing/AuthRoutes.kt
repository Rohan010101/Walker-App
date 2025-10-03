package com.example.routing

import com.example.data.model.*
import com.example.domain.repository.AuthRepository
import com.example.utils.AuthResult
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route

fun Route.authRoutes(
    authRepository: AuthRepository,
) {
    route("/auth") {

        // REQUEST OTP
        post("/request-otp") {
            val request = call.receive<OtpRequest>()
            when (val result = authRepository.requestOtp(request)) {
                is AuthResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is AuthResult.Error -> call.respond(result.status, mapOf("error" to result.message))
            }
        }

        // VERIFY OTP
        post("/verify-otp") {
            val request = call.receive<OtpVerificationRequest>()
            when (val result = authRepository.verifyOtp(request)) {
                is AuthResult.Success -> call.respond(HttpStatusCode.OK, result.data)
                is AuthResult.Error -> call.respond(result.status, mapOf("error" to result.message))
            }
        }


        // SIGN UP
        post("/signup") {
            val request = call.receive<SignUpRequest>()
            when (val result = authRepository.signUpUser(request)) {
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

