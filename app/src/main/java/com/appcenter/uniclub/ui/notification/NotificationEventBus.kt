package com.appcenter.uniclub.ui.notification

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

//알림이 새로 도착/변경되었음을 앱 전체에 알리는 이벤트 버스
object NotificationEventBus {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val events: SharedFlow<Unit> = _events

    fun notifyChanged() {
        _events.tryEmit(Unit)
    }
}
