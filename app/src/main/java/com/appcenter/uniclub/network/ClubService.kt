package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.PageClubResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ClubService {
    @GET("/api/v1/clubs")
    suspend fun getClubs(
        @Query("category") category: String? = null,
        @Query("sortBy") sortBy: String? = "name",
        @Query("cursorName") cursorName: String? = null,
        @Query("size") size: Int = 10
    ): PageClubResponseDto
}