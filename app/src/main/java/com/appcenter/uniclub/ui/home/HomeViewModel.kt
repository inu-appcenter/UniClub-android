package com.appcenter.uniclub.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.MainRepository
import com.appcenter.uniclub.network.dto.MainPageMediaResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: MainRepository
) : ViewModel() {
    private val _bannerList = MutableStateFlow<List<Int>>(emptyList()) // 로컬 리소스 fallback 용
    val bannerList: StateFlow<List<Int>> = _bannerList

    private val _remoteBanner = MutableStateFlow<List<MainPageMediaResponseDto>>(emptyList())
    val remoteBanner: StateFlow<List<MainPageMediaResponseDto>> = _remoteBanner

    init {
        loadBanner()
    }

    private fun loadBanner() {
        viewModelScope.launch {
            runCatching { repo.getMainBanner() }
                .onSuccess { list ->
                    // mediaLink 기준으로 중복 제거
                    _remoteBanner.value = list.distinctBy { it.mediaLink }
                }
                .onFailure {
                    // 실패 시 로컬 기본 배너만 사용 (이미지 id 리스트 직접 세팅하고 싶으면 여기서 set)
                    _bannerList.value = emptyList()
                }
        }
    }
}

class HomeViewModelFactory(
    private val repo: MainRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}