package com.appcenter.uniclub.ui.notification

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.NotificationRepository
import com.appcenter.uniclub.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotificationTabState(
    val items: List<NotificationItem> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 1,
    val hasNext: Boolean = false
)

data class NotificationUiState(
    val unread: NotificationTabState = NotificationTabState(),
    val read: NotificationTabState = NotificationTabState()
)

private enum class TabKey { UNREAD, READ }

class NotificationViewModel(private val repo: NotificationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        //최초 로드
        refresh()

        //FCM 등 알림 변경 이벤트 오면 0페이지부터 다시 로드
        viewModelScope.launch {
            NotificationEventBus.events.collect {
                refresh()
            }
        }
    }

    //항상 최신 상태로 다시 불러오기(0페이지부터)
    fun refresh(size: Int = 10) {
        refreshUnread(size)
        refreshRead(size)
    }

    fun refreshUnread(size: Int = 10) = loadPage(TabKey.UNREAD, page = 0, size = size, reset = true)
    fun refreshRead(size: Int = 10) = loadPage(TabKey.READ, page = 0, size = size, reset = true)

    fun loadMoreUnread(size: Int = 10) {
        val st = _uiState.value.unread
        if (st.loading || !st.hasNext) return
        loadPage(TabKey.UNREAD, page = st.currentPage + 1, size = size, reset = false)
    }

    fun loadMoreRead(size: Int = 10) {
        val st = _uiState.value.read
        if (st.loading || !st.hasNext) return
        loadPage(TabKey.READ, page = st.currentPage + 1, size = size, reset = false)
    }

    private fun loadPage(tab: TabKey, page: Int, size: Int, reset: Boolean) {
        viewModelScope.launch {
            setLoading(tab, true)

            val isReadFilter = (tab == TabKey.READ)

            repo.getNotifications(
                page = page,
                size = size,
                sort = "createdAt,DESC",
                isRead = isReadFilter
            )
                .onSuccess { pageData ->
                    val cur = _uiState.value

                    val merged = if (tab == TabKey.UNREAD) {
                        val base = if (reset) emptyList() else cur.unread.items
                        (base + pageData.items).distinctBy { it.id }
                    } else {
                        val base = if (reset) emptyList() else cur.read.items
                        (base + pageData.items).distinctBy { it.id }
                    }

                    Log.d(
                        "NOTI",
                        "tab=$tab isRead=$isReadFilter page=${pageData.currentPage} items=${pageData.items.size} merged=${merged.size} hasNext=${pageData.hasNext}"
                    )

                    if (tab == TabKey.UNREAD) {
                        _uiState.value = cur.copy(
                            unread = cur.unread.copy(
                                items = merged,
                                loading = false,
                                error = null,
                                currentPage = pageData.currentPage,
                                totalPages = pageData.totalPages,
                                hasNext = pageData.hasNext
                            )
                        )
                    } else {
                        _uiState.value = cur.copy(
                            read = cur.read.copy(
                                items = merged,
                                loading = false,
                                error = null,
                                currentPage = pageData.currentPage,
                                totalPages = pageData.totalPages,
                                hasNext = pageData.hasNext
                            )
                        )
                    }
                }
                .onFailure { e ->
                    Log.e("NOTI", "tab=$tab load failed: ${e.message}", e)
                    val cur = _uiState.value
                    if (tab == TabKey.UNREAD) {
                        _uiState.value = cur.copy(unread = cur.unread.copy(loading = false, error = e.message))
                    } else {
                        _uiState.value = cur.copy(read = cur.read.copy(loading = false, error = e.message))
                    }
                }
        }
    }

    private fun setLoading(tab: TabKey, loading: Boolean) {
        val cur = _uiState.value
        _uiState.value = if (tab == TabKey.UNREAD) {
            cur.copy(unread = cur.unread.copy(loading = loading, error = null))
        } else {
            cur.copy(read = cur.read.copy(loading = loading, error = null))
        }
    }

    //개별 읽음 처리
    fun markAsRead(id: String) {
        val idLong = id.toLongOrNull() ?: return
        viewModelScope.launch {
            repo.markAsRead(idLong).onSuccess {
                val cur = _uiState.value
                val target = cur.unread.items.firstOrNull { it.id == id } ?: return@onSuccess
                val moved = target.copy(isRead = true)

                _uiState.value = cur.copy(
                    unread = cur.unread.copy(items = cur.unread.items.filter { it.id != id }),
                    read = cur.read.copy(items = listOf(moved) + cur.read.items)
                )
            }
        }
    }

    //전체 읽음 처리
    fun markAllAsRead() {
        viewModelScope.launch {
            repo.markAllAsRead().onSuccess {
                val cur = _uiState.value
                val moved = cur.unread.items.map { it.copy(isRead = true) }

                _uiState.value = cur.copy(
                    unread = cur.unread.copy(items = emptyList()),
                    read = cur.read.copy(items = moved + cur.read.items)
                )
            }
        }
    }

    //개별 삭제 처리
    fun delete(id: String) {
        val idLong = id.toLongOrNull() ?: return
        viewModelScope.launch {
            repo.deleteNotification(idLong).onSuccess {
                val cur = _uiState.value
                _uiState.value = cur.copy(
                    unread = cur.unread.copy(items = cur.unread.items.filter { it.id != id }),
                    read = cur.read.copy(items = cur.read.items.filter { it.id != id })
                )
            }
        }
    }

    //전체 삭제 처리
    fun deleteAllRead() {
        viewModelScope.launch {
            val cur = _uiState.value
            val readIds = cur.read.items.mapNotNull { it.id.toLongOrNull() }
            if (readIds.isEmpty()) return@launch

            // 현재 화면에 로드된 read들만 삭제 (안전)
            readIds.forEach { id ->
                repo.deleteNotification(id)
            }
            _uiState.value = cur.copy(read = cur.read.copy(items = emptyList()))
        }
    }
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