package com.appcenter.uniclub.ui.mypage

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.App
import com.appcenter.uniclub.data.UserRepository
import com.appcenter.uniclub.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileEditUiState(
    val name: String = "",
    val major: String = "",
    val nickname: String = "",
    val profileUrl: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,

    //처음 로딩 당시 원본 값
    val originalName: String = "",
    val originalMajor: String = "",
    val originalNickname: String = "",
    val originalProfileUrl: String? = null
)

class ProfileEditViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState

    val isModified: Boolean
        get() {
            val s = _uiState.value
            return s.name != s.originalName ||
                    s.major != s.originalMajor ||
                    s.nickname != s.originalNickname ||
                    s.profileUrl != s.originalProfileUrl
        }

    // GET /users/me
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            val result = repo.getMyPage()
            result.fold(
                onSuccess = { dto ->
                    _uiState.value = ProfileEditUiState(
                        name = dto.name,
                        major = dto.major,   // ex: "COMPUTER_ENGINEERING"
                        nickname = dto.nickname ?: "",
                        profileUrl = dto.profileImageLink,
                        loading = false,

                        //원본값 저장
                        originalName = dto.name,
                        originalMajor = dto.major,
                        originalNickname = dto.nickname ?: "",
                        originalProfileUrl = dto.profileImageLink
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(loading = false, error = e.message)
                }
            )
        }
    }

    fun updateProfileImage(uri: Uri, app: App) {
        viewModelScope.launch {
            try {
                val url = repo.uploadProfileImage(app, uri)
                _uiState.value = _uiState.value.copy(profileUrl = url)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "이미지 업로드 실패: ${e.message}")
            }
        }
    }
    fun updateProfileImageDeleted() {
        _uiState.value = _uiState.value.copy(profileUrl = "__deleted__")
    }

    fun updateMajor(major: String) {
        _uiState.value = _uiState.value.copy(major = major)
    }

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun updateNickname(nickname: String) {
        _uiState.value = _uiState.value.copy(nickname = nickname)
    }

    fun updateProfile() {
        val s = _uiState.value

        // 변경된 필드만 Map에 담기
        val body = mutableMapOf<String, Any?>()

        if (s.name != s.originalName) {
            body["name"] = s.name
        }
        if (s.major != s.originalMajor) {
            body["major"] = s.major
        }
        if (s.nickname != s.originalNickname) {
            body["nickname"] = s.nickname
        }

        // 프로필 이미지 처리
        if (s.profileUrl != s.originalProfileUrl) {
            // 삭제한 경우: "" 보내기
            if (s.profileUrl == null) {
                body["profileImageLink"] = ""
            } else {
                body["profileImageLink"] = s.profileUrl!!
            }
        }

        if (s.profileUrl == "__deleted__") {
            body["profileImageLink"] = ""
        }

        // 아무것도 변경되지 않았으면 종료
        if (body.isEmpty()) return

        viewModelScope.launch {
            _uiState.value = s.copy(loading = true, error = null)
            val result = repo.updateMe(body)
            result.fold(
                onSuccess = { _uiState.value = s.copy(loading = false, success = true) },
                onFailure = { e -> _uiState.value = s.copy(loading = false, error = e.message) }
            )
        }
    }
}

class ProfileEditViewModelFactory(
    private val app: App
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = ServiceLocator.userRepository(app)
        return ProfileEditViewModel(repo) as T
    }
}