package com.appcenter.uniclub.data

import com.appcenter.uniclub.model.NotificationItem
import com.appcenter.uniclub.model.NotificationType
import com.appcenter.uniclub.network.NotificationService
import com.appcenter.uniclub.network.dto.NotificationPageResponseDto
import com.appcenter.uniclub.network.dto.NotificationResponseDto
import com.appcenter.uniclub.util.TimeUtils

class NotificationRepository(private val service: NotificationService){
    //알림 설정
    suspend fun getNotificationSetting() = service.getNotificationSetting()
    suspend fun toggleNotification() = service.toggleNotification()

    //알림 페이지 조회
    suspend fun getNotifications(
        page: Int = 0,
        size: Int = 10,
        sort: String = "createdAt,DESC",
        isRead: Boolean? = null
    ): Result<NotificationPage> = runCatching {
        val dto = service.getNotifications(page, size, sort, isRead)
        dto.toDomain()
    }

    //개별 알림 읽음
    suspend fun markAsRead(notificationId: Long): Result<Unit> =
        runCatching { service.markAsRead(notificationId) }

    //전체 읽음 처리
    suspend fun markAllAsRead(): Result<Unit> =
        runCatching { service.markAllAsRead() }

    //개별 알림 삭제
    suspend fun deleteNotification(notificationId: Long): Result<Unit> =
        runCatching { service.deleteNotification(notificationId) }

    //전체 삭제 처리
    suspend fun deleteAllNotifications(): Result<Unit> =
        runCatching { service.deleteAllNotifications() }
}

data class NotificationPage(
    val items: List<NotificationItem>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean
)

private fun NotificationPageResponseDto.toDomain(): NotificationPage =
    NotificationPage(
        items = notifications.map { it.toDomainItem() },
        currentPage = currentPage,
        totalPages = totalPages,
        totalElements = totalElements,
        hasNext = hasNext
    )

private fun NotificationResponseDto.toDomainItem(): NotificationItem {
    return NotificationItem(
        id = notificationId.toString(),
        title = title,
        message = message,
        time = TimeUtils.toRelativeTime(createdAt),
        isRead = read,
        type = notificationType.toNotificationType(),
        targetId = targetId
    )
}

private fun String.toNotificationType(): NotificationType = when (this.uppercase()) {
    "CLUB" -> NotificationType.CLUB
    "QNA" -> NotificationType.QNA
    "FEDERATION" -> NotificationType.FEDERATION
    "SYSTEM" -> NotificationType.SYSTEM
    "PERSONAL" -> NotificationType.PERSONAL
    else -> NotificationType.SYSTEM // 안전망
}