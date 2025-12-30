package com.appcenter.uniclub.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * 일정 시간 동안 네비게이션(pop/navigate)을 1회만 허용하는 가드.
 * "전환 직후 연타로 pop이 2번 들어가는 문제"를 막는 용도.
 */
class NavGuard(private val lockMs: Long = 800L) {
    var locked by mutableStateOf(false)
        private set

    suspend fun run(block: () -> Unit) {
        if (locked) return
        locked = true
        try {
            block()
        } finally {
            delay(lockMs)
            locked = false
        }
    }
}
