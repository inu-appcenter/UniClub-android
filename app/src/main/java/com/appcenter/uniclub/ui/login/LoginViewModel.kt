package com.appcenter.uniclub.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.FcmRepository
import com.appcenter.uniclub.data.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//로그인 ui 상태를 표현하는 데이터 클래스
data class LoginUiState(
    val studentId: String = "", //학번 입력값
    val password: String = "", //비밀번호 입력값
    val loading: Boolean = false, //네트워크 요청 진행 중 여부 (중복 클릭/요청 방지용)
    val error: String? = null
) {
    val canLogin get() = studentId.isNotBlank() && password.isNotBlank() && !loading
}

class LoginViewModel(
    private val repo: UserRepository,
    private val fcmRepo: FcmRepository
) : ViewModel() {
    private val _ui = MutableStateFlow(LoginUiState()) //내부에서만 수정 가능한 상태
    val ui: StateFlow<LoginUiState> = _ui //외부(ui)에는 읽기 전용으로 노출

    fun onIdChange(v: String) { _ui.value = _ui.value.copy(studentId = v, error = null) }
    fun onPwChange(v: String) { _ui.value = _ui.value.copy(password = v, error = null) }

    fun login(onSuccess: () -> Unit) {
        val s = _ui.value
        if (!s.canLogin) return
        _ui.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            repo.login(s.studentId, s.password)
                .onSuccess {
                    Log.d("FCM", "Login success -> start FCM register")

                    //로그인 성공 직후: FCM 토큰을 가져와 서버에 등록
                    runCatching {
                        val fcmToken = fetchFcmTokenSuspend()
                        Log.d("FCM", "Fetched token len=${fcmToken.length}, head=${fcmToken.take(12)}...")

                        val result = fcmRepo.flushPendingIfAny(fcmToken)
                        result
                            .onSuccess { Log.d("FCM", "flushPendingIfAny SUCCESS") }
                            .onFailure { e -> Log.w("FCM", "flushPendingIfAny FAILED", e) }
                    }.onFailure { e ->
                        //FCM 등록 실패해도 로그인 자체는 성공이므로 막지 않는 것이 일반적
                        Log.w("FCM", "fetch/register token failed", e)
                    }

                    //UI 상태 정리 후 성공 콜백
                    _ui.value = _ui.value.copy(loading = false, error = null)
                    onSuccess()
                }
                .onFailure { _ui.value = _ui.value.copy(error = it.message, loading = false) }
        }
    }

    //coroutines-play-services 의존성 없이 token을 suspend로 가져오기
    private suspend fun fetchFcmTokenSuspend(): String =
        suspendCancellableCoroutine { cont ->
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token -> cont.resume(token) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
}
