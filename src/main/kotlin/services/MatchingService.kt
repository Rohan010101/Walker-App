package com.example.services

import com.example.domain.model.Candidate
import com.example.domain.repository.AvailabilityRepository
import com.example.logger

class MatchingService(
    private val repository: AvailabilityRepository,
    private val routingService: RoutingService,
    private val topGeoLimit: Int = 30,
    private val topRouteEnrich: Int = 6
) {

    suspend fun findNearbyCandidates(
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
            var dist: Double? = null
            var dur: Double? = null
            if (i < topRouteEnrich) {
                logger().info("if (i < topRouteEnrich) executed")
                val route = routingService.getDistanceAndDuration(av.location.coordinates[0], av.location.coordinates[1], lng, lat )
                dist = route?.first
                dur = route?.second
            }

            val score = computeScore(dist, dur, av.serviceRadiusKm)
            enriched.add(Candidate(av, dist, dur, score))
        }

        return enriched.sortedBy { it.score }.take(limit)

    }



    private fun computeScore(
        distanceMeters: Double?,
        durationSeconds: Double?,
        serviceRadiusKm: Int
    ): Double {
        val INF = 1e9
        val d = distanceMeters ?: INF
        val t = durationSeconds ?: INF

        // Normalize: prefer lower time then distance (can be changed)
        val baseScore = 0.7 * (t / 60.0) + 0.3 * (d / 1000.0)       // t: secs => mins    d: meters => km

        // penalize if outside declared service radius
        val radiusMeters = serviceRadiusKm * 1000
        val penalty = if (distanceMeters != null && distanceMeters > radiusMeters) {
            (distanceMeters - radiusMeters) / 10000.0
        } else 0.0

        return baseScore + penalty
    }

}