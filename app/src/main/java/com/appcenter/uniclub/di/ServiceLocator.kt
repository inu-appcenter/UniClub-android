package com.appcenter.uniclub.di

import com.appcenter.uniclub.App
import com.appcenter.uniclub.data.MainRepository
import com.appcenter.uniclub.network.ApiClient
import com.appcenter.uniclub.network.UserService
import com.appcenter.uniclub.network.MainService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.jvm.java

object ServiceLocator {
    // AuthService 인스턴스 생성
    fun authService(app: App): UserService {
        val retrofit = ApiClient.createRetrofit {
            // DataStore에서 현재 Authorization 헤더를 동기 조회
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        return retrofit.create(UserService::class.java)
    }

    fun mainRepository(app: App): MainRepository {
        val retrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        val api = retrofit.create(MainService::class.java)
        return MainRepository(api)
    }
}
