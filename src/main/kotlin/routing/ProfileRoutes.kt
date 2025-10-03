package com.example.routing

import com.example.data.model.*
import com.example.logger
import com.example.services.FamilyService
import com.example.services.WalkerService
import com.example.services.WandererService
import com.example.utils.ProfileUpdateResult
import com.example.utils.userIdOrRespond
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route

fun Route.profileRoutes(
    walkerService: WalkerService,
    wandererService: WandererService,
    familyService: FamilyService
) {
    authenticate("auth-jwt") {
        route("/users/me"){

            // ----------------- WALKER -----------------

            route("/walker") {
                post {
                    val userId = call.userIdOrRespond() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    logger().info("createWalker => userId: $userId")
                    val dto = call.receive<CreateWalkerDto>()
                    val created = walkerService.createProfile(userId, dto)
                    if (created != null) call.respond(HttpStatusCode.Created, created)
                    else call.respond(HttpStatusCode.InternalServerError, "Failed to create Walker profile")
                }

                get {
                    val userId = call.userIdOrRespond() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    logger().info("getWalker => userId: $userId")
                    val profile = walkerService.getProfile(userId)
                    if (profile != null) call.respond(profile)
                    else call.respond(HttpStatusCode.NotFound)
                }

                put {
                    val userId = call.userIdOrRespond() ?: return@put call.respond(HttpStatusCode.BadRequest)
                    logger().info("updateWalker => userId: $userId")
                    val dto = call.receive<UpdateWalkerProfileDto>()

                    when (walkerService.updateProfile(userId, dto)) {
                        ProfileUpdateResult.NotFound ->
                            call.respond(
                                HttpStatusCode.NotFound,
                                mapOf("success" to false, "message" to "Walker profile not found")
                            )

                        ProfileUpdateResult.Forbidden ->
                            call.respond(
                                HttpStatusCode.Forbidden,
                                mapOf("success" to false, "message" to "Walker ID mismatch")
                            )

                        ProfileUpdateResult.Failed ->
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                mapOf("success" to false, "message" to "Failed to update Walker profile")
                            )

                        ProfileUpdateResult.Success ->
                            call.respond(
                                HttpStatusCode.OK,
                                mapOf("success" to true, "message" to "Walker profile updated successfully")
                            )
                    }
                }
            }




            // ----------------- WANDERER -----------------

            route("/wanderer") {


                post {
                    val userId = call.userIdOrRespond() ?: return@post call.respond(HttpStatusCode.BadRequest)
                    logger().info("createWanderer => userId: $userId")
                    val dto = call.receive<CreateWandererDto>()
                    val created = wandererService.createProfile(userId, dto)
                    if (created != null) call.respond(HttpStatusCode.Created, created)
                    else call.respond(HttpStatusCode.InternalServerError, "Failed to create Wanderer profile")
                }

                get {
                    val userId = call.userIdOrRespond() ?: return@get call.respond(HttpStatusCode.BadRequest)
                    logger().info("getWanderer => userId: $userId")
                    val profile = wandererService.getProfile(userId)
                    if (profile != null) call.respond(profile)
                    else call.respond(HttpStatusCode.NotFound, "Wanderer profile not found")
                }

                put {
                    val userId = call.userIdOrRespond() ?: return@put call.respond(HttpStatusCode.BadRequest)
                    logger().info("updateWanderer => userId: $userId")
                    val dto = call.receive<UpdateWandererProfileDto>()
                    when (wandererService.updateProfile(userId, dto)) {
                        ProfileUpdateResult.NotFound ->
                            call.respond(
                                HttpStatusCode.NotFound,
                                mapOf("success" to false, "message" to "Wanderer profile not found")
                            )

                        ProfileUpdateResult.Forbidden ->
                            call.respond(
                                HttpStatusCode.Forbidden,
                                mapOf("success" to false, "message" to "Wanderer ID mismatch")
                            )

                        ProfileUpdateResult.Failed ->
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                mapOf("success" to false, "message" to "Failed to update Wanderer profile")
                            )

                        ProfileUpdateResult.Success ->
                            call.respond(
                                HttpStatusCode.OK,
                                mapOf("success" to true, "message" to "Wanderer profile updated successfully")
                            )
                    }
                }


                // FAMILY

                route("/family") {


                    post {
                        val userId = call.userIdOrRespond() ?: return@post call.respond(HttpStatusCode.BadRequest)
                        logger().info("createFamily => userId: $userId")
                        val dto = call.receive<CreateFamilyMemberDto>()
                        val member = familyService.addFamilyMember(userId, dto)
                        if (member != null) call.respond(HttpStatusCode.Created, member)
                        else call.respond(HttpStatusCode.BadRequest, "User does not have a Wanderer profile")
                    }

                    put {
                        val userId = call.userIdOrRespond() ?: return@put call.respond(HttpStatusCode.BadRequest)
                        logger().info("updateFamily => userId: $userId")
                        val dto = call.receive<UpdateFamilyMemberDto>()
                        when (familyService.updateFamilyMember(userId, dto)) {
                            ProfileUpdateResult.NotFound ->
                                call.respond(HttpStatusCode.NotFound, mapOf("success" to false, "message" to "Family member not found"))
                            ProfileUpdateResult.Forbidden ->
                                call.respond(HttpStatusCode.Forbidden, mapOf("success" to false, "message" to "Cannot update family member of another Wanderer"))
                            ProfileUpdateResult.Failed ->
                                call.respond(HttpStatusCode.InternalServerError, mapOf("success" to false, "message" to "Failed to update family member"))
                            ProfileUpdateResult.Success ->
                                call.respond(HttpStatusCode.OK, mapOf("success" to true, "message" to "Family member updated successfully"))
                        }
                    }

                    get {
                        val userId = call.userIdOrRespond() ?: return@get call.respond(HttpStatusCode.BadRequest)
                        logger().info("getFamily => userId: $userId")
                        val members = familyService.listFamily(userId)
                        call.respond(members)
                    }

                }


            }



        }
    }
}