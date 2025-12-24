package com.appcenter.uniclub.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

/*
-로그인, 회원가입 후 발급받은 인증 토큰("Bearer xxx")을 DataStore에 저장하고 관리
-DataStore는 비동기/코루틴 기반으로 동작하여 ui와 안전하게 연동 가능
 */
class TokenStore(private val context: Context) {
    private val KEY_AUTH_HEADER = stringPreferencesKey("auth_header") //DataStore에 저장할 Key 정의

    val authHeaderFlow: Flow<String?> = context.dataStore.data.map { it[KEY_AUTH_HEADER] } //인증 헤더 스트림

    suspend fun saveAuthHeader(header: String) { //인증 헤더 저장
        context.dataStore.edit { it[KEY_AUTH_HEADER] = header }
    }

    suspend fun clear() { //인증 헤더 삭제
        context.dataStore.edit { it.remove(KEY_AUTH_HEADER) }
    }

    suspend fun getAuthHeader(): String? { //인증 헤더 단발성 조회
        return context.dataStore.data.map { it[KEY_AUTH_HEADER] }.firstOrNull()
    }

    suspend fun isLoggedIn(): Boolean = !getAuthHeader().isNullOrBlank() //편의 함수: 로그인 여부 확인
}
