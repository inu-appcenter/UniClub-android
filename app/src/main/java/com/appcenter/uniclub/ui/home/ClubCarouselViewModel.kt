package com.appcenter.uniclub.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.ClubRepository
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
    private val mainRepo: MainRepository,
    private val clubRepo: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubCarouselUiState())
    val uiState: StateFlow<ClubCarouselUiState> = _uiState

    private val toggling = mutableSetOf<Long>()

    init { refresh(initial = true) }

    fun refresh(initial: Boolean = false) {
        if (!initial && _uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }
            mainRepo.getMainClubs()
                .onSuccess { list ->
                    // 서버가 최대 6개를 준다고 하지만, 혹시 몰라 take(6)
                    _uiState.update { it.copy(clubs = list.take(6), isRefreshing = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isRefreshing = false, error = e.message) }
                }
        }
    }

    fun onFavoriteClick(clubId: Long) {
        if (toggling.contains(clubId)) return
        toggling.add(clubId)

        val before = _uiState.value.clubs
        val optimistic = before.map { if (it.clubId == clubId) it.copy(favorite = !it.favorite) else it }
        _uiState.update { it.copy(clubs = optimistic, error = null) }

        // 2) 서버 호출
        viewModelScope.launch {
            val result = clubRepo.toggleFavorite(clubId)
            if (result.isFailure) {
                // 3) 실패 시 롤백
                _uiState.update { it.copy(clubs = before, error = result.exceptionOrNull()?.message) }
            }
            toggling.remove(clubId)
        }
    }
}

class ClubCarouselViewModelFactory(
    private val mainRepo: MainRepository,
    private val clubRepo: ClubRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClubCarouselViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClubCarouselViewModel(mainRepo, clubRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
