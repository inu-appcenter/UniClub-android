package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//알림설정 조회 응답
@Keep
data class NotificationSettingResponseDto(
    @field:SerializedName("notificationEnabled") val notificationEnabled: Boolean
)

//알림설정 토글 응답
@Keep
data class ToggleNotificationResponseDto(
    @field:SerializedName("message") val message: String
)

//알림 조회 응답
@Keep
data class NotificationResponseDto(
    @field:SerializedName("notificationId") val notificationId: Long,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("message") val message: String,
    @field:SerializedName("read") val read: Boolean,
    @field:SerializedName("notificationType") val notificationType: String,
    @field:SerializedName("targetId") val targetId: Long,
    @field:SerializedName("createdAt") val createdAt: String
)

//페이지 응답
@Keep
data class NotificationPageResponseDto(
    @field:SerializedName("notifications") val notifications: List<NotificationResponseDto>,
    @field:SerializedName("currentPage") val currentPage: Int, //현재 페이지
    @field:SerializedName("totalPages") val totalPages: Int, //총 페이지
    @field:SerializedName("totalElements") val totalElements: Long, //총 갯수
    @field:SerializedName("hasNext") val hasNext: Boolean //다음 페이지 여부
)