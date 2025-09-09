package com.appcenter.uniclub.di

import com.appcenter.uniclub.App
import com.appcenter.uniclub.data.ClubRepository
import com.appcenter.uniclub.data.MainRepository
import com.appcenter.uniclub.data.NotificationRepository
import com.appcenter.uniclub.data.SearchRepository
import com.appcenter.uniclub.data.UserRepository
import com.appcenter.uniclub.network.ApiClient
import com.appcenter.uniclub.network.ClubService
import com.appcenter.uniclub.network.UserService
import com.appcenter.uniclub.network.MainService
import com.appcenter.uniclub.network.NotificationService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

/*
-앱 전역에서 사용할 service(api 인터페이스)와 repository 인스턴스를 생성/제공하는 역할
-의존성 주입의 일종으로 간단히 객체 생성을 관리하는 패턴
-모든 service는 ApiClient.createRetrofit()으로 retrofit 인스턴스를 생성하고,
 DataStore에 저장된 토큰을 Authorization 헤더로 붙여 api 호출이 가능하도록 설정됨
 */
//ServiceLocator = Retrofit Service + Repository 전역에서 쉽게 가져올 수 있게 해주는 DI 도구
object ServiceLocator {
    private fun s3OkHttp(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) //서버 연결까지 최대 30초
            .readTimeout(60, TimeUnit.SECONDS) //응답 읽기 최대 60초
            .writeTimeout(60, TimeUnit.SECONDS) //요청 쓰기 최대 60초
            .build()

    //UserRepository 인스턴스 생성
    //UserService, ProfileRepository, TokenStore 종합 관리
    fun userRepository(app: App): UserRepository {
        val retrofit = ApiClient.createRetrofit(
            getAuthHeader = { runBlocking { app.tokenStore.authHeaderFlow.first() } },
            onUnauthorized = {
                runBlocking { app.tokenStore.clear() }
                app.logoutEvent.tryEmit(Unit)
            }
        )
        val userService = retrofit.create(UserService::class.java)

        return UserRepository(
            service = userService,
            tokenStore = app.tokenStore
        )
    }

    //MainService, MainRepository 인스턴스 생성
    fun mainService(app: App): MainService {
        val retrofit = ApiClient.createRetrofit(
            getAuthHeader = { runBlocking { app.tokenStore.authHeaderFlow.first() } },
            onUnauthorized = {
                runBlocking { app.tokenStore.clear() }
                app.logoutEvent.tryEmit(Unit) // 로그아웃 이벤트 발생
            }
        )
        return retrofit.create(MainService::class.java)
    }
    fun mainRepository(app: App): MainRepository {
        return MainRepository(mainService(app))
    }

    //ClubService, ClubRepository 인스턴스 생성
    fun clubService(app: App): ClubService {
        val retrofit = ApiClient.createRetrofit(
            getAuthHeader = { runBlocking { app.tokenStore.authHeaderFlow.first() } },
            onUnauthorized = {
                runBlocking { app.tokenStore.clear() }
                app.logoutEvent.tryEmit(Unit) // 로그아웃 이벤트 발생
            }
        )
        return retrofit.create(ClubService::class.java)
    }
    fun clubRepository(app: App): ClubRepository =
        ClubRepository(
            service = clubService(app),
            okHttp = s3OkHttp(),
            app = app
        )

    //NotificationRepository 인스턴스 생성
    fun notificationRepository(app: App): NotificationRepository {
        val retrofit = ApiClient.createRetrofit(
            getAuthHeader = { runBlocking { app.tokenStore.authHeaderFlow.first() } },
            onUnauthorized = {
                runBlocking { app.tokenStore.clear() }
                app.logoutEvent.tryEmit(Unit) // 로그아웃 이벤트 발생
            }
        )
        val api = retrofit.create(NotificationService::class.java)
        return NotificationRepository(api)
    }

    //SearchRepository 인스턴스 생성
    fun searchRepository(app: App): SearchRepository {
        val retrofit = ApiClient.createRetrofit(
            getAuthHeader = { runBlocking { app.tokenStore.authHeaderFlow.first() } },
            onUnauthorized = {
                runBlocking { app.tokenStore.clear() }
                app.logoutEvent.tryEmit(Unit) // 로그아웃 이벤트 발생
            }
        )
        val api = retrofit.create(ClubService::class.java) //ClubService 활용
        return SearchRepository(api)
    }
}
