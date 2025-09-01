package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.ClubResponseDto
import com.appcenter.uniclub.network.dto.PageClubResponseDto
import com.appcenter.uniclub.network.dto.ToggleFavoriteResponseDto
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ClubService {
    @GET("/api/v1/clubs")
    suspend fun getClubs(
        @Query("category") category: String? = null,
        @Query("sortBy") sortBy: String? = "name",
        @Query("cursorName") cursorName: String? = null,
        @Query("size") size: Int = 10
    ): PageClubResponseDto

    @POST("/api/v1/clubs/{clubId}/favorite")
    suspend fun toggleFavorite(
        @Path("clubId") clubId: Long
    ): ToggleFavoriteResponseDto

    @GET("/api/v1/search")
    suspend fun searchClubs(
        @Query("keyword") keyword: String
    ): List<ClubResponseDto>
}