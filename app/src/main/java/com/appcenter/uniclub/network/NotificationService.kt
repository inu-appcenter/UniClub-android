package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.NotificationPageResponseDto
import com.appcenter.uniclub.network.dto.NotificationSettingResponseDto
import com.appcenter.uniclub.network.dto.ToggleNotificationResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationService {
    //알림 설정
    @GET("/api/v1/users/notification")
    suspend fun getNotificationSetting(): NotificationSettingResponseDto

    @PATCH("/api/v1/users/notification")
    suspend fun toggleNotification(): ToggleNotificationResponseDto

    //알림 목록 조회
    @GET("/api/v1/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "createdAt, DESC",
        @Query("isRead") isRead: Boolean? = null
    ): NotificationPageResponseDto

    //개별 알림 읽음
    @PATCH("/api/v1/notifications/{notificationId}/read")
    suspend fun markAsRead(
        @Path("notificationId") notificationId: Long
    )

    //전체 읽음 처리
    @PATCH("/api/v1/notifications/read-all")
    suspend fun markAllAsRead()

    //개별 알림 삭제
    @DELETE("/api/v1/notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: Long
    )

    //전체 삭제 처리
    @DELETE("/api/v1/notifications")
    suspend fun deleteAllNotifications()
}