package com.appcenter.uniclub.ui.home.clublist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.ClubRepository
import com.appcenter.uniclub.network.dto.ClubResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ClubListUiState(
    val clubs: List<ClubResponseDto> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val hasNext: Boolean = true
)

class ClubListViewModel(
    private val repository: ClubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClubListUiState())
    val uiState: StateFlow<ClubListUiState> = _uiState

    private var currentCategory: String? = null
    private var currentSortBy: String = "name"

    private val toggling = mutableSetOf<Long>()

    fun loadClubs(category: String?, sortBy: String, reset: Boolean = false) {
        currentCategory = category
        currentSortBy = sortBy

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true, error = null)
                val res = repository.fetchClubs(category, sortBy, reset)
                val newList =
                    if (reset) res.content
                    else _uiState.value.clubs + res.content

                _uiState.value = _uiState.value.copy(
                    clubs = newList,
                    loading = false,
                    hasNext = res.hasNext
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "알 수 없는 오류가 발생했습니다."
                )
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.loading || !state.hasNext) return
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true, error = null)

                val res = repository.fetchClubs(
                    category = currentCategory,
                    sortBy = currentSortBy,
                    reset = false
                )
                _uiState.value = _uiState.value.copy(
                    clubs = _uiState.value.clubs + res.content,
                    loading = false,
                    hasNext = res.hasNext
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "알 수 없는 오류가 발생했습니다."
                )
            }
        }
    }

    fun reset() {
        repository.resetPaging()
        _uiState.value = ClubListUiState()
    }

    fun onFavoriteClick(clubId: Long) {
        if (toggling.contains(clubId)) return
        toggling.add(clubId)

        val before = _uiState.value.clubs
        val optimistic = before.map { c ->
            if (c.id == clubId) c.copy(favorite = !c.favorite) else c
        }
        _uiState.value = _uiState.value.copy(clubs = optimistic, error = null)

        viewModelScope.launch {
            val result = repository.toggleFavorite(clubId)
            if (result.isFailure) {
                // 실패 → 롤백
                _uiState.value = _uiState.value.copy(clubs = before, error = result.exceptionOrNull()?.message)
            }
            toggling.remove(clubId)
        }
    }
}