package com.example.data.repository

import com.example.data.model.UpdateWalkerProfileDto
import com.example.domain.model.Walker
import com.example.domain.repository.WalkerRepository
import org.bson.types.ObjectId
import org.litote.kmongo.combine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class WalkerRepositoryImpl(
    db: CoroutineDatabase
): WalkerRepository {

    private val walkers = db.getCollection<Walker>()

    override suspend fun create(profile: Walker): Walker? {
        return try {
            val result = walkers.insertOne(profile).wasAcknowledged()
            if (result) profile
            else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun findByUserId(userId: String): Walker? {
        return walkers.findOne(Walker::userId eq ObjectId(userId))
    }

    override suspend fun findByWalkerId(walkerId: String): Walker? {
        return walkers.findOne(Walker::id eq ObjectId(walkerId))
    }

    override suspend fun update(profile: UpdateWalkerProfileDto): Boolean {
        val result = walkers.updateOne(
            filter = Walker::id eq ObjectId(profile.walkerId),
            update = combine(
                setValue(Walker::bio, profile.bio),
                setValue(Walker::isAvailable, profile.isAvailable),
                setValue(Walker::serviceRadiusKm, profile.serviceRadiusKm),
                setValue(Walker::bankAccount, profile.bankAccount),
                setValue(Walker::rating, profile.rating),
                setValue(Walker::completedWalks, profile.completedWalks),
                setValue(Walker::languages, profile.languages),
                setValue(Walker::specialties, profile.specialties),
                setValue(Walker::isAadharVerified, profile.isAadharVerified)
            )
        )
        return result.modifiedCount > 0
    }
}