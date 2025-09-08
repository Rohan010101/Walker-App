package com.example.s3

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.*
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlin.time.Duration.Companion.hours

class S3Service (
    private val bucketName: String = System.getenv("S3_BUCKET"),
) {
    private val client = HttpClient(CIO)




    // USE OBJECT KEY DIRECTLY TO FETCH PRESIGNED URL
    suspend fun getPresignedUrlFromKey(key: String, version: Long? = null): String? {
        val s3 = S3Client.fromEnvironment {  }

        return try {
            val request = GetObjectRequest {
                bucket = bucketName
                this.key = key
            }

            val presigned = s3.presignGetObject(
                input = request,
                duration = 24.hours
            )

            presigned.url.toString()

        } catch (e: NoSuchKey) {
            null
        } catch (e: NotFound) {
            null
        } catch (e: S3Exception) {
            println("S3 exception: ${e.message}")
            null
        } catch (e: Exception) {
            println("S3 error: ${e.message}")
            null
        }
    }


    // 1. PROFILE PICTURE



    // Upload Profile Picture to S3 (presigned)
    suspend fun uploadProfilePic(
        userId: String,
        imageBytes: ByteArray
    ) {
        val key = "profilePicture/$userId/$userId.jpg"
        val s3 = S3Client.fromEnvironment {  }

        val unsignedPutObjectRequest = PutObjectRequest{
            bucket = bucketName
            this.key = key
        }


        val presigned = s3.presignPutObject(
            input = unsignedPutObjectRequest,
            duration = 24.hours
        )

        val response = client.put(presigned.url.toString()) {
            presigned.headers.forEach { key, values ->
                headers.appendAll(key,values)
            }
            setBody(imageBytes)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Failed to upload to S3: ${response.status}")
        }
    }


    // TODO: TO BE DELETED => NO DIRECT FETCH FROM S3, ONLY VIA DB-PFP-KEY
    suspend fun getProfilePicUrl(userId: String): String? {
        val key = "profilePicture/$userId/$userId.jpg"
        val s3 = S3Client.fromEnvironment {  }

        return try {


            // If object exists
            val unsignedGetObjectRequest = GetObjectRequest {
                bucket = bucketName
                this.key = key
            }

            val presigned = s3.presignGetObject(
                input = unsignedGetObjectRequest,
                duration = 24.hours
            )

            presigned.url.toString()
        } catch (e: NoSuchKey) {
            null
        } catch (e: NotFound) {
            null
        } catch (e: S3Exception) {
            println("S3 exception: ${e.message}")
            null
        } catch (e: Exception) {
            println("S3 error: ${e.message}")
            null
        }
    }

    suspend fun deleteProfilePic(userId: String) {
        val key = "profilePicture/$userId/$userId.jpg"
        val s3 = S3Client.fromEnvironment {  }

        try {
            s3.deleteObject(DeleteObjectRequest {
                bucket = bucketName
                this.key = key
            })
        } catch (e: S3Exception) {
            println("S3 delete exception: ${e.message}")
        }
    }

}
