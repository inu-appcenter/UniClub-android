package com.appcenter.uniclub.ui.notification

enum class NotificationType {
    INTERESTED_CLUB, //관심 동아리
    ANSWER_RECEIVED, //질문 답변
    NOTICE //총동연 공지
}

//알림 데이터 모델
data class NotificationItem(
    val id: String,
    val title: String, //알림 내용
    val description: String = "", //추가 설명
    val time: String,
    val isRead: Boolean, //읽음 여부
    val type: NotificationType //알림 종류
)

//샘플 데이터
val dummyNotifications = listOf(
    NotificationItem("1", "Appcenter 동아리가 곧 지원마감해요!", "지원하기", "30분 전", false, NotificationType.INTERESTED_CLUB),
    NotificationItem("2", "질문에 답변이 도착했어요.", "", "30분 전", false, NotificationType.ANSWER_RECEIVED),
    NotificationItem("3", "간식나눔 오후6시에 진행합니다.", "", "30분 전", true, NotificationType.NOTICE),
    NotificationItem("5", "Appcenter 동아리가 곧 지원마감해요!", "지원하기", "30분 전", false, NotificationType.INTERESTED_CLUB),
    NotificationItem("6", "질문에 답변이 도착했어요.", "", "30분 전", false, NotificationType.ANSWER_RECEIVED),
    NotificationItem("7", "간식나눔 오후6시에 진행합니다.", "", "30분 전", false, NotificationType.NOTICE),
    NotificationItem("8", "Appcenter 동아리가 곧 지원마감해요!", "지원하기", "30분 전", false, NotificationType.INTERESTED_CLUB)
)
