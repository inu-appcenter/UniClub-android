package com.appcenter.uniclub.network.dto

//회원가입 요청: POST /api/v1/auth/register
data class RegisterRequestDto(
    val studentId: String,
    val password: String,
    val name: String,
    val major: String,
    val agreed: Boolean,
    val studentVerification: Boolean
)

//재학생 인증 요청: POST /api/v1/auth/register/student-verification
data class StudentVerificationRequestDto(
    val studentId: String,
    val password: String
)

//재학생 인증 응답
data class StudentVerificationResponseDto(
    val verification: Boolean
)

//로그인 요청: POST /api/v1/auth/login
data class LoginRequestDto(
    val studentId: String,
    val password: String
)

//로그인 응답
data class LoginResponseDto(
    val userId: Long,
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long
)

//내 정보 수정: PATCH /api/v1/users/me
data class UpdateMeRequestDto(
    val name: String,
    val major: String,
    val nickname: String
)

// 알림 조회 응답
data class NotificationSettingResponseDto(
    val notificationEnabled: Boolean
)

// 알림 토글 응답
data class ToggleNotificationResponseDto(
    val message: String
)