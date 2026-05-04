package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//메인페이지 배너 응답
@Keep
data class MainPageMediaResponseDto(
    @field:SerializedName("mediaLink") val mediaLink: String,
    @field:SerializedName("mediaType") val mediaType: String
)

//메인페이지 동아리 응답
@Keep
data class MainPageClubResponseDto(
    @field:SerializedName("clubId") val clubId: Long,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("imageUrl") val imageUrl: String? = null,
    @field:SerializedName("favorite") val favorite: Boolean
)