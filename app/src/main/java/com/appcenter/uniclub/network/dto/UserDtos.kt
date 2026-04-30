package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//회원가입 요청: POST /api/v1/auth/register
@Keep
data class RegisterRequestDto(
    @field:SerializedName("studentId") val studentId: String,
    @field:SerializedName("password") val password: String,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("major") val major: String,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("personalInfoCollectionAgreement") val personalInfoCollectionAgreement: Boolean,
    @field:SerializedName("marketingAdvertisement") val marketingAdvertisement: Boolean
)

//재학생 인증 요청: POST /api/v1/auth/register/student-verification
@Keep
data class StudentVerificationRequestDto(
    @field:SerializedName("studentId") val studentId: String,
    @field:SerializedName("password") val password: String
)

//재학생 인증 응답
@Keep
data class StudentVerificationResponseDto(
    @field:SerializedName("verification") val verification: Boolean
)

//로그인 요청: POST /api/v1/auth/login
@Keep
data class LoginRequestDto(
    @field:SerializedName("studentId") val studentId: String,
    @field:SerializedName("password") val password: String
)

//로그인 응답
@Keep
data class LoginResponseDto(
    @field:SerializedName("userId") val userId: Long,
    @field:SerializedName("accessToken") val accessToken: String,
    @field:SerializedName("tokenType") val tokenType: String,
    @field:SerializedName("expiresIn") val expiresIn: Long
)

//내 정보 수정: PATCH /api/v1/users/me
@Keep
data class UpdateMeRequestDto(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("major") val major: String,
    @field:SerializedName("nickname") val nickname: String,
    @field:SerializedName("profileImageLink") val profileImageLink: String? = null
)

//내 정보 조회
@Keep
data class MyPageResponseDto(
    @field:SerializedName("nickname") val nickname: String?,
    @field:SerializedName("name") val name: String,
    @field:SerializedName("studentId") val studentId: String,
    @field:SerializedName("major") val major: String,
    @field:SerializedName("profileImageLink") val profileImageLink: String?
)

//계정 삭제: DELETE /api/v1/users
@Keep
data class UserDeleteRequestDto(
    @field:SerializedName("password") val password: String
)