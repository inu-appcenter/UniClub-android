package com.appcenter.uniclub.model

//모집 상태를 표현하는 enum 클래스
enum class RecruitStatus {
    RECRUITING, // 모집중
    UPCOMING,   // 모집예정
    CLOSED      // 모집마감
}

//동아리 하나를 표현하는 데이터 클래스
data class Club(
    val name: String, //동아리 이름
    val description: String, //추가 정보
    val isRecruiting: RecruitStatus, //모집 상태
    val isLiked: Boolean, //좋아요 여부
    val imageResId: Int? = null //프로필 이미지 (drawable 리소스 ID)
)
