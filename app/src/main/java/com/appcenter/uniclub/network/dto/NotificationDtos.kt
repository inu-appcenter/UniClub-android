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
    val title: String,
    val message: String,
    val read: Boolean,
    val notificationType: String,
    val targetId: Long,
    val createdAt: String
)

//페이지 응답
data class NotificationPageResponseDto(
    val notifications: List<NotificationResponseDto>,
    val currentPage: Int, //현재 페이지
    val totalPages: Int, //총 페이지
    val totalElements: Long, //총 갯수
    val hasNext: Boolean //다음 페이지 여부
)