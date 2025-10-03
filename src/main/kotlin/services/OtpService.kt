package com.example.services

import com.example.logger
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.rest.verify.v2.service.Verification
import com.twilio.rest.verify.v2.service.VerificationCheck
import com.twilio.type.Client
import com.twilio.type.PhoneNumber


class OtpService (
    private val accountSid: String,
    private val authToken: String,
//    private val fromNumber: String,
    private val verifyServiceSid: String
) {
    init {
        Twilio.init(accountSid, authToken)
    }


    /**
     * Sends an OTP via Twilio Verify (no fromNumber needed).
     */
    fun sendOtp(to: String): Boolean {
        val toPhoneNumber = if (to.startsWith("+")) to else "+91$to" // prepend country code if missing
        return try {
            Verification.creator(
                verifyServiceSid,
                toPhoneNumber,
                "sms"
            ).create()
            true
        } catch (e: Exception) {
            logger().error("Failed to send OTP SMS to $to: ${e.message}", e)
            false
        }
    }

    /**
     * Verifies the OTP with Twilio Verify.
     */
    fun verifyOtp(to: String, otp: String): Boolean {
        val toPhoneNumber = if (to.startsWith("+")) to else "+91$to"
        return try {
            val result = VerificationCheck.creator(
                verifyServiceSid
            )
                .setTo(toPhoneNumber)
                .setCode(otp)
                .create()

            result.status == "approved"
        } catch (e: Exception) {
            logger().error("Failed to verify OTP for $to: ${e.message}", e)
            false
        }
    }


}