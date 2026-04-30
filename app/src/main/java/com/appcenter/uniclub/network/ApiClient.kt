package com.appcenter.uniclub.network

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/*
-매 요청마다 Authorization 헤더를 자동으로 추가하는 역할
-tokenProvider: 현재 저장된 인증 토큰("Bearer xxx")을 반환하는 함수
                (TokenStore.getAuthHeader()를 ServiceLocator에서 주입받음)
*/
class AuthInterceptor(
    private val tokenProvider: () -> String?,
    private val onUnauthorized: () -> Unit //401 발생 시 처리할 콜백
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request() //원래 요청 객체
        val token = tokenProvider() //저장된 토큰 가져오기
        val path = original.url.encodedPath //요청 url 경로

        //예외처리: 특정 요청에는 헤더를 붙이지 않음
        val isNoAuthEndpoint =
            path.startsWith("/api/v1/auth/login") ||
                    path.startsWith("/api/v1/auth/register") ||
                    path.startsWith("/api/v1/users/terms") ||
                    path.startsWith("/api/v1/user/profile/s3-presigned")


        val shouldAttachAuth = !token.isNullOrBlank() && !isNoAuthEndpoint
        Log.d("AuthInterceptor", "path=$path tokenExists=${!token.isNullOrBlank()} attachAuth=$shouldAttachAuth")

        val req = if (shouldAttachAuth) {
            original.newBuilder()
                .header("Authorization", token!!)
                .build()
        } else {
            original
        }

        val response = chain.proceed(req)

        //401이면 토큰 삭제 (token 있을 때만)
        val isDeleteAccountRequest =
            req.method == "DELETE" && req.url.encodedPath == "/api/v1/users"

        if (response.code == 401 && !token.isNullOrBlank() && !isDeleteAccountRequest) {
            runBlocking { onUnauthorized() }
        }

        return response
    }
}

//Retrofit 인스턴스를 생성하는 팩토리 객체
object ApiClient {
    private const val BASE_URL = "https://uniclub-server.inuappcenter.kr/" //서버 기본 URL

    /*
    -Retrofit 생성 메서드
    -getAuthHeader: 현재 Autuhorization 헤더를 반환하는 함수
    */
    fun createRetrofit(getAuthHeader: () -> String?, onUnauthorized: () -> Unit): Retrofit {
        //HTTP 로깅 인터셉터: 요청/응답 본문까지 로그 출력
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        //OkHttpClient 빌더
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(getAuthHeader, onUnauthorized)) //Authorization 헤더 자동 추가
            .addInterceptor(logging) //네트워크 요청/응답 로그 출력
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL) //API 서버 주소
            .addConverterFactory(GsonConverterFactory.create()) //JSON <-> 객체 변환
            .client(client) //위에서 만든 OkHttpClient 사용
            .build()
    }
}
