package com.appcenter.uniclub.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.appcenter.uniclub.network.UserService
import com.appcenter.uniclub.network.dto.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Protocol

class UserRepository(
    private val service: UserService,
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
            Log.d("UserRepository", "updateMe profileImageLink = $profileImageLink")
            val res = service.updateMe(UpdateMeRequestDto(name, major, nickname, profileImageLink))
            if (!res.isSuccessful) throw HttpException(res)
            Unit
        }

    suspend fun uploadProfileImage(context: Context, uri: Uri): String = withContext(Dispatchers.IO) {
        Log.d("UserRepository", "uploadProfileImage 호출됨, uri=$uri")

        val filename = "profile_${System.currentTimeMillis()}.jpg"

        val presigned = service.getPresignedUrl(S3PresignedRequestDto(filename))
        Log.d("UserRepository", "presignedUrl = ${presigned.presignedUrl}")

        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("파일 읽기 실패: InputStream null")
        val bytes = inputStream.use { it.readBytes() }
        Log.d("UserRepository", "파일 읽기 완료, size=${bytes.size}")

        val client = OkHttpClient.Builder()
            .protocols(listOf(Protocol.HTTP_1_1)) // ← HTTP1.1 강제
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"
        val request = Request.Builder()
            .url(presigned.presignedUrl)
            .put(bytes.toRequestBody(mimeType.toMediaType()))
            .build()

        Log.d("UserRepository", "PUT 요청 전송 준비됨: ${request.method} ${request.url}")

        try {
            val response = client.newCall(request).execute()
            Log.d("UserRepository", "S3 업로드 응답: code=${response.code}, msg=${response.message}")
            if (!response.isSuccessful) throw RuntimeException("S3 업로드 실패, code=${response.code}")
        } catch (e: Exception) {
            Log.e("UserRepository", "S3 업로드 중 오류 발생", e)
            throw e
        }

        val cleanPath = presigned.presignedUrl
            .substringAfter("uploads/")
            .substringBefore("?")
        val finalPath = "uploads/$cleanPath"

        Log.d("UserRepository", "uploadProfileImage finalPath = $finalPath")
        finalPath
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
}