package com.appcenter.uniclub.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

sealed class DeleteAccountUiState {
    object Idle : DeleteAccountUiState()
    object Loading : DeleteAccountUiState()
    object Success : DeleteAccountUiState()
    data class Error(val message: String) : DeleteAccountUiState()
}

class DeleteAccountViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<DeleteAccountUiState>(DeleteAccountUiState.Idle)
    val uiState: StateFlow<DeleteAccountUiState> = _uiState

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _uiState.value = DeleteAccountUiState.Loading
            try {
                val response: Response<Unit> = repository.deleteUser(password)
                if (response.isSuccessful) {
                    _uiState.value = DeleteAccountUiState.Success
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "비밀번호가 일치하지 않습니다."
                        404 -> "유저를 찾을 수 없습니다."
                        410 -> "이미 삭제된 유저입니다."
                        else -> "알 수 없는 오류 (${response.code()})"
                    }
                    _uiState.value = DeleteAccountUiState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = DeleteAccountUiState.Error("네트워크 오류: ${e.message}")
            }
        }
    }
}

class DeleteAccountViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DeleteAccountViewModel(repository) as T
    }
}
