package com.example.services

import com.example.data.model.DirectionResponse
import com.example.logger
import com.example.utils.Constants.OLA_API_KEY
import com.example.utils.Constants.OLA_BASE_URL
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.json

class RoutingService(
    private val client: HttpClient
) {

    private val json = Json {
        ignoreUnknownKeys = true  // ignore extra fields
    }


    suspend fun getDistanceAndDuration(
        originLng: Double,
        originLat: Double,
        destLng: Double,
        destLat: Double
    ): Pair<Double, Double>? {

        logger().info("RoutingService => getDistanceAndDuration executed")


        val url = "$OLA_BASE_URL/routing/v1/directions/basic"
        val coords = "${originLng},${originLat};${destLng},${destLat}"
        val origin = "${originLat},${originLng}"        // lat,lng (format)
        val destination = "${destLat},${destLng}"
        val mode = "walking"

        logger().info("url : $url")
        logger().info("origin : $origin")
        logger().info("destination: $destination")
        logger().info("mode : $mode")


        return try {
            logger().info("RoutingService => getDistanceAndDuration => try executed")
            val response = client.post(url) {
                parameter("api_key", OLA_API_KEY)
                parameter("origin", origin)
                parameter("destination", destination)
//                parameter("coordinates", coords)
                parameter("mode", mode)
            }
            logger().info("raw response: ${response.bodyAsText()}")




            val directionResponse: DirectionResponse = json.decodeFromString(response.bodyAsText())
            logger().info("DirectionResponse: ${directionResponse.status}")


            val route = directionResponse.routes.firstOrNull() ?: return null
            logger().info("route : $route ")

            val leg = route.legs.firstOrNull() ?: return null
            logger().info("leg : $leg ")

            Pair(leg.distance, leg.duration)
        } catch (e: Exception) {
            logger().info("RoutingService => getDistanceAndDuration => catch executed")
            null
        }
    }
}