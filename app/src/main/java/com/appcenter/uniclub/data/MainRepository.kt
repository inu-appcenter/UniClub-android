package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.MainService
import com.appcenter.uniclub.network.dto.MainPageMediaResponseDto

class MainRepository(private val api: MainService) {
    suspend fun getMainBanner(): List<MainPageMediaResponseDto> {
        return api.getMainBanner()
    }
}