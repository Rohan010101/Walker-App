package com.example.data.repository

import com.example.domain.model.OtpEntry
import com.example.domain.repository.OtpRepository
import com.example.services.OtpService
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import java.time.Instant

class OtpRepositoryImpl(
    private val db: CoroutineDatabase,
    private  val otpService: OtpService
): OtpRepository {

    private val otpEntries = db.getCollection<OtpEntry>()

    override suspend fun saveOtp(target: String, otp: String, ttlMs: Long) {
        val expires = Instant.now().toEpochMilli() + ttlMs
        // clean old OTP for this target
        otpEntries.deleteOne(OtpEntry::target eq target)
        otpEntries.insertOne(
            OtpEntry(
                target = target,
                otp = otp,
                expiresAt = expires
            )
        )
    }


    override suspend fun verifyOtp(target: String, otp: String): Boolean {
        val entry = otpEntries.findOne(OtpEntry::target eq target) ?: return false
        val now = Instant.now().toEpochMilli()

        return if (entry.otp == otp && entry.expiresAt > now) {
            otpEntries.deleteOne(OtpEntry::target eq target) // consume OTP
            true
        } else {
            false
        }
    }


    override suspend fun deliverOtp(target: String, otp: String) {
        otpService.sendOtpViaSms(target, otp)
    }

    override suspend fun deliverTestOtp(target: String, otp: String): String {
        return otpService.sendTestOtp(target, otp)
    }

}