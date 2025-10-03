package com.example.services

import com.example.domain.model.Candidate
import com.example.domain.model.Walker
import com.example.domain.model.Wanderer
import com.example.domain.repository.AvailabilityRepository
import com.example.domain.repository.WalkerRepository
import com.example.domain.repository.WandererRepository
import com.example.logger
import kotlin.math.ln

class MatchingService(
    private val repository: AvailabilityRepository,
    private val walkerRepository: WalkerRepository,
    private val wandererRepository: WandererRepository,
    private val routingService: RoutingService,
    private val topGeoLimit: Int = 30,
    private val topRouteEnrich: Int = 6
) {

    suspend fun findNearbyCandidates(
        userId: String,
        lng: Double,
        lat: Double,
        radiusMeters: Int,
        limit: Int = 20
    ): List<Candidate> {

        // Step 1: get geo-near raw candidates (cheap)
        val raw = repository.findNearby(lng, lat, radiusMeters, topGeoLimit)
        if (raw.isEmpty()) return emptyList()

        // Step 2: select subset to enrich via routing
//        val toEnrich = raw.take(topRouteEnrich)

        // For enriched candidates gather distance/duration
        val enriched = mutableListOf<Candidate>()
        for ((i,av) in raw.withIndex()) {
            val walker = walkerRepository.findByUserId(av.walkerId) ?: continue
            /*TODO: alert system that the wanderer cant be found using the */
            val wanderer =  wandererRepository.findByUserId(userId) ?: return emptyList()

            var dist: Double? = null
            var dur: Double? = null
            if (i < topRouteEnrich) {
                logger().info("if (i < topRouteEnrich) executed")
                val route = routingService.getDistanceAndDuration(av.location.coordinates[0], av.location.coordinates[1], lng, lat )
                dist = route?.first
                dur = route?.second
            }


            val score = computeScore(wanderer, walker, dist, dur, av.serviceRadiusKm)
            enriched.add(Candidate(av, dist, dur, score))
        }

        return enriched.sortedBy { it.score }.take(limit)

    }



    /*TODO: Improve the scoring system*/
    private fun computeScore(
        wander: Wanderer,
        walker: Walker,
        distanceMeters: Double?,
        durationSeconds: Double?,
        serviceRadiusKm: Int
    ): Double {
        val INF = 1e9
        val d = distanceMeters ?: INF
        val t = durationSeconds ?: INF
        val r = walker.rating
        val c = walker.completedWalks.toDouble()
        /*TODO: Languages to be changed into ENUM*/
        val l = wander.languages.intersect(walker.languages.toSet()).size

        // Normalize: prefer lower time then distance (can be changed)
        val baseScore = 0.7 * (t / 60.0) + 0.3 * (d / 1000.0)       // t: secs => mins    d: meters => km

        // penalize if outside declared service radius
        val radiusMeters = serviceRadiusKm * 1000
        val penalty = if (distanceMeters != null && distanceMeters > radiusMeters) {
            (distanceMeters - radiusMeters) / 10000.0
        } else 0.0


        // Factor: rating (higher rating = lower score)
        val ratingScore = - 0.5 * r

        // Factor: experience
        val experienceScore = - 0.2 * ln(1 + c)

        // Factor: aadhar verification
        val verificationScore = - 0.5

        // Factor: language match (bonus)
        val languageScore = -0.3 * l

        val score = baseScore + penalty + ratingScore + experienceScore + verificationScore + languageScore

        return score
    }

}