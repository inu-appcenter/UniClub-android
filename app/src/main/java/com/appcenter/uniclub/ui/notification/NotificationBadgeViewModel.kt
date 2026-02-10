package com.appcenter.uniclub.ui.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationBadgeViewModel(
    private val repo: NotificationRepository
) : ViewModel() {

    private val _hasUnread = MutableStateFlow(false)
    val hasUnread: StateFlow<Boolean> = _hasUnread.asStateFlow()

    init {
        refresh()

        viewModelScope.launch {
            NotificationEventBus.events.collect {
                Log.d("NOTI_BADGE", "EventBus received -> refresh()")
                refresh()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            // ✅ 서버 필터(isRead=false)에 의존하지 말고, 기존 알림화면처럼 '클라에서' 판별
            repo.getNotifications(page = 0, size = 30, isRead = null)  // size는 적당히
                .onSuccess { pageData ->
                    val anyUnread = pageData.items.any { !it.isRead }
                    Log.d("NOTI_BADGE", "refresh success: items=${pageData.items.size}, anyUnread=$anyUnread")
                    _hasUnread.value = anyUnread
                }
                .onFailure { e ->
                    Log.e("NOTI_BADGE", "refresh fail: ${e.message}", e)
                    // 실패 시 기존값 유지
                }
        }
    }
}

class NotificationBadgeViewModelFactory(
    private val repo: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationBadgeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationBadgeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
