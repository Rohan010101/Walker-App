package com.example.data.model

import com.mongodb.client.model.geojson.PolygonCoordinates
import kotlinx.serialization.Serializable

@Serializable
data class GeoPoint(
    val type: String = "Point",
    val coordinates: List<Double>       // [lng,lat]
) {
    init {
        require(coordinates.size == 2) { "GeoPoint must have [lng, lat]" }
    }

//    val lng: Double get() = coordinates[0]
//    val lat: Double get() = coordinates[1]
}
