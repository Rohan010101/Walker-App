package com.example.routing

import com.example.data.model.*
import com.example.domain.repository.AvailabilityRepository
import com.example.domain.repository.UserRepository
import com.example.domain.repository.WalkerRepository
import com.example.logger
import com.example.services.MatchingService
import com.example.utils.userIdOrRespond
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.Route

fun Route.geoRoutes(
    userRepository: UserRepository,
    walkerRepository: WalkerRepository,
    availabilityRepository: AvailabilityRepository,
    matchingService: MatchingService
) {

    route("/geo") {

        authenticate("auth-jwt") {

            // Walker's regularly post there location continuously WHEN THEY ARE ONLINE/ON DUTY
            post("/location") {
                val userId = call.userIdOrRespond() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<LocationUpdateRequest>()
                val walker = walkerRepository.findByUserId(userId) ?: return@post

                val av = WalkerAvailability(
                    walkerId = walker.id.toHexString(),
                    location = GeoPoint(coordinates = listOf(request.lng, request.lat)),
                    isAvailable = request.isAvailable,
                    serviceRadiusKm = request.serviceRadiusKm,
                    updatedAt = System.currentTimeMillis()
                )

                val ok = availabilityRepository.upsertAvailability(av)
                call.respond(mapOf("ok" to ok))
            }



            // For Wanderer: Fetches list of walkers around them
            get("/nearby") {
                logger().info("check1")

                val userId = call.userIdOrRespond() ?: return@get call.respond(HttpStatusCode.BadRequest)

                val lng = call.request.queryParameters["lng"]?.toDoubleOrNull()
                val lat = call.request.queryParameters["lat"]?.toDoubleOrNull()
                val radiusKm = call.request.queryParameters["radiusKm"]?.toDoubleOrNull() ?: 5.0
                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

                logger().info("check2")

                if (lng == null || lat == null) {
                    call.respond(mapOf("error" to "lng & lat are required"))
                    return@get
                }

                logger().info("check3")


                val radiusMeters = (radiusKm * 1000).toInt()
                val candidates = matchingService.findNearbyCandidates(userId, lng, lat, radiusMeters, limit)

                logger().info("check4")
                val dto = candidates.map {

                    val walkerId = it.availability.walkerId
                    logger().info("walkerId: $walkerId")
                    val walker = walkerRepository.findByWalkerId(walkerId)
                    logger().info("walker: $walker")
                    val user = userRepository.getUserByWalkerId(walkerId)
                    logger().info("user: $user")

                    // TODO: Substitute !! with something better
                    val walkerPublicDto = WalkerPublicDto(
                         walkerId = walker!!.id.toHexString(),
                         name = user!!.name,
                         phone = user.phone,
                         bio = walker.bio,
                         gender = user.gender ,
                         rating = walker.rating,
                         completedWalks = walker.completedWalks,
                         languages = walker.languages,
                         specialties = walker.specialties,
                         serviceRadiusKm = walker.serviceRadiusKm,
                         isAadharVerified = walker.isAadharVerified
                    )
                    logger().info("walkerPublicDto: $WalkerPublicDto")

                    val nearbyCandidateDto = NearbyCandidateDto(
                        walker = walkerPublicDto,
                        lng = it.availability.location.coordinates[0],
                        lat = it.availability.location.coordinates[1],
                        distanceMeters = it.distanceMetersFromRequester,
                        durationSeconds = it.durationSeconds,
                        serviceRadiusKm = it.availability.serviceRadiusKm,
                        score = it.score
                    )

                    nearbyCandidateDto
                }
                logger().info("dto: $dto")


                call.respond(NearbyResponse(dto))

            }
        }
    }
}