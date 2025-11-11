package com.appcenter.uniclub.data

import com.appcenter.uniclub.model.NotificationItem
import com.appcenter.uniclub.model.NotificationType
import com.appcenter.uniclub.network.NotificationService
import com.appcenter.uniclub.network.dto.NotificationPageResponseDto
import com.appcenter.uniclub.network.dto.NotificationResponseDto
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        title = title.ifBlank { message },
        description = message,
        time = createdAt.toRelativeTimeString(),
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

// 서버 createdAt("yyyy-MM-dd'T'HH:mm:ss") → "n분 전 / n시간 전 / n일 전" 변환
private val serverDateFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

private fun String.toRelativeTimeString(): String = runCatching {
    val serverTime = LocalDateTime.parse(this, serverDateFormatter)
    val now = LocalDateTime.now(ZoneId.systemDefault())
    val diff = Duration.between(serverTime, now)

    val minutes = diff.toMinutes()
    val hours = diff.toHours()
    val days = diff.toDays()

    when {
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        hours < 24 -> "${hours}시간 전"
        days < 7 -> "${days}일 전"
        else -> this.replace('T', ' ') // 오래됐으면 원문 표기
    }
}.getOrElse { this.replace('T', ' ') }
