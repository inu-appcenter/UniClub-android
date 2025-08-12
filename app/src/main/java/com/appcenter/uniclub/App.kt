package com.appcenter.uniclub

import android.app.Application
import com.appcenter.uniclub.data.TokenStore

// 전역 TokenStore를 관리하는 Application 클래스
class App : Application() {

    lateinit var tokenStore: TokenStore

    override fun onCreate() {
        super.onCreate()
        tokenStore = TokenStore(this)
    }
}