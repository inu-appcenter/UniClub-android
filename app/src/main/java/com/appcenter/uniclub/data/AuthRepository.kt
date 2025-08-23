package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.AuthService
import com.appcenter.uniclub.network.dto.*
import retrofit2.HttpException

class AuthRepository(
    private val service: AuthService,
    private val tokenStore: TokenStore
) {
    //재학생 인증
    suspend fun verifyStudent(id: String, pw: String): Result<Boolean> =
        runCatching {
            val res = service.studentVerification(StudentVerificationRequestDto(id, pw))
            if (!res.isSuccessful) throw HttpException(res)

            val body = res.body() ?: throw Exception("인증 응답이 비어 있습니다.")
            if (!body.verification) throw Exception("학번/비밀번호가 올바르지 않습니다.")

            true
        }


    //회원가입
    suspend fun register(req: RegisterRequestDto): Result<Unit> =
        runCatching {
            val res = service.register(req)
            if (!res.isSuccessful) throw HttpException(res)
            Unit
        }

    //로그인 후 토큰 저장, userId 반환
    suspend fun login(id: String, pw: String): Result<Long> =
        runCatching {
            val res = service.login(LoginRequestDto(id, pw))
            if (!res.isSuccessful) throw HttpException(res)

            val body = res.body() ?: throw Exception("로그인 응답이 비어 있습니다.")
            val header = "${body.tokenType} ${body.accessToken}"
            tokenStore.saveAuthHeader(header)
            body.userId
        }

    //내 정보 수정
    suspend fun updateMe(name: String, major: String, nickname: String): Result<Unit> =
        runCatching {
            val res = service.updateMe(UpdateMeRequestDto(name, major, nickname))
            if (!res.isSuccessful) throw HttpException(res)
            Unit
        }

    //회원 탈퇴
    suspend fun deleteAccount(): Result<Unit> =
        runCatching {
            val res = service.deleteAccount()
            if (!res.isSuccessful) throw HttpException(res)
            tokenStore.clear()
            Unit
        }

    // 🔵 개발용: 더미 토큰 저장
    suspend fun saveDummyToken() {
        // 실제 API 인증이 필요한 호출들에서 헤더만 필요하다면 이걸로 충분
        tokenStore.saveAuthHeader("Bearer DEV_DUMMY_TOKEN")
    }
}