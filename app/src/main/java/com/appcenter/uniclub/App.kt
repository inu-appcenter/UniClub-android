package com.appcenter.uniclub

import android.app.Application
import com.appcenter.uniclub.data.TokenStore
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.flow.MutableSharedFlow

// 전역 TokenStore를 관리하는 Application 클래스
class App : Application() {
    val logoutEvent = MutableSharedFlow<Unit>() //401 발생 시 알림용

    lateinit var tokenStore: TokenStore
    override fun onCreate() {
        super.onCreate()
        tokenStore = TokenStore(this)
        AndroidThreeTen.init(this)
    }
}