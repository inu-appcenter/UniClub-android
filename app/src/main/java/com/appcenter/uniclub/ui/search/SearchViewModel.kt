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
}
