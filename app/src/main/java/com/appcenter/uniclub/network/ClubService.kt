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
    //동아리 조회
    @GET("/api/v1/clubs")
    suspend fun getClubs(
        @Query("category") category: String? = null,
        @Query("sortBy") sortBy: String? = "name",
        @Query("cursorName") cursorName: String? = null,
        @Query("size") size: Int = 10
    ): PageClubResponseDto

    //관심동아리 등록/취소
    @POST("/api/v1/clubs/{clubId}/favorite")
    suspend fun toggleFavorite(
        @Path("clubId") clubId: Long
    ): ToggleFavoriteResponseDto

    //동아리 검색
    @GET("/api/v1/search")
    suspend fun searchClubs(
        @Query("keyword") keyword: String
    ): List<ClubResponseDto>

    //동아리 홍보페이지 조회
    @GET("/api/v1/clubs/{clubId}")
    suspend fun getClubPromotion(
        @Path("clubId") clubId: Long
    ): ClubPromotionResponseDto

    //동아리 홍보페이지 작성 및 수정
    @PUT("/api/v1/clubs/{clubId}")
    suspend fun upsertClubPromotion(
        @Path("clubId") clubId: Long,
        @Body body: ClubPromotionRegisterRequestDto
    ): Response<Unit>

    //동아리 S3 presigned url
    @POST("/api/v1/club/{clubId}/s3-presigned")
    suspend fun createPresignedUrl(
        @Path("clubId") clubId: Long,
        @Body body: List<S3PresignedRequestDto>
    ): List<S3PresignedResponseDto>

    //동아리 미디어 업로드
    @POST("/api/v1/clubs/{clubId}/upload")
    suspend fun registerClubMedia(
        @Path("clubId") clubId: Long,
        @Body body: List<ClubMediaUploadRequestDto>
    ): Unit
}