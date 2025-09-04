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

        val path = original.url.encodedPath
        val isStudentVerification = path.contains("student-verification")
        val isRegister = path.contains("auth/register")
        val isTerms = path.contains("users/terms")

        val req = if (!token.isNullOrBlank() && !isStudentVerification && !isRegister && !isTerms) {
            original.newBuilder()
                .addHeader("Authorization", token)
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
