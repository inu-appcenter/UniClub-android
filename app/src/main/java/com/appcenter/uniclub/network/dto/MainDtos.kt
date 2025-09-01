package com.appcenter.uniclub.network.dto

//메인페이지 배너 응답
data class MainPageMediaResponseDto(
    val mediaLink: String,
    val mediaType: String
)

// 메인페이지 동아리 응답
data class MainPageClubResponseDto(
    val clubId: Long,
    val name: String,
    val imageUrl: String,
    val favorite: Boolean
)