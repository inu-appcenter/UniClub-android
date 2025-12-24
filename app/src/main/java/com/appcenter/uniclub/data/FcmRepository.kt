package com.appcenter.uniclub.data

import android.util.Log
import com.appcenter.uniclub.network.FcmService
import com.appcenter.uniclub.network.dto.FcmRegisterRequestDto
import retrofit2.HttpException

class FcmRepository(
    private val fcmService: FcmService,
    private val tokenStore: TokenStore,
    private val fcmTokenStore: FcmTokenStore
) {
    //현재 로그인 상태면 서버에 등록, 아니면 pending으로 저장
    suspend fun registerIfLoggedIn(token: String): Result<Unit> = runCatching {
        Log.d("FCM", "registerIfLoggedIn entered")
        Log.d("FCM", "authHeader empty?=${tokenStore.getAuthHeader().isNullOrBlank()}")

        val authHeader = tokenStore.getAuthHeader()
        if (authHeader.isNullOrBlank()) {
            //로그인 전이면 보류
            fcmTokenStore.setPendingToken(token)
            Log.d("FCM", "Not logged-in. Save pending token.")
            return@runCatching
        }

        //이미 같은 토큰을 보냈으면 중복 전송 스킵
        val lastSent = fcmTokenStore.getLastSentToken()
        if (lastSent == token) {
            Log.d("FCM", "Token already sent. Skip.")
            return@runCatching
        }

        val res = fcmService.registerFcmToken(FcmRegisterRequestDto(token))
        Log.d("FCM", "register response code=${res.code()}")
        if (!res.isSuccessful) throw HttpException(res)

        fcmTokenStore.setLastSentToken(token)
        fcmTokenStore.setPendingToken(null)
        Log.d("FCM", "FCM token registered to server.")
    }

    /**
     * 로그인 직후에 호출: pending 토큰이 있으면 보내고,
     * 없으면 현재 토큰을 받아서 보내는 흐름에서 사용
     */
    suspend fun flushPendingIfAny(currentToken: String?): Result<Unit> = runCatching {
        val pending = fcmTokenStore.getPendingToken()
        val tokenToSend = pending ?: currentToken
        if (tokenToSend.isNullOrBlank()) return@runCatching

        registerIfLoggedIn(tokenToSend).getOrThrow()
    }
}
