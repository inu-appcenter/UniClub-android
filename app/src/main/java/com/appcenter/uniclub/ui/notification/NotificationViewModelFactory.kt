package com.appcenter.uniclub.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appcenter.uniclub.data.UserRepository

class NotificationViewModelFactory(
    private val repo: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
