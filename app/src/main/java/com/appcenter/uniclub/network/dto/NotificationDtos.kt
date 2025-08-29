package com.appcenter.uniclub.network.dto

// 알림설정 조회 응답
data class NotificationSettingResponseDto(
    val notificationEnabled: Boolean
)

// 알림설정 토글 응답
data class ToggleNotificationResponseDto(
    val message: String
)

//알림 조회 응답
data class NotificationResponseDto(
    val notificationId: Long,
    val message: String,
    val type: String,
    val createdAt: String,
    val isRead: Boolean
)