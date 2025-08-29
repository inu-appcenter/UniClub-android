package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.SearchService
import com.appcenter.uniclub.network.dto.ClubResponseDto

class SearchRepository(private val service: SearchService) {
    suspend fun searchClubs(keyword: String): List<ClubResponseDto> {
        return service.searchClubs(keyword)
    }
}