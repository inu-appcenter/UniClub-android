package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.MainPageClubResponseDto
import com.appcenter.uniclub.network.dto.MainPageMediaResponseDto
import retrofit2.http.GET

interface MainService {
    //메인페이지 배너 조회
    @GET("/api/v1/main/banner")
    suspend fun getMainBanner(): List<MainPageMediaResponseDto>

    //메인페이지 추천 동아리 목록 조회
    @GET("/api/v1/main/clubs")
    suspend fun getMainClubs(): List<MainPageClubResponseDto>
}