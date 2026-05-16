package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

enum class Role { GUEST, MEMBER, ADMIN, PRESIDENT }

@Keep
data class DescriptionMediaDto(
    @field:SerializedName("mediaId") val mediaId: Long,
    @field:SerializedName("mediaLink") val mediaLink: String,
    @field:SerializedName("mediaType") val mediaType: String,
    @field:SerializedName("updatedAt") val updatedAt: String,
    @field:SerializedName("main") val main: Boolean
)

@Keep
data class ClubPromotionResponseDto(
    @field:SerializedName("role") val role: Role,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("status") val status: String?,
    @field:SerializedName("startTime") val startTime: String?,
    @field:SerializedName("endTime") val endTime: String?,
    @field:SerializedName("simpleDescription") val simpleDescription: String?,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("notice") val notice: String?,
    @field:SerializedName("location") val location: String?,
    @field:SerializedName("presidentName") val presidentName: String?,
    @field:SerializedName("presidentPhone") val presidentPhone: String?,
    @field:SerializedName("youtubeLink") val youtubeLink: String?,
    @field:SerializedName("instagramLink") val instagramLink: String?,
    @field:SerializedName("applicationFormLink") val applicationFormLink: String?,
    @field:SerializedName("favorite") val favorite: Boolean,
    @field:SerializedName("mediaList") val mediaList: List<DescriptionMediaDto>
)

@Keep
data class ClubPromotionRegisterRequestDto(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("status") val status: String?,
    @field:SerializedName("startTime") val startTime: String?,
    @field:SerializedName("endTime") val endTime: String?,
    @field:SerializedName("simpleDescription") val simpleDescription: String?,
    @field:SerializedName("description") val description: String?,
    @field:SerializedName("notice") val notice: String?,
    @field:SerializedName("location") val location: String?,
    @field:SerializedName("presidentName") val presidentName: String?,
    @field:SerializedName("presidentPhone") val presidentPhone: String?,
    @field:SerializedName("youtubeLink") val youtubeLink: String?,
    @field:SerializedName("instagramLink") val instagramLink: String?,
    @field:SerializedName("applicationFormLink") val applicationFormLink: String?
)

@Keep
data class ClubMediaDeleteRequestDto(
    @field:SerializedName("mediaIds") val mediaIds: List<Long>
)