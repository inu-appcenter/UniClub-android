package com.appcenter.uniclub.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.UserRepository
import com.appcenter.uniclub.model.Major
import com.appcenter.uniclub.network.dto.RegisterRequestDto
import com.appcenter.uniclub.network.dto.RegisterTermsRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//회원가입 ui 상태를 표현하는 데이터 클래스
data class SignUpUiState(
    val studentId: String = "",
    val password: String = "",
    val name: String = "",
    val major: String = "",
    val personalInfoCollectionAgreement: Boolean = false, // 필수 동의
    val marketingAdvertisement: Boolean = false,          // 선택 동의
    val verified: Boolean = false,
    val loading: Boolean = false,
    val error: String? = null
) {
    val canVerify get() = studentId.isNotBlank() && password.isNotBlank() && !loading
    val canProceed get() = verified && name.isNotBlank() && major.isNotBlank() && !loading
}

class SignUpViewModel(private val repo: UserRepository) : ViewModel() {
    private val _ui = MutableStateFlow(SignUpUiState()) //내부에서만 수정 가능한 상태
    val ui: StateFlow<SignUpUiState> = _ui //외부(ui)에는 읽기 전용으로 노출

    fun onId(v: String) { _ui.value = _ui.value.copy(studentId = v, error = null) }
    fun onPw(v: String) { _ui.value = _ui.value.copy(password = v, error = null) }
    fun onName(v: String) { _ui.value = _ui.value.copy(name = v, error = null) }
    fun onMajor(displayName: String) {
        val enumValue = Major.values().find { it.displayName == displayName }
        _ui.value = _ui.value.copy(
            major = enumValue?.name ?: "",
            error = null
        )
    }

    fun onEssentialAgree(v: Boolean) { _ui.value = _ui.value.copy(personalInfoCollectionAgreement = v) }
    fun onChoiceAgree(v: Boolean) { _ui.value = _ui.value.copy(marketingAdvertisement = v) }

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

    // (기존 register 대체) 약관 저장 + 회원가입을 한 번에
    fun agreeAndRegister(onDone: () -> Unit) {
        val s = _ui.value
        if (!s.personalInfoCollectionAgreement) {
            _ui.value = s.copy(error = "필수 약관에 동의해 주세요.")
            return
        }
        _ui.value = s.copy(loading = true, error = null)

        viewModelScope.launch {
            // 1) 회원가입
            val regReq = RegisterRequestDto(
                studentId = s.studentId,
                name = s.name,
                major = s.major,
                personalInfoCollectionAgreement = s.personalInfoCollectionAgreement,
                marketingAdvertisement = s.marketingAdvertisement,
                studentVerification = true
            )
            val registerResult = repo.register(regReq)

            registerResult.fold(
                onSuccess = {
                    // 2) 약관 저장
                    val termsReq = RegisterTermsRequestDto(
                        studentId = s.studentId,
                        personalInfoCollectionAgreement = s.personalInfoCollectionAgreement,
                        marketingAdvertisement = s.marketingAdvertisement
                    )
                    repo.saveRegisterTerms(termsReq)
                        .onSuccess {
                            _ui.value = _ui.value.copy(loading = false, error = null)
                            onDone()
                        }
                        .onFailure {
                            _ui.value = _ui.value.copy(loading = false, error = it.message)
                        }
                },
                onFailure = {
                    _ui.value = _ui.value.copy(loading = false, error = it.message)
                }
            )
        }
    }
}
