package com.appcenter.uniclub.model

enum class NotificationType {
    CLUB, QNA, FEDERATION, SYSTEM, PERSONAL
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType,
    val targetId: Long
)