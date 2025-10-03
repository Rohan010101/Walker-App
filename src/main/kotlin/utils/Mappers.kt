package com.example.utils

import com.example.data.model.*
import com.example.domain.model.FamilyMember
import com.example.domain.model.User
import com.example.domain.model.Walker
import com.example.domain.model.Wanderer
import org.bson.types.ObjectId

// Convert User (DB entity) into detailed response (for profile, settings, chat usage, etc.)
fun User.toUserResponse() = UserResponse(
    id = this.id.toHexString(),
    username = this.username,
    name = this.name,
    email = this.email,
    phone = this.phone,
    profilePicKey = this.profilePicKey,
    profilePicVersion = this.profilePicVersion,
    dob = this.dob,
    gender = this.gender,
    roles = this.roles.map { it.name },
    createdAt = this.createdAt
)





// Wanderer Mappers


// Create DTO → Domain
fun CreateWandererDto.toDomain(userId: String): Wanderer =
    Wanderer(
        id = ObjectId(),
        userId = ObjectId(userId),
        genderPreference = genderPreference,
        medicalInfo = medicalInfo,
        medicalConditions = medicalConditions,
        hasPet = hasPet,
        pace = pace,
        languages = languages,
        interests = interests,
        topicsOfConversation = topicsOfConversation,
        isAadharVerified = isAadharVerified
    )




// WandererMapper.kt
fun WandererDto.toDomain(userId: String): Wanderer =
    Wanderer(
        id = ObjectId(),
        userId = ObjectId(userId),
        genderPreference = genderPreference,
        medicalInfo = medicalInfo,
        medicalConditions = medicalConditions,
        hasPet = hasPet,
        pace = pace,
        languages = languages,
        interests = interests,
        topicsOfConversation = topicsOfConversation,
        isAadharVerified = isAadharVerified
    )

//fun UpdateWandererProfileDto.toDomain(existing: Wanderer): Wanderer =
//    existing.copy(
//        genderPreference = genderPreference,
//        medicalInfo = medicalInfo,
//        medicalConditions = medicalConditions,
//        hasPet = hasPet,
//        pace = pace,
//        languages = languages,
//        interests = interests,
//        topicsOfConversation = topicsOfConversation,
//        isAadharVerified = isAadharVerified
//    )

fun Wanderer.toDto(): WandererDto =
    WandererDto(
        id = id.toHexString(),
        genderPreference = genderPreference,
        medicalInfo = medicalInfo,
        medicalConditions = medicalConditions,
        hasPet = hasPet,
        pace = pace,
        languages = languages,
        interests = interests,
        topicsOfConversation = topicsOfConversation,
        isAadharVerified = isAadharVerified
    )





// WalkerMapper.kt

// Create DTO → Domain
fun CreateWalkerDto.toDomain(userId: String): Walker =
    Walker(
        id = ObjectId(),
        userId = ObjectId(userId),
        bio = bio,
        isAvailable = isAvailable,
        serviceRadiusKm = serviceRadiusKm,
        bankAccount = bankAccount,
        rating = 0.0, // new walker starts fresh
        completedWalks = 0,
        languages = languages,
        specialties = specialties,
        isAadharVerified = isAadharVerified
    )



fun WalkerDto.toDomain(userId: String): Walker =
    Walker(
        id = ObjectId(),
        userId = ObjectId(userId),
        bio = bio,
        isAvailable = isAvailable,
        serviceRadiusKm = serviceRadiusKm,
        bankAccount = bankAccount,
        rating = rating,
        completedWalks = completedWalks,
        languages = languages,
        specialties = specialties,
        isAadharVerified = isAadharVerified
    )

fun UpdateWalkerProfileDto.toDomain(existing: Walker): Walker =
    existing.copy(
        bio = bio,
        isAvailable = isAvailable,
        serviceRadiusKm = serviceRadiusKm,
        bankAccount = bankAccount,
        rating = rating,
        completedWalks = completedWalks,
        languages = languages,
        specialties = specialties,
        isAadharVerified = isAadharVerified
    )

fun Walker.toDto(): WalkerDto =
    WalkerDto(
        id = id.toHexString(),
        bio = bio,
        isAvailable = isAvailable,
        serviceRadiusKm = serviceRadiusKm,
        bankAccount = bankAccount,
        rating = rating,
        completedWalks = completedWalks,
        languages = languages,
        specialties = specialties,
        isAadharVerified = isAadharVerified
    )





// FamilyMapper.kt



// Create DTO → Domain
fun CreateFamilyMemberDto.toDomain(wandererId: String): FamilyMember =
    FamilyMember(
        id = ObjectId(),
        wandererId = ObjectId(wandererId),
        name = name,
        phone = phone,
        email = email,
        profilePicKey = profilePicKey,
        profilePicVersion = profilePicVersion,
        dob = dob,
        gender = gender,
        genderPreference = genderPreference,
        medicalInfo = medicalInfo,
        medicalConditions = medicalConditions,
        hasPet = hasPet,
        pace = pace,
        languages = languages,
        interests = interests,
        topicsOfConversation = topicsOfConversation
    )




fun FamilyMemberDto.toDomain(wandererId: String): FamilyMember =
    FamilyMember(
        id = ObjectId(),
        wandererId = ObjectId(wandererId),
        name = name,
        phone = phone,
        email = email,
        profilePicKey = profilePicKey,
        profilePicVersion = profilePicVersion,
        dob = dob,
        gender = gender,
        genderPreference = genderPreference,
        medicalInfo = medicalInfo,
        medicalConditions = medicalConditions,
        hasPet = hasPet,
        pace = pace,
        languages = languages,
        interests = interests,
        topicsOfConversation = topicsOfConversation
    )

fun UpdateFamilyMemberDto.toDomain(existing: FamilyMember): FamilyMember =
    existing.copy(
        name = name,
        phone = phone,
        email = email,
        profilePicKey = profilePicKey,
        profilePicVersion = profilePicVersion,
//        dob = dob,
//        gender = gender,
        genderPreference = genderPreference,
        medicalInfo = medicalInfo,
        medicalConditions = medicalConditions,
        hasPet = hasPet,
        pace = pace,
        languages = languages,
        interests = interests,
        topicsOfConversation = topicsOfConversation
    )

fun FamilyMember.toDto(): FamilyMemberDto =
    FamilyMemberDto(
        id = id.toHexString(),
        wandererId = wandererId.toHexString(),
        name = name,
        phone = phone,
        email = email,
        profilePicKey = profilePicKey,
        profilePicVersion = profilePicVersion,
        dob = dob,
        gender = gender,
        genderPreference = genderPreference,
        medicalInfo = medicalInfo,
        medicalConditions = medicalConditions,
        hasPet = hasPet,
        pace = pace,
        languages = languages,
        interests = interests,
        topicsOfConversation = topicsOfConversation
    )
