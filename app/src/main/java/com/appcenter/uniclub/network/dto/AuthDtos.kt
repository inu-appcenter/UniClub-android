package com.appcenter.uniclub.network.dto

// 회원가입 요청: POST /api/v1/auth/register
data class RegisterRequestDto(
    val studentId: String,
    val password: String,
    val name: String,
    val major: String,
    val agreed: Boolean
)

// 재학생 인증 요청: POST /api/v1/auth/register/student-verification
data class StudentVerificationRequestDto(
    val studentId: String,
    val password: String
)

// 재학생 인증 응답
data class StudentVerificationResponseDto(
    val verification: Boolean
)

// 로그인 요청: POST /api/v1/auth/login
data class LoginRequestDto(
    val studentId: String,
    val password: String
)

// 로그인 응답: (스크린샷 기준) userId, accessToken, tokenType, expiresIn
data class LoginResponseDto(
    val userId: Long,        // int64 → Kotlin에선 Long 권장
    val accessToken: String,
    val tokenType: String,   // 예: "Bearer"
    val expiresIn: Long      // 초(가정). 서버 문서에 단위 명시돼 있으면 맞춰서 사용
)

// 내 정보 수정: PATCH /api/v1/users/me  (예시엔 major만)
data class UpdateMeRequestDto(
    val major: String
)
