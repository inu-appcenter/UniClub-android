package com.appcenter.uniclub.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.MainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MainRepository
) : ViewModel() {

    private val _bannerList = MutableStateFlow<List<String>>(emptyList())
    val bannerList: StateFlow<List<String>> = _bannerList

    init {
        fetchBanners()
    }

    private fun fetchBanners() {
        viewModelScope.launch {
            try {
                val response = repository.getMainBanner()
                _bannerList.value = response.map { it.mediaLink }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}