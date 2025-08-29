package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.NotificationResponseDto
import com.appcenter.uniclub.network.dto.NotificationSettingResponseDto
import com.appcenter.uniclub.network.dto.ToggleNotificationResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface NotificationService {
    @GET("/api/v1/users/notification")
    suspend fun getNotificationSetting(): NotificationSettingResponseDto

    @PATCH("/api/v1/users/notification")
    suspend fun toggleNotification(): ToggleNotificationResponseDto

    @GET("/api/v1/notifications")
    suspend fun getNotifications(): List<NotificationResponseDto>

    //알림 읽음
    @PATCH("/api/v1/notifications/{notificationId}/read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: Long
    )

    //알림 삭제
    @DELETE("/api/v1/notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: Long
    )
}