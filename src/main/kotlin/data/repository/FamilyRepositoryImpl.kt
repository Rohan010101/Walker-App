package com.example.data.repository

import com.example.data.model.UpdateFamilyMemberDto
import com.example.domain.model.FamilyMember
import com.example.domain.repository.FamilyRepository
import org.bson.types.ObjectId
import org.litote.kmongo.combine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class FamilyRepositoryImpl(
    db: CoroutineDatabase
): FamilyRepository {

    private val members = db.getCollection<FamilyMember>()


    override suspend fun create(profile: FamilyMember): FamilyMember? {
        return try {
            val result = members.insertOne(profile).wasAcknowledged()
            if (result) profile
            else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun findByWandererId(wandererId: String): List<FamilyMember> {
        return members.find(FamilyMember::wandererId eq ObjectId(wandererId)).toList()
    }

    override suspend fun update(profile: UpdateFamilyMemberDto): Boolean {
        val result = members.updateOne(
            filter = FamilyMember::wandererId eq ObjectId(profile.wandererId),
            update = combine(
                setValue(FamilyMember::name, profile.name),
                setValue(FamilyMember::phone, profile.phone),
                setValue(FamilyMember::email, profile.email),
                setValue(FamilyMember::profilePicKey, profile.profilePicKey),
                setValue(FamilyMember::profilePicVersion, profile.profilePicVersion),
                setValue(FamilyMember::genderPreference, profile.genderPreference),
                setValue(FamilyMember::medicalInfo, profile.medicalInfo),
                setValue(FamilyMember::medicalConditions, profile.medicalConditions),
                setValue(FamilyMember::hasPet, profile.hasPet),
                setValue(FamilyMember::pace, profile.pace),
                setValue(FamilyMember::languages, profile.languages),
                setValue(FamilyMember::interests, profile.interests),
                setValue(FamilyMember::topicsOfConversation, profile.topicsOfConversation)
            )
        )
        return result.modifiedCount > 0
    }
}