package com.appcenter.uniclub.di

import com.appcenter.uniclub.App
import com.appcenter.uniclub.data.ClubRepository
import com.appcenter.uniclub.data.MainRepository
import com.appcenter.uniclub.data.NotificationRepository
import com.appcenter.uniclub.data.ProfileRepository
import com.appcenter.uniclub.data.SearchRepository
import com.appcenter.uniclub.data.UserRepository
import com.appcenter.uniclub.network.ApiClient
import com.appcenter.uniclub.network.ClubService
import com.appcenter.uniclub.network.UserService
import com.appcenter.uniclub.network.MainService
import com.appcenter.uniclub.network.NotificationService
import com.appcenter.uniclub.network.ProfileService
import com.appcenter.uniclub.network.SearchService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.jvm.java

object ServiceLocator {
    // AuthService 인스턴스 생성
    fun userService(app: App): UserService {
        val retrofit = ApiClient.createRetrofit {
            // DataStore에서 현재 Authorization 헤더를 동기 조회
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        return retrofit.create(UserService::class.java)
    }

    fun profileRepository(app: App): ProfileRepository {
        val retrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        val api = retrofit.create(ProfileService::class.java)
        return ProfileRepository(api)
    }

    fun userRepository(app: App): UserRepository {
        val retrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        val userService = retrofit.create(UserService::class.java)

        val profileRetrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        val profileService = profileRetrofit.create(ProfileService::class.java)

        val profileRepo = ProfileRepository(profileService)

        return UserRepository(
            service = userService,
            tokenStore = app.tokenStore,
            profileRepo = profileRepo
        )
    }

    fun mainService(app: App): MainService {
        val retrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        return retrofit.create(MainService::class.java)
    }

    fun mainRepository(app: App): MainRepository {
        return MainRepository(mainService(app))
    }

    fun clubRepository(app: App): ClubRepository {
        val retrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        val api = retrofit.create(ClubService::class.java)
        return ClubRepository(api)
    }

    fun notificationRepository(app: App): NotificationRepository {
        val retrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        val api = retrofit.create(NotificationService::class.java)
        return NotificationRepository(api)
    }

    fun searchRepository(app: App): SearchRepository {
        val retrofit = ApiClient.createRetrofit {
            runBlocking { app.tokenStore.authHeaderFlow.first() }
        }
        val api = retrofit.create(SearchService::class.java)
        return SearchRepository(api) // ← SearchRepository(SearchService) 생성자 형태
    }
}
