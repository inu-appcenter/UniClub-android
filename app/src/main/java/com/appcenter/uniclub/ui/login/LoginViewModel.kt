package com.appcenter.uniclub.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//로그인 ui 상태를 표현하는 데이터 클래스
data class LoginUiState(
    val studentId: String = "", //학번 입력값
    val password: String = "", //비밀번호 입력값
    val loading: Boolean = false, //네트워크 요청 진행 중 여부 (중복 클릭/요청 방지용)
    val error: String? = null
) {
    val canLogin get() = studentId.isNotBlank() && password.isNotBlank() && !loading
}

class LoginViewModel(private val repo: UserRepository) : ViewModel() {
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
                .onSuccess { onSuccess() }
                .onFailure { _ui.value = _ui.value.copy(error = it.message, loading = false) }
        }
    }

    // 🔵 개발용: 토큰 저장 후 바로 진입
    fun devBypass(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 필요하다면 로딩 플래그 잠깐 켤 수도 있음
            // _ui.value = _ui.value.copy(loading = true)

            repo.saveDummyToken()      // 더미 토큰 저장
            // _ui.value = _ui.value.copy(loading = false)
            onSuccess()                // 홈(또는 다음 화면)으로 이동
        }
    }
}
