package com.appcenter.uniclub.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlarmSettingViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _checked = MutableStateFlow(false)
    val checked: StateFlow<Boolean> = _checked

    init {
        // 화면 진입 시 서버에서 현재 상태 불러오기
        viewModelScope.launch {
            try {
                val res = repository.getNotificationSetting()
                _checked.value = res.notificationEnabled
            } catch (e: Exception) {
                // 에러 처리 (로그/스낵바)
            }
        }
    }

    fun onToggle() {
        viewModelScope.launch {
            try {
                val res = repository.toggleNotification()
                // 서버 응답에 따라 상태 반영
                _checked.value = !_checked.value
                // res.message 사용 가능: "알림이 활성화되었습니다." 등
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }
}
