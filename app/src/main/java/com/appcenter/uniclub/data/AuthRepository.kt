package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.AuthService
import com.appcenter.uniclub.network.dto.*

class AuthRepository(
    private val service: AuthService,
    private val tokenStore: TokenStore
) {
    // 재학생 인증
    suspend fun verifyStudent(id: String, pw: String): Result<Unit> =
        runCatching {
            val res = service.studentVerification(StudentVerificationRequestDto(id, pw))
            if (!res.isSuccessful) error("verify failed: ${res.code()}")
        }

    // 회원가입
    suspend fun register(req: RegisterRequestDto): Result<Unit> =
        runCatching {
            val res = service.register(req)
            if (!res.isSuccessful) error("register failed: ${res.code()}")
        }

    // 로그인 후 토큰 저장, userId 반환
    suspend fun login(id: String, pw: String): Result<Long> =
        runCatching {
            val res = service.login(LoginRequestDto(id, pw))
            if (!res.isSuccessful) error("login failed: ${res.code()}")
            val body = res.body() ?: error("empty login body")
            val header = "${body.tokenType} ${body.accessToken}"
            tokenStore.saveAuthHeader(header)
            body.userId
        }

    // 내 정보 수정 (전공 변경 예시)
    suspend fun updateMajor(major: String): Result<Unit> =
        runCatching {
            val res = service.updateMe(UpdateMeRequestDto(major))
            if (!res.isSuccessful) error("update failed: ${res.code()}")
        }
}