package com.appcenter.uniclub.ui.mypage

import android.net.Uri
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
    val success: Boolean = false
)

class ProfileEditViewModel(
    private val repo: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState

    fun updateProfileImage(uri: Uri, app: App) {
        viewModelScope.launch {
            try {
                val url = repo.uploadProfileImage(app, uri) // presigned 요청 + S3 업로드
                _uiState.value = _uiState.value.copy(profileUrl = url)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "이미지 업로드 실패")
            }
        }
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
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.value = state.copy(loading = true, error = null)
            val result = repo.updateMe(
                name = state.name,       // 이름은 서버에 저장된 걸 그대로 두려면 공백 아닌 값 넣어야 함
                major = state.major,
                nickname = state.nickname,
                profileImageLink = state.profileUrl
            )
            result.fold(
                onSuccess = {
                    _uiState.value = state.copy(loading = false, success = true)
                },
                onFailure = { e ->
                    _uiState.value = state.copy(loading = false, error = e.message)
                }
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