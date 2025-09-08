package com.example.routing

import com.example.data.model.ProfilePicResponse
import com.example.data.model.UpdateUserDto
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.s3.S3Service
import com.example.utils.toUserResponse
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

fun Route.userRoutes(
    userRepository: UserRepository
) {
    route("/users") {

        // ---------------------------
        // PUBLIC ENDPOINTS
        // ---------------------------

        // CREATE USER
        post("") {
            call.application.log.info("POST /users called")
            val user = try {
                call.receive<User>()
            } catch (e: Exception) {
                call.application.log.error("Invalid request body: ${e.message}", e)
                return@post call.respond(HttpStatusCode.BadRequest, "Invalid user payload")
            }

            val created = userRepository.createUser(user)
                ?: return@post call.respond(HttpStatusCode.InternalServerError, "Failed to create user")

            call.application.log.info("User created with id: ${created.id}")
            call.respond(HttpStatusCode.Created, created.toUserResponse())
        }

        // GET ALL USERS
        get("") {
            call.application.log.info("GET /users called")
            val users = userRepository.getAllUsers().map { it.toUserResponse() }
            call.application.log.info("Fetched ${users.size} users")
            call.respond(HttpStatusCode.OK, users)
        }

        // GET USER BY ID
        get("/id/{userId}") {
            val userId = call.parameters["userId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing userId")

            val user = userRepository.getUserByUserId(userId)?.toUserResponse()
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

            call.application.log.info("User found: ${user.username}")
            call.respond(HttpStatusCode.OK, user)
        }

        // GET USER BY USERNAME
        get("/username/{username}") {
            val username = call.parameters["username"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing username")

            val user = userRepository.getUserByUsername(username)?.toUserResponse()
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

            call.application.log.info("User found by username: $username")
            call.respond(HttpStatusCode.OK, user)
        }

        // GET USER BY EMAIL
        get("/email/{email}") {
            val email = call.parameters["email"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing email")

            val user = userRepository.getUserByEmail(email)?.toUserResponse()
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

            call.application.log.info("User found by email: $email")
            call.respond(HttpStatusCode.OK, user)
        }

        // GET USER BY PHONE
        get("/phone/{phone}") {
            val phone = call.parameters["phone"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing phone")

            val user = userRepository.getUserByPhone(phone)?.toUserResponse()
                ?: return@get call.respond(HttpStatusCode.NotFound, "User not found")

            call.application.log.info("User found by phone: $phone")
            call.respond(HttpStatusCode.OK, user)
        }






        // CHECK IF EMAIL IS TAKEN
        get("/check/email/{email}") {
            val email = call.parameters["email"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing email")

            val taken = userRepository.isEmailTaken(email)
            call.respond(HttpStatusCode.OK, mapOf("emailTaken" to taken))
        }

        // CHECK IF USERNAME IS TAKEN
        get("/check/username/{username}") {
            val username = call.parameters["username"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing username")

            val taken = userRepository.isUsernameTaken(username)
            call.respond(HttpStatusCode.OK, mapOf("usernameTaken" to taken))
        }

        // CHECK IF PHONE IS TAKEN
        get("/check/phone/{phone}") {
            val phone = call.parameters["phone"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing phone")

            val taken = userRepository.isPhoneTaken(phone)
            call.respond(HttpStatusCode.OK, mapOf("phoneTaken" to taken))
        }




        authenticate("auth-jwt") { // Use your JWT authentication config name
            route("/me"){

                // GET CURRENT USER (self)
                get {
                    val principal = call.principal<JWTPrincipal>()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Invalid or missing token")
                        )

                    val userId = principal.payload.getClaim("userId")?.asString()
                        ?: return@get call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Missing userId in token")
                        )

                    val user = userRepository.getUserByUserId(userId)?.toUserResponse()
                        ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))

                    call.application.log.info("Fetched current user: ${user.username}")
                    call.respond(HttpStatusCode.OK, user)
                }


                put {
                    call.application.log.info("put('/users/me') executed")
                    val userDto = call.receive<UpdateUserDto>()
                    call.application.log.info("userDto executed: $userDto")

                    val principal = call.principal<JWTPrincipal>()
                        ?: return@put call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Invalid or missing token")
                        )
                    call.application.log.info("principal executed: $principal")


                    val userId = principal.payload.getClaim("userId")?.asString()
                        ?: return@put call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to "Missing userId in token")
                        )
                    call.application.log.info("userId executed: $userId")



                    val success = userRepository.updateUserProfile(userId, userDto)
                    call.application.log.info("success executed: $success")

                    if (success) call.respondText("Profile updated")
                    else call.respondText("Update failed")
                }



                route("/pfp") {

                    // Upload/Update PFP
                    post {
                        val principal = call.principal<JWTPrincipal>()
                            ?: return@post call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to "Invalid or missing token")
                            )
                        val userId = principal.payload.getClaim("userId").asString()
                            ?: return@post call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to "Missing userId in token")
                            )

                        val multipart = call.receiveMultipart()
                        var fileBytes: ByteArray? = null

                        multipart.forEachPart { part ->
                            if (part is PartData.FileItem) {
                                fileBytes = part.provider().toByteArray()
                            }
                            part.dispose()
                        }

                        if (fileBytes == null) {
                            call.application.log.warn("PFP upload failed: No file uploaded for user $userId")
                            return@post call.respond(HttpStatusCode.BadRequest, "No file uploaded")
                                .also { call.application.log.info("Responded 400: No file uploaded for $userId") }
                        }

                        val s3Service = S3Service()
                        val key = "profilePicture/$userId/$userId.jpg"

                        try {
                            // 1️⃣ Upload to S3
                            s3Service.uploadProfilePic(userId, fileBytes!!)
                            call.application.log.info("S3 upload successful for user $userId, key: $key")

                            // 2️⃣ Update DB
                            val updated = userRepository.updateProfilePic(userId, key)
                            call.application.log.info("updated DB for pfpKey: $updated")
                            if (!updated) {
                                // DB update failed → rollback S3
                                s3Service.deleteProfilePic(userId)
                                call.application.log.error("DB update failed for user $userId, S3 rollback executed")
                                return@post call.respond(
                                    HttpStatusCode.InternalServerError,
                                    "Failed to update user profile"
                                ).also { call.application.log.info("Responded 500: DB update failed for $userId") }
                            }

                            call.application.log.info("Profile picture DB update successful for user $userId")
                            return@post call.respond(
                                HttpStatusCode.OK,
                                mapOf("message" to "Profile picture uploaded")
                            ).also { call.application.log.info("Responded 200: Profile picture uploaded for $userId") }

                        } catch (e: Exception) {
                            call.application.log.error("Profile picture upload failed for user $userId: ${e.message}", e)
                            return@post call.respond(
                                HttpStatusCode.InternalServerError,
                                "Profile picture upload failed"
                            ).also { call.application.log.info("Responded 500: Upload failed for $userId") }
                        }
                    }


                    // Fetch PFP presigned URL
                    get {
                        val principal = call.principal<JWTPrincipal>()
                            ?: return@get call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to "Invalid or missing token")
                            )
                        val userId = principal.payload.getClaim("userId").asString()
                            ?: return@get call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to "Missing userId in token")
                            )

                        val user = userRepository.getUserByUserId(userId)
                        if (user == null) {
                            call.application.log.warn("Fetch PFP failed: User not found: $userId")
                            return@get call.respond(HttpStatusCode.NotFound, "User not found")
                                .also { call.application.log.info("Responded 404: User not found $userId in get") }
                        }

                        val url = user.profilePicKey?.let { S3Service().getPresignedUrlFromKey(it) }

                        call.application.log.info("Fetched presigned URL for user $userId: ${url != null}")

                        call.respond(
                            ProfilePicResponse(
                                profilePicUrl = url,
                                profilePicVersion = user.profilePicVersion
                            )
                        ).also { call.application.log.info("Responded 200: Returned presigned URL for $userId") }
                    }


                    // Delete PFP
                    delete {
                        val principal = call.principal<JWTPrincipal>()
                            ?: return@delete call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to "Invalid or missing token")
                            )
                        val userId = principal.payload.getClaim("userId").asString()
                            ?: return@delete call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to "Missing userId in token")
                            )

                        val user = userRepository.getUserByUserId(userId)
                        if (user == null) {
                            call.application.log.warn("Delete PFP failed: User not found: $userId")
                            return@delete call.respond(HttpStatusCode.NotFound, "User not found")
                                .also { call.application.log.info("Responded 404: User not found $userId") }
                        }

                        val key = user.profilePicKey
                        if (key == null) {
                            call.application.log.warn("Delete PFP failed: No profile picture to delete for user $userId")
                            return@delete call.respond(HttpStatusCode.BadRequest, "No profile picture to delete")
                                .also { call.application.log.info("Responded 400: No PFP to delete for $userId") }
                        }

                        val s3Service = S3Service()

                        try {
                            // 1️⃣ Delete DB key first
                            val dbUpdated = userRepository.deleteProfilePic(userId)
                            if (!dbUpdated) {
                                call.application.log.error("Delete PFP failed: DB update failed for user $userId")
                                return@delete call.respond(
                                    HttpStatusCode.InternalServerError,
                                    "Failed to update user profile"
                                ).also { call.application.log.info("Responded 500: DB update failed for $userId in delete" ) }
                            }
                            call.application.log.info("Profile picture DB cleared for user $userId")

                            // 2️⃣ Delete from S3
                            s3Service.deleteProfilePic(userId)
                            call.application.log.info("Profile picture deleted from S3 for user $userId")

                            return@delete call.respond(HttpStatusCode.OK, mapOf("message" to "Profile picture deleted"))
                                .also { call.application.log.info("Responded 200: PFP deleted for $userId") }

                        } catch (e: Exception) {
                            call.application.log.error("Profile picture deletion failed for user $userId: ${e.message}", e)
                            return@delete call.respond(
                                HttpStatusCode.InternalServerError,
                                "Profile picture deletion failed"
                            ).also { call.application.log.info("Responded 500: Delete failed for $userId") }
                        }
                    }
                }
            }

        }
    }
}