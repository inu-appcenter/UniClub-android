package com.appcenter.uniclub.ui.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appcenter.uniclub.data.UserRepository

class AlarmSettingViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmSettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmSettingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
