package com.appcenter.uniclub.ui.notification

import androidx.lifecycle.ViewModel
import com.appcenter.uniclub.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
}
