package com.example.services

import com.example.logger
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*


//class OtpService(
//    private val accountSid: String,
//    private val authToken: String,
//) {
//    private val client = HttpClient(CIO)
//
//    suspend fun sendOtp(target: String, otp: String): String {
//
//        logger().info("sendOtp => target: $target")
//        logger().info("sendOtp => otp: $otp")
//
//        val response: HttpResponse = client.post("https://www.fast2sms.com/dev/bulkV2") {
//            header("authorization", apiKey)
//            parameter("variables_values", otp)
//            parameter("route", "otp")
//            parameter("numbers", target)
//        }
//
//        logger().info("sendOtp => response: $response")
//        logger().info("sendOtp => bodyAsText: ${response.bodyAsText()}")
//
//        return response.bodyAsText()
//    }
//
//
//    // Alternative method using the standard SMS route
//    suspend fun sendOtpViaSms(target: String, otp: String): String {
//        logger().info("sendOtpViaSms => target: $target")
//        logger().info("sendOtpViaSms => otp: $otp")
//
//        val message = "Your OTP is $otp. Valid for 10 minutes. Do not share with anyone."
//
//        val response: HttpResponse = client.post("https://www.fast2sms.com/dev/bulkV2") {
//            header("authorization", apiKey)
//            parameter("message", message)
//            parameter("language", "english")
//            parameter("route", "q")  // Quick SMS route
//            parameter("numbers", target)
//        }
//
//        logger().info("sendOtpViaSms => response: $response")
//        logger().info("sendOtpViaSms => bodyAsText: ${response.bodyAsText()}")
//
//        return response.bodyAsText()
//    }
//
//}



    // FAST 2 SMS
class OtpService (
    private val apiKey: String
) {
    private val client = HttpClient(CIO)

    suspend fun sendOtp(target: String, otp: String): String {

        logger().info("sendOtp => target: $target")
        logger().info("sendOtp => otp: $otp")

        val response: HttpResponse = client.post("https://www.fast2sms.com/dev/bulkV2") {
            header("authorization", apiKey)
            parameter("variables_values", otp)
            parameter("route", "otp")
            parameter("numbers", target)
        }

        logger().info("sendOtp => response: $response")
        logger().info("sendOtp => bodyAsText: ${response.bodyAsText()}")

        return response.bodyAsText()
    }


    // Alternative method using the standard SMS route
    suspend fun sendOtpViaSms(target: String, otp: String): String {
        logger().info("sendOtpViaSms => target: $target")
        logger().info("sendOtpViaSms => otp: $otp")

        val message = "Your OTP is $otp. Valid for 10 minutes. Do not share with anyone."

        val response: HttpResponse = client.post("https://www.fast2sms.com/dev/bulkV2") {
            header("authorization", apiKey)
            parameter("message", message)
            parameter("language", "english")
            parameter("route", "q")  // Quick SMS route
            parameter("numbers", target)
        }

        logger().info("sendOtpViaSms => response: $response")
        logger().info("sendOtpViaSms => bodyAsText: ${response.bodyAsText()}")

        return response.bodyAsText()
    }

    suspend fun sendTestOtp(target: String, otp: String): String {

        logger().info("sendTestOtp => target: $target")
        logger().info("sendTestOtp => otp: $otp")

        val message = "Your OTP is $otp. Valid for 10 minutes. Do not share with anyone."


        return message
    }
}