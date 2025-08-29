package com.appcenter.uniclub.network

import com.appcenter.uniclub.network.dto.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {

    //회원가입
    @POST("api/v1/auth/register")
    suspend fun register(
        @Body body: RegisterRequestDto
    ): Response<Unit>

    // 재학생 인증
    @POST("api/v1/auth/register/student-verification")
    suspend fun studentVerification(
        @Body body: StudentVerificationRequestDto
    ): Response<StudentVerificationResponseDto>

    // 로그인
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): Response<LoginResponseDto>

    // 회원 탈퇴
    @DELETE("api/v1/users")
    suspend fun deleteAccount(): Response<Unit>

    // 내 정보 수정 (예: 전공만 수정)
    @PATCH("api/v1/users/me")
    suspend fun updateMe(
        @Body body: UpdateMeRequestDto
    ): Response<Unit>

    //내 정보 조회
    @GET("/api/v1/users/me")
    suspend fun getMyPage(): MyPageResponseDto

    //계정 삭제
    @DELETE("api/v1/users")
    suspend fun deleteUser(
        @Body request: UserDeleteRequestDto
    ): Response<Unit>
}
