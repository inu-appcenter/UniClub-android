package com.appcenter.uniclub.model

enum class NotificationType {
    CLUB, QNA, FEDERATION, SYSTEM, PERSONAL
}

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType,
    val targetId: Long
)

////샘플 데이터
//val dummyNotifications = listOf(
//    NotificationItem("1", "Appcenter 동아리가 곧 지원마감해요!", "지원하기", "30분 전", false, NotificationType.CLUB),
//    NotificationItem("2", "질문에 답변이 도착했어요.", "", "30분 전", false, NotificationType.QNA),
//    NotificationItem("3", "간식나눔 오후6시에 진행합니다.", "", "30분 전", true, NotificationType.FEDERATION),
//    NotificationItem("5", "Appcenter 동아리가 곧 지원마감해요!", "지원하기", "30분 전", false, NotificationType.CLUB),
//    NotificationItem("6", "질문에 답변이 도착했어요.", "", "30분 전", false, NotificationType.QNA),
//    NotificationItem("7", "간식나눔 오후6시에 진행합니다.", "", "30분 전", false, NotificationType.SYSTEM),
//    NotificationItem("8", "Appcenter 동아리가 곧 지원마감해요!", "지원하기", "30분 전", false, NotificationType.CLUB)
//)
