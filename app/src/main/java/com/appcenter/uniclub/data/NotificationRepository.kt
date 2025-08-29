package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.NotificationService
import com.appcenter.uniclub.network.dto.NotificationResponseDto

class NotificationRepository(private val service: NotificationService){
    suspend fun getNotificationSetting() = service.getNotificationSetting()

    suspend fun toggleNotification() = service.toggleNotification()

    suspend fun getNotifications(): Result<List<NotificationResponseDto>> {
        return runCatching { service.getNotifications() }
    }

    suspend fun markAsRead(notificationId: Long): Result<Unit> {
        return runCatching { service.markAsRead(notificationId) }
    }

    suspend fun deleteNotification(notificationId: Long): Result<Unit> {
        return runCatching { service.deleteNotification(notificationId) }
    }
}
