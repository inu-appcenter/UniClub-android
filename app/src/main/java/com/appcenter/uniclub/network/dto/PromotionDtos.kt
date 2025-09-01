package com.appcenter.uniclub.network.dto

enum class Role { GUEST, MEMBER, ADMIN, PRESIDENT }

data class DescriptionMediaDto(
    val mediaLink: String,
    val mediaType: String,     // "MAIN_PAGE", "CLUB_PROMOTION", ...
    val updatedAt: String,     // ISO string 그대로 사용 (필요시 나중에 LocalDateTime 파싱)
    val main: Boolean
)

data class ClubPromotionResponseDto(
    val role: Role,            // 서버에서 "ADMIN"/"PRESIDENT"/... 로 내려옴
    val name: String,
    val status: String,
    val startTime: String,
    val endTime: String,
    val simpleDescription: String,
    val description: String,
    val notice: String,
    val location: String,
    val presidentName: String,
    val presidentPhone: String,
    val youtubeLink: String?,
    val instagramLink: String?,
    val applicationFormLink: String?,
    val favorite: Boolean,
    val mediaList: List<DescriptionMediaDto>
)

data class ClubPromotionRegisterRequestDto(
    val name: String,
    val status: String,
    val startTime: String,
    val endTime: String,
    val simpleDescription: String,
    val description: String,
    val notice: String,
    val location: String,
    val presidentName: String,
    val presidentPhone: String,
    val youtubeLink: String?,
    val instagramLink: String?,
    val applicationFormLink: String?
)