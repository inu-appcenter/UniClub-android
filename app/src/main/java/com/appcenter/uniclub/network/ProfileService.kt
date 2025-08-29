package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.S3PresignedRequestDto
import com.appcenter.uniclub.network.dto.S3PresignedResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileService {
    @POST("/api/v1/user/profile/s3-presigned")
    suspend fun getPresignedUrl(
        @Body request: S3PresignedRequestDto
    ): S3PresignedResponseDto
}