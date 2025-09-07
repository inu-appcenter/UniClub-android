package com.appcenter.uniclub.network.dto

import com.appcenter.uniclub.model.Club
import com.appcenter.uniclub.model.ClubCategory

//동아리 조회 응답 DTO
data class ClubResponseDto(
    val id: Long,
    val name: String,
    val info: String,
    val status: String, //"SCHEDULED", "ACTIVE", "CLOSED"
    val favorite: Boolean,
    val category: String, //"LIBERAL_ACADEMIC", "HOBBY_EXHIBITION", "SPORTS", "RELIGION", "VOLUNTEER", "CULTURE"
    val clubProfileUrl: String?
)

//페이징 응답 DTO
data class PageClubResponseDto(
    val content: List<ClubResponseDto>, //조회된 동아리 목록
    val hasNext: Boolean //다음페이지 존재 여부
)

fun ClubResponseDto.toClub(): Club {
    return Club(
        id = id,
        name = name,
        info = info,
        status = status,
        favorite = favorite,
        category = ClubCategory.valueOf(category),
        profileUrl = clubProfileUrl
    )
}

//관심동아리 토글 응답 DTO
data class ToggleFavoriteResponseDto(
    val message: String
)

//동아리 미디어 업로드 요청 DTO
data class ClubMediaUploadRequestDto(
    val mediaLink: String,
    val mediaType: String, //"MAIN_PAGE", "CLUB_PROMOTION", "CLUB_PROFILE", "CLUB_BACKGROUND"
    val main: Boolean //대표이미지 여부
)