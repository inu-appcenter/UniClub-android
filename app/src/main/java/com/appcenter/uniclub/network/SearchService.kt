package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.ClubResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("/api/v1/search")
    suspend fun searchClubs(
        @Query("keyword") keyword: String
    ): List<ClubResponseDto>
}