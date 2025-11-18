package com.appcenter.uniclub.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.SearchRepository
import com.appcenter.uniclub.network.dto.ClubResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    private val _results = MutableStateFlow<List<ClubResponseDto>>(emptyList())
    val results: StateFlow<List<ClubResponseDto>> = _results

    private val toggling = mutableSetOf<Long>()

    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _results.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.searchClubs(keyword)
                _results.value = response
            } catch (e: Exception) {
                _results.value = emptyList()
            }
        }
    }

    fun onFavoriteClick(clubId: Long) {
        if (toggling.contains(clubId)) return
        toggling.add(clubId)

        val before = _results.value
        // 낙관적 토글
        val optimistic = before.map { c ->
            if (c.id == clubId) c.copy(favorite = !c.favorite) else c
        }
        _results.value = optimistic

        viewModelScope.launch {
            val result = repository.toggleFavorite(clubId)
            if (result.isFailure) {
                _results.value = before // 실패 롤백
            }
            toggling.remove(clubId)
        }
    }

    fun clear() {
        _results.value = emptyList()
    }
}
