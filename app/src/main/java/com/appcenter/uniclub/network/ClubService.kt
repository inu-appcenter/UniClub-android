package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.ClubPromotionRegisterRequestDto
import com.appcenter.uniclub.network.dto.ClubPromotionResponseDto
import com.appcenter.uniclub.network.dto.ClubResponseDto
import com.appcenter.uniclub.network.dto.ClubMediaUploadRequestDto
import com.appcenter.uniclub.network.dto.PageClubResponseDto
import com.appcenter.uniclub.network.dto.S3PresignedRequestDto
import com.appcenter.uniclub.network.dto.S3PresignedResponseDto
import com.appcenter.uniclub.network.dto.ToggleFavoriteResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @GET("/api/v1/clubs/{clubId}")
    suspend fun getClubPromotion(
        @Path("clubId") clubId: Long
    ): ClubPromotionResponseDto

    @PUT("/api/v1/clubs/{clubId}")
    suspend fun upsertClubPromotion(
        @Path("clubId") clubId: Long,
        @Body body: ClubPromotionRegisterRequestDto
    ): Response<Unit>

    @POST("/api/v1/club/{clubId}/s3-presigned")
    suspend fun createPresignedUrl(
        @Path("clubId") clubId: Long,
        @Body body: List<S3PresignedRequestDto> // 한 번에 여러 개도 가능하면 List로
    ): List<S3PresignedResponseDto>

    // S3 업로드 후, 서버에 media 정보 등록
    @POST("/api/v1/clubs/{clubId}/upload")
    suspend fun registerClubMedia(
        @Path("clubId") clubId: Long,
        @Body body: List<ClubMediaUploadRequestDto>
    ): Unit
}