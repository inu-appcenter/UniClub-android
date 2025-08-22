package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.MainPageMediaResponseDto
import retrofit2.http.GET

interface MainService {
    @GET("/api/v1/main/banner")
    suspend fun getMainBanner(): List<MainPageMediaResponseDto>
}