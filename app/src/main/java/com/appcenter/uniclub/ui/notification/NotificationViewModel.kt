package com.appcenter.uniclub.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.NotificationRepository
import com.appcenter.uniclub.model.NotificationItem
import com.appcenter.uniclub.model.NotificationType
import com.appcenter.uniclub.util.TimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotificationUiState(
    val items: List<NotificationItem> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 1,
    val hasNext: Boolean = false
)
class NotificationViewModel(private val repo: NotificationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState(loading = true))
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init { //최초 페이지 로드
        loadPage(0)
    }

    fun loadPage(page: Int, size: Int = 10) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            repo.getNotifications(page = page, size = size)
                .onSuccess { pageData ->
                    _uiState.value = _uiState.value.copy(
                        items = if (page == 0) pageData.items else _uiState.value.items + pageData.items,
                        loading = false,
                        error = null,
                        currentPage = pageData.currentPage,
                        totalPages = pageData.totalPages,
                        hasNext = pageData.hasNext
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(loading = false, error = e.message)
                }
        }
    }

    //개별 읽음 처리
    fun markAsRead(id: String) {
        viewModelScope.launch {
            repo.markAsRead(id.toLong()).onSuccess {
                _uiState.value = _uiState.value.copy(
                    items = _uiState.value.items.map { if (it.id == id) it.copy(isRead = true) else it }
                )
            }
        }
    }

    //전체 읽음 처리
    fun markAllAsRead() {
        viewModelScope.launch {
            repo.markAllAsRead().onSuccess {
                _uiState.value = _uiState.value.copy(
                    items = _uiState.value.items.map { it.copy(isRead = true) }
                )
            }
        }
    }

    //개별 삭제 처리
    fun delete(id: String) {
        viewModelScope.launch {
            repo.deleteNotification(id.toLong()).onSuccess {
                _uiState.value = _uiState.value.copy(
                    items = _uiState.value.items.filter { it.id != id }
                )
            }
        }
    }

    //전체 삭제 처리
    fun deleteAllRead() {
        viewModelScope.launch {
            val readIds = _uiState.value.items.filter { it.isRead }.map { it.id.toLong() }
            readIds.forEach { repo.deleteNotification(it) }
            _uiState.value = _uiState.value.copy(
                items = _uiState.value.items.filterNot { it.isRead }
            )
        }
    }

    //안읽은 알림 존재 여부
    val hasUnread: StateFlow<Boolean> = uiState
        .map { state -> state.items.any { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)
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