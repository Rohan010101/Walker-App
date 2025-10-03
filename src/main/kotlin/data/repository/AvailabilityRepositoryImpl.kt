package com.example.data.repository

import com.example.data.model.WalkerAvailability
import com.example.domain.repository.AvailabilityRepository
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class AvailabilityRepositoryImpl(
    private val db: CoroutineDatabase
): AvailabilityRepository {

    private val availabilities = db.getCollection<WalkerAvailability>()

    override suspend fun ensureIndexes() {
        // 2dSphere Index
//      val index = Document().apply { put("location","2dsphere") }
        val index = Document("location","2dsphere")
        availabilities.createIndex(index)
        availabilities.createIndex(Document("walkerId", 1))         // 1 => asc; -1 => desc;  create an ascending index on the walkerId field.
        availabilities.createIndex(Document("updatedAt", 1))        // 1 => asc; -1 => desc;  create an ascending index on the updated field.
    }

    override suspend fun upsertAvailability(av: WalkerAvailability): Boolean {
        val existing = availabilities.findOne(WalkerAvailability::walkerId eq av.walkerId)
        return if (existing == null) {
            availabilities.insertOne(av).wasAcknowledged()
        } else {
            // update fields: location, isAvailable, serviceRadiusKm, updatedAt
            val updateDoc = Document("\$set", Document().apply {
                put("location", Document("type","Point").append("coordinates", listOf(av.location.coordinates[0], av.location.coordinates[1])))
                put("isAvailable", av.isAvailable)
                put("serviceRadiusKm", av.serviceRadiusKm)
                put("updatedAt", av.updatedAt)
            })
            availabilities.updateOne(
                filter = WalkerAvailability::walkerId eq av.walkerId,
                update = updateDoc
            ).wasAcknowledged()
        }
    }

    override suspend fun getAvailabilityByUser(userId: String): WalkerAvailability? {
        return availabilities.findOne(WalkerAvailability::walkerId eq userId)
    }

    override suspend fun removeAvailability(userId: String): Boolean {
        return availabilities.deleteOne(WalkerAvailability::walkerId eq userId).wasAcknowledged()
    }

    override suspend fun findNearby(
        lng: Double,
        lat: Double,
        maxDistanceMeters: Int,
        limit: Int
    ): List<WalkerAvailability> {

        val geometry = Document("type", "Point").apply { put("coordinates", listOf(lng,lat)) }
        val near = Document(
            "\$near",
            Document()
                .append("\$geometry", geometry)
                .append("\$maxDistance", maxDistanceMeters)
        )
        val query = Document().apply {
            put("location", near)
            put("isAvailable", true)
        }
        val result = availabilities.find(query).limit(limit)
        return result.toList()
    }
}