package com.appcenter.uniclub.network.dto

//회원가입 요청: POST /api/v1/auth/register
data class RegisterRequestDto(
    val studentId: String,
    val password: String,
    val name: String,
    val major: String,
    val personalInfoCollectionAgreement: Boolean,
    val marketingAdvertisement: Boolean,
    val studentVerification: Boolean
)

//개인정보 약관 동의 정보 저장 요청: POST /api/v1/users/terms
data class RegisterTermsRequestDto(
    val studentId: String,
    val personalInfoCollectionAgreement: Boolean,
    val marketingAdvertisement: Boolean
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
    val nickname: String,
    val profileImageLink: String? = null
)

//내 정보 조회
data class MyPageResponseDto(
    val nickname: String?,
    val name: String,
    val studentId: String,
    val major: String,
    val profileImageLink: String?
)

//계정 삭제: DELETE /api/v1/users
data class UserDeleteRequestDto(
    val password: String
)