package com.appcenter.uniclub.data

import android.content.Context
import android.net.Uri
import com.appcenter.uniclub.network.UserService
import com.appcenter.uniclub.network.dto.*
import retrofit2.HttpException
import retrofit2.Response

class UserRepository(
    private val service: UserService,
    private val tokenStore: TokenStore,
    private val profileRepo: ProfileRepository
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

    suspend fun saveRegisterTerms(req: RegisterTermsRequestDto): Result<Unit> =
        runCatching {
            val res = service.saveRegisterTerms(req)
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
    suspend fun updateMe(name: String, major: String, nickname: String, profileImageLink: String? = null): Result<Unit> =
        runCatching {
            val res = service.updateMe(UpdateMeRequestDto(name, major, nickname, profileImageLink))
            if (!res.isSuccessful) throw HttpException(res)
            Unit
        }

    suspend fun uploadProfileImage(context: Context, uri: Uri): String {
        return profileRepo.uploadProfileImage(context, uri)
    }

    //내 정보 조회
    suspend fun getMyPage(): Result<MyPageResponseDto> {
        return runCatching {
            service.getMyPage()
        }
    }

    //계정 삭제
    suspend fun deleteUser(password: String): Response<Unit> {
        return service.deleteUser(UserDeleteRequestDto(password))
    }

    // 🔵 개발용: 더미 토큰 저장
    suspend fun saveDummyToken() {
        // 실제 API 인증이 필요한 호출들에서 헤더만 필요하다면 이걸로 충분
        tokenStore.saveAuthHeader("Bearer DEV_DUMMY_TOKEN")
    }
}