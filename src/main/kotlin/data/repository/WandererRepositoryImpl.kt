package com.example.data.repository

import com.example.data.model.UpdateWandererProfileDto
import com.example.domain.model.Wanderer
import com.example.domain.repository.WandererRepository
import org.bson.types.ObjectId
import org.litote.kmongo.combine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class WandererRepositoryImpl(
    db: CoroutineDatabase
): WandererRepository {

    private val wanderers = db.getCollection<Wanderer>()

    override suspend fun create(profile: Wanderer): Wanderer? {
        return try {
            val result = wanderers.insertOne(profile).wasAcknowledged()
            if (result) profile
            else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun findByUserId(userId: String): Wanderer? {
        return wanderers.findOne(Wanderer::userId eq ObjectId(userId))
    }

    override suspend fun update(profile: UpdateWandererProfileDto): Boolean {
        val result = wanderers.updateOne(
            filter = Wanderer::id eq ObjectId(profile.wandererId),
            update = combine(
                setValue(Wanderer::genderPreference, profile.genderPreference),
                setValue(Wanderer::medicalInfo, profile.medicalInfo),
                setValue(Wanderer::medicalConditions, profile.medicalConditions),
                setValue(Wanderer::hasPet, profile.hasPet),
                setValue(Wanderer::pace, profile.pace),
                setValue(Wanderer::languages, profile.languages),
                setValue(Wanderer::interests, profile.interests),
                setValue(Wanderer::topicsOfConversation, profile.topicsOfConversation),
                setValue(Wanderer::isAadharVerified, profile.isAadharVerified)
            )
        )
        return result.modifiedCount > 0
    }
}