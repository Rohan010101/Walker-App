package com.example.data.repository

import com.example.data.model.UpdateUserDto
import com.example.domain.model.User
import com.example.domain.repository.UserRepository
import com.example.logger
import org.bson.types.ObjectId
import org.litote.kmongo.combine
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class UserRepositoryImpl(
    db: CoroutineDatabase
): UserRepository {

    private val users = db.getCollection<User>()


    override suspend fun ensureIndexes() {
        users.ensureUniqueIndex(User::email)
        users.ensureUniqueIndex(User::username)
        users.ensureUniqueIndex(User::phone)
    }

    override suspend fun createUser(user: User): User? {
        return try {
            val result = users.insertOne(user).wasAcknowledged()
            if (result) user
            else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        val user = users.findOne(User::username eq username)
        return user
    }

    override suspend fun getUserByEmail(email: String): User? {
        val user = users.findOne(User::email eq email)
        return user
    }

    override suspend fun getUserByPhone(phone: String): User? {
        logger().info("check1")
        logger().info(phone)
        return try {
            val user = users.findOne(User::phone eq phone)
            logger().info("check2, user=$user")
            user
        } catch (e: Exception) {
            logger().error("Error fetching user by phone=$phone", e)
            null
        }

    }

    override suspend fun getUserByUserId(userId: String): User? {
        val user = users.findOneById(userId)
        return user
    }

    override suspend fun updateUserProfile(userId: String, updatedUser: UpdateUserDto): Boolean {
        val result = users.updateOne(
            filter = User::id eq ObjectId(userId),
            update = combine(
                setValue(User::name, updatedUser.name),
            )
        )
        return result.modifiedCount > 0
    }

    override suspend fun updateProfilePic(userId: String, key: String): Boolean {
        val result = users.updateOne(
            filter = User::id eq ObjectId(userId),
            update = combine (
                setValue(User::profilePicKey, key),
                setValue(User::profilePicVersion, System.currentTimeMillis()) // NEW
            )

        )
        return result.modifiedCount > 0
    }

    override suspend fun deleteProfilePic(userId: String): Boolean {
        val result = users.updateOne(
            filter = User::id eq ObjectId(userId),
            update = combine(
                setValue(User::profilePicKey, null),
                setValue(User::profilePicVersion, null) // NEW
            )
        )
        return result.modifiedCount > 0
    }

    override suspend fun getAllUsers(): List<User> {
        logger().info("Fetching all users")
        val users = users.find().toList()
        logger().info("Fetched ${users.size} users")
        return users
    }

    override suspend fun isEmailTaken(email: String): Boolean {
        logger().info("Checking if email is taken: $email")
        val taken = getUserByEmail(email) != null
        logger().info("isEmailTaken($email): $taken")
        return taken
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        logger().info("Checking if username is taken: $username")
        val taken = getUserByUsername(username) != null
        logger().info("isUsernameTaken($username): $taken")
        return taken
    }

    override suspend fun isPhoneTaken(phone: String): Boolean {
        logger().info("Checking if phone is taken: $phone")
        val taken = getUserByPhone(phone) != null
        logger().info("isPhoneTaken($phone): $taken")
        return taken
    }

}