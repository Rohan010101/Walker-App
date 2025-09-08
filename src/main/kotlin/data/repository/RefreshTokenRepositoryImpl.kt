package com.example.data.repository

import com.example.data.model.RefreshToken
import com.example.domain.repository.RefreshTokenRepository
import com.example.logger
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setTo

class RefreshTokenRepositoryImpl(
    db: CoroutineDatabase
): RefreshTokenRepository {

    private val refreshTokens = db.getCollection<RefreshToken>()


    override suspend fun ensureIndexes() {
        logger().info("RefreshTokenRepositoryImpl => ensureIndexes executed")
        try {
            refreshTokens.ensureIndex(RefreshToken::userId)
            refreshTokens.ensureUniqueIndex(RefreshToken::jti)
            refreshTokens.ensureIndex(RefreshToken::familyId)
            refreshTokens.ensureIndex(RefreshToken::expiresAt)
            logger().info("Indexes ensured successfully")
        } catch (e: Exception) {
            logger().error("Error ensuring indexes: ${e.message}", e)
        }
    }

    override suspend fun insert(rt: RefreshToken): Boolean {
        logger().info("RefreshTokenRepositoryImpl => insert executed => refreshToken: $rt")
        return try {
            val result = refreshTokens.insertOne(rt).wasAcknowledged()
            logger().info("Insert result for refreshToken jti=${rt.jti}: $result")
            result
        } catch (e: Exception) {
            logger().error("Error inserting refreshToken: ${e.message}", e)
            false
        }
    }

    override suspend fun findByJti(jti: String): RefreshToken? {
        logger().info("RefreshTokenRepositoryImpl => findByJti executed => jti: $jti")
        return try {
            val result = refreshTokens.find(RefreshToken::jti eq jti).first()
            logger().info("Find result for jti=$jti: $result")
            result
        } catch (e: Exception) {
            logger().error("Error finding refreshToken by jti=$jti: ${e.message}", e)
            null
        }
    }

    override suspend fun revokeJti(jti: String, replacedByJti: String?): Boolean {
        logger().info("RefreshTokenRepositoryImpl => revokeJti executed => jti: $jti, replacedByJti: $replacedByJti")
        return try {
            val result = refreshTokens.updateOne(
                RefreshToken::jti eq jti,
                set(
                    RefreshToken::revoked setTo true,
                    RefreshToken::replacedByJti setTo replacedByJti
                )
            ).wasAcknowledged()
            logger().info("Revoke result for jti=$jti: $result")
            result
        } catch (e: Exception) {
            logger().error("Error revoking refreshToken jti=$jti: ${e.message}", e)
            false
        }
    }

    override suspend fun revokeFamily(familyId: String): Boolean {
        logger().info("RefreshTokenRepositoryImpl => revokeFamily executed => familyId: $familyId")
        return try {
            val result = refreshTokens.updateMany(
                RefreshToken::familyId eq familyId,
                set(RefreshToken::revoked setTo true)
            ).wasAcknowledged()
            logger().info("Revoke result for familyId=$familyId: $result")
            result
        } catch (e: Exception) {
            logger().error("Error revoking familyId=$familyId: ${e.message}", e)
            false
        }
    }
}