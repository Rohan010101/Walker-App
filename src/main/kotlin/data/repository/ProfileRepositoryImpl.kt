package com.example.data.repository

import com.example.data.model.UpdateWalkerProfileDto
import com.example.data.model.UpdateWandererProfileDto
import com.example.domain.model.Walker
import com.example.domain.model.Wanderer
import com.example.domain.repository.ProfileRepository
import org.bson.types.ObjectId
import org.litote.kmongo.combine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class ProfileRepositoryImpl(
    private val db: CoroutineDatabase
): ProfileRepository {


    private val wandererProfiles = db.getCollection<Wanderer>()
    private val walkerProfiles = db.getCollection<Walker>()


    override suspend fun createWandererProfile(profile: Wanderer): Boolean {
        return wandererProfiles.insertOne(profile).wasAcknowledged()
    }

    override suspend fun createWalkerProfile(profile: Walker): Boolean {
        return walkerProfiles.insertOne(profile).wasAcknowledged()
    }

    override suspend fun getWandererProfile(userId: String): Wanderer? {
        return wandererProfiles.findOne(Wanderer::userId eq ObjectId(userId))
    }

    override suspend fun getWalkerProfile(userId: String): Walker? {
        return walkerProfiles.findOne(Walker::userId eq ObjectId(userId))
    }

    override suspend fun updateWandererProfile(profile: UpdateWandererProfileDto): Boolean {
        val result =  wandererProfiles.updateOne(
            filter = Wanderer::id eq ObjectId(profile.wandererId),
            update = combine(
                setValue(Wanderer::genderPreference, profile.genderPreference),
                setValue(Wanderer::medicalInfo, profile.medicalInfo),
                setValue(Wanderer::hasPet, profile.hasPet),
                setValue(Wanderer::pace, profile.pace),
                setValue(Wanderer::interests, profile.interests),
                setValue(Wanderer::isAadharVerified, profile.isAadharVerified)
            )
        )

        return result.modifiedCount > 0
    }

    override suspend fun updateWalkerProfile(profile: UpdateWalkerProfileDto): Boolean {
        val result =  walkerProfiles.updateOne(
            filter = Walker::id eq ObjectId(profile.walkerId),
            update = combine(
                setValue(Walker::isAvailable, profile.isAvailable),
                setValue(Walker::bankAccount, profile.bankAccount),
                setValue(Walker::rating, profile.rating),
                setValue(Walker::serviceRadiusKm, profile.serviceRadiusKm),
                setValue(Walker::completedWalks, profile.completedWalks),
                setValue(Walker::isAadharVerified, profile.isAadharVerified)
            )
        )
        return result.modifiedCount > 0
    }
}