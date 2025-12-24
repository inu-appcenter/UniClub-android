package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.FcmRegisterRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmService {

    @POST("/api/v1/fcm/register")
    suspend fun registerFcmToken(
        @Body body: FcmRegisterRequestDto
    ): Response<Unit>
}