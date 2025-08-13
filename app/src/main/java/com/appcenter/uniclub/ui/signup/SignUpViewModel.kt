package com.appcenter.uniclub.ui.signup

import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.AuthRepository
import com.appcenter.uniclub.network.dto.RegisterRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//회원가입 ui 상태를 표현하는 데이터 클래스
data class SignUpUiState(
    val studentId: String = "", //학번 입력값
    val password: String = "", //비밀번호 입력값
    val name: String = "", //사용자 이름
    val major: String = "", //전공
    val agreed: Boolean = true, //약관 동의 여부
    val verified: Boolean = false, //재학생 인증 성공 여부
    val loading: Boolean = false, //네트워크 요청 중 여부
    val error: String? = null //사용자에게 보여줄 에러 메시지
) {
    val canVerify get() = studentId.isNotBlank() && password.isNotBlank() && !loading //학번/비번이 유효하게 채워졌고, 현재 로딩 중이 아닐 때 "재학생확인" 버튼 활성화
    val canProceed get() = verified && name.isNotBlank() && major.isNotBlank() && !loading //인증 완료 + 이름/전공이 채워졌고, 현재 로딩 중이 아닐 때 "다음" 버튼 활성화
}

class SignUpViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _ui = MutableStateFlow(SignUpUiState()) //내부에서만 수정 가능한 상태
    val ui: StateFlow<SignUpUiState> = _ui //외부(ui)에는 읽기 전용으로 노출

    fun onId(v: String) { _ui.value = _ui.value.copy(studentId = v, error = null) }
    fun onPw(v: String) { _ui.value = _ui.value.copy(password = v, error = null) }
    fun onName(v: String) { _ui.value = _ui.value.copy(name = v, error = null) }
    fun onMajor(v: String) { _ui.value = _ui.value.copy(major = v, error = null) }

    //재학생 인증 요청
    fun verify() {
        val s = _ui.value
        if (!s.canVerify) return
        _ui.value = s.copy(loading = true, error = null, verified = false)
        viewModelScope.launch {
            repo.verifyStudent(s.studentId, s.password)
                .onSuccess { _ui.value = _ui.value.copy(verified = true, loading = false, error = null) }
                .onFailure { _ui.value = _ui.value.copy(verified = false, loading = false, error = it.message) }
        }
    }

    //회원가입 요청
    fun register(onDone: () -> Unit) {
        val s = _ui.value
        if (!s.canProceed) return
        _ui.value = s.copy(loading = true, error = null)
        viewModelScope.launch {
            val req = RegisterRequestDto(
                studentId = s.studentId,
                password = s.password,
                name = s.name,
                major = s.major,
                agreed = s.agreed
            )
            repo.register(req)
                .onSuccess {
                    _ui.value = _ui.value.copy(loading = false, error = null)
                    onDone()
                }
                .onFailure { _ui.value = _ui.value.copy(error = it.message, loading = false) }
        }
    }
}
