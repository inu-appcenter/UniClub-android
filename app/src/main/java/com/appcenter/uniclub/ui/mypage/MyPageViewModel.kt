package com.appcenter.uniclub.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.App
import com.appcenter.uniclub.data.UserRepository
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.model.Major
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MyPageUiState(
    val nickname: String? = null,
    val name: String = "",
    val studentId: String = "",
    val major: String = "",
    val profileImageLink: String? = null,
    val loading: Boolean = false,
    val error: String? = null
)

class MyPageViewModel(
    private val repo: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyPageUiState())
    val uiState: StateFlow<MyPageUiState> = _uiState

    fun loadMyPage() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            val result = repo.getMyPage()
            result.fold(
                onSuccess = { dto ->
                    val majorDisplay = try {
                        Major.valueOf(dto.major).displayName
                    } catch (e: IllegalArgumentException) {
                        dto.major // 매핑 실패 시 원래 값 표시
                    }

                    _uiState.value = MyPageUiState(
                        nickname = dto.nickname,
                        name = dto.name,
                        studentId = dto.studentId,
                        major = majorDisplay,
                        profileImageLink = dto.profileImageLink,
                        loading = false
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(loading = false, error = e.message)
                }
            )
        }
    }

    suspend fun logout() {
        repo.logout() //tokenStore.clear() 실행됨
    }
}

class MyPageViewModelFactory(private val app: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = ServiceLocator.userRepository(app)
        return MyPageViewModel(repo) as T
    }
}