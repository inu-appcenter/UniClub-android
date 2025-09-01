package com.appcenter.uniclub.network.dto

import com.appcenter.uniclub.model.Club
import com.appcenter.uniclub.model.ClubCategory

data class ClubResponseDto(
    val id: Long,
    val name: String,
    val info: String,
    val status: String, // "SCHEDULED", "ACTIVE", "CLOSED"
    val favorite: Boolean,
    val category: String, // "LIBERAL_ACADEMIC", "HOBBY_EXHIBITION", "SPORTS", "RELIGION", "VOLUNTEER", "CULTURE"
    val clubProfileUrl: String?
)

data class PageClubResponseDto(
    val content: List<ClubResponseDto>,
    val hasNext: Boolean
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

data class ToggleFavoriteResponseDto(
    val message: String
)

data class ClubMediaUploadRequestDto(
    val mediaLink: String,
    val mediaType: String,
    val main: Boolean
)