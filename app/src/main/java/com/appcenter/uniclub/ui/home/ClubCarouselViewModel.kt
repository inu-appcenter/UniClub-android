package com.appcenter.uniclub.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.MainRepository
import com.appcenter.uniclub.network.dto.MainPageClubResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClubCarouselUiState(
    val clubs: List<MainPageClubResponseDto> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)

class ClubCarouselViewModel(
    private val repo: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubCarouselUiState())
    val uiState: StateFlow<ClubCarouselUiState> = _uiState

    init {
        refresh(initial = true)
    }

    fun refresh(initial: Boolean = false) {
        if (!initial && _uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            repo.getMainClubs()
                .onSuccess { list ->
                    // 서버가 최대 6개를 준다고 하지만, 혹시 몰라 take(6)
                    _uiState.update { it.copy(clubs = list.take(6), isRefreshing = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isRefreshing = false, error = e.message) }
                }
        }
    }
}

class ClubCarouselViewModelFactory(
    private val repo: MainRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ClubCarouselViewModel(repo) as T
    }
}