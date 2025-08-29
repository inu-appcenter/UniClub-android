package com.appcenter.uniclub.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.NotificationRepository
import com.appcenter.uniclub.ui.notification.NotificationItem
import com.appcenter.uniclub.ui.notification.NotificationType
import com.appcenter.uniclub.util.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(private val repo: NotificationRepository) : ViewModel() {
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications

    init {
        loadNotifications()
    }

    private fun mapType(serverType: String): NotificationType {
        return when (serverType) {
            "FEDERATION" -> NotificationType.NOTICE
            "CLUB" -> NotificationType.INTERESTED_CLUB
            "QNA" -> NotificationType.ANSWER_RECEIVED
            else -> NotificationType.NOTICE // 기본값
        }
    }

    fun loadNotifications() {
        viewModelScope.launch {
            repo.getNotifications().onSuccess { list ->
                _notifications.value = list.map { dto ->
                    NotificationItem(
                        id = dto.notificationId.toString(),
                        title = dto.message,
                        description = "",
                        time = TimeUtils.formatRelativeTime(dto.createdAt),
                        isRead = dto.isRead,   // 변경
                        type = mapType(dto.type)
                    )
                }
            }.onFailure {
                // TODO: 에러 처리 (로그 출력이나 상태 표시)
            }
        }
    }

    // ✅ 서버에 읽음 처리 요청 + 로컬 상태 반영
    fun markAsRead(id: String) {
        viewModelScope.launch {
            repo.markAsRead(id.toLong()).onSuccess {
                _notifications.value = _notifications.value.map {
                    if (it.id == id) it.copy(isRead = true) else it
                }
            }
        }
    }

    // ✅ 서버에 삭제 요청 + 로컬 상태 반영
    fun delete(id: String) {
        viewModelScope.launch {
            repo.deleteNotification(id.toLong()).onSuccess {
                _notifications.value = _notifications.value.filter { it.id != id }
            }
        }
    }

    val hasUnread: StateFlow<Boolean> = notifications
        .map { list -> list.any { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)
}

class NotificationViewModelFactory(
    private val repo: NotificationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}