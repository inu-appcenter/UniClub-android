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

            if (!res.isSuccessful) {
                // HTTP 에러는 그대로 실패 처리
                val code = res.code()
                error(
                    when (code) {
                        400 -> "요청 형식이 올바르지 않습니다."
                        401, 403 -> "학번/비밀번호가 올바르지 않습니다."
                        404 -> "계정을 찾을 수 없습니다."
                        else -> "인증 실패($code)"
                    }
                )
            }

            val body = res.body() ?: error("인증 응답이 비어 있습니다.")
            if (!body.verification) {
                // 200이라도 비즈니스 실패면 반드시 실패로 전환
                error("학번/비밀번호가 올바르지 않습니다.")
            }
            // verification==true 이면 성공(Unit 반환)
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