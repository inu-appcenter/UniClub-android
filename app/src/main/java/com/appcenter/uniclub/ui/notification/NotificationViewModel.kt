package com.appcenter.uniclub.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NotificationViewModel(private val repo: AuthRepository) : ViewModel() {
    //서버 연동 전, 임시 더미 데이터로 초기화
    private val _notifications = MutableStateFlow(dummyNotifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications

    //특정 알림 읽음 처리
    fun markAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    //특정 알림 삭제
    fun delete(id: String) {
        _notifications.value = _notifications.value.filter { it.id != id }
    }

    //안읽은 알림이 하나라도 있으면 true
    val hasUnread: StateFlow<Boolean> = notifications
        .map { list -> list.any { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
}
