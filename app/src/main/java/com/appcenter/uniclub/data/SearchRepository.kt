package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.ClubService
import com.appcenter.uniclub.network.dto.ClubResponseDto

class SearchRepository(private val service: ClubService) {
    suspend fun searchClubs(keyword: String): List<ClubResponseDto> {
        return service.searchClubs(keyword)
    }

    suspend fun toggleFavorite(clubId: Long): Result<Unit> = runCatching {
        service.toggleFavorite(clubId)
        Unit
    }
}