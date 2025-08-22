package com.appcenter.uniclub.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// 매 요청마다 Authorization 헤더를 자동으로 붙여주는 인터셉터
class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenProvider()

        // student-verification 엔드포인트는 Authorization 헤더 제외
        val isStudentVerification = original.url.encodedPath.contains("student-verification")

        val req = if (!token.isNullOrBlank() && !isStudentVerification) {
            original.newBuilder()
                .addHeader("Authorization", token) // "Bearer xxx" 그대로 사용
                .build()
        } else {
            original
        }

        return chain.proceed(req)
    }

}

object ApiClient {
    private const val BASE_URL = "https://uniclub-server.inuappcenter.kr/" // 슬래시 필수

    fun createRetrofit(getAuthHeader: () -> String?): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(getAuthHeader))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}
