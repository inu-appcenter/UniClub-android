package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.appcenter.uniclub.model.Club
import com.appcenter.uniclub.model.ClubCategory
import com.google.gson.annotations.SerializedName

//동아리 조회 응답 DTO
@Keep
data class ClubResponseDto(
    @field:SerializedName("id") val id: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("info") val info: String?,
    @field:SerializedName("status") val status: String?, //"SCHEDULED", "ACTIVE", "CLOSED"
    @field:SerializedName("favorite") val favorite: Boolean,
    @field:SerializedName("category") val category: String, //"LIBERAL_ACADEMIC", ...
    @field:SerializedName("clubProfileUrl") val clubProfileUrl: String?
)

//페이징 응답 DTO
@Keep
data class PageClubResponseDto(
    @field:SerializedName("content") val content: List<ClubResponseDto>, //조회된 동아리 목록
    @field:SerializedName("hasNext") val hasNext: Boolean //다음페이지 존재 여부
)

//DTO → 내부 모델 변환
fun ClubResponseDto.toClub(): Club {
    return Club(
        id = id,
        name = name,
        info = info ?: "",
        status = status ?: "CLOSED",
        favorite = favorite,
        category = ClubCategory.fromServerValue(category)
            ?: throw IllegalArgumentException("Unknown category: $category"),
        profileUrl = clubProfileUrl
    )
}

//관심동아리 토글 응답 DTO
@Keep
data class ToggleFavoriteResponseDto(
    @field:SerializedName("message") val message: String
)

//동아리 미디어 업로드 요청 DTO
@Keep
data class ClubMediaUploadRequestDto(
    @field:SerializedName("mediaLink") val mediaLink: String,
    @field:SerializedName("mediaType") val mediaType: String, //"MAIN_PAGE", ...
    @field:SerializedName("main") val main: Boolean //대표이미지 여부
)