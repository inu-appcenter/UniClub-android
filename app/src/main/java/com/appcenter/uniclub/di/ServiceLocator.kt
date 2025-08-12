package com.appcenter.uniclub.di

import com.appcenter.uniclub.App
import com.appcenter.uniclub.network.ApiClient
import com.appcenter.uniclub.network.AuthService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object ServiceLocator {
    // AuthService 인스턴스 생성
    fun authService(app: App): AuthService {
        val retrofit = ApiClient.createRetrofit {
            // DataStore에서 현재 Authorization 헤더를 동기 조회
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        return retrofit.create(AuthService::class.java)
    }
}
