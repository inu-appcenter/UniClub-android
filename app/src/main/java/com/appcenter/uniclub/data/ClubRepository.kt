package com.appcenter.uniclub.data

import com.appcenter.uniclub.network.ClubService
import com.appcenter.uniclub.network.dto.ClubResponseDto
import com.appcenter.uniclub.network.dto.PageClubResponseDto

class ClubRepository(
    private val service: ClubService
) {
    // 페이지네이션 상태
    private var lastCursorName: String? = null
    private var hasNext: Boolean = true
    private var currentSortBy: String = "name"
    private var currentCategory: String? = null
    private val pageSize = 10

    /** 정렬/카테고리 바뀌면 reset=true 로 호출 */
    suspend fun fetchClubs(
        category: String?,
        sortBy: String,
        reset: Boolean = false
    ): PageClubResponseDto {
        if (reset || currentSortBy != sortBy || currentCategory != category) {
            // 상태 초기화
            lastCursorName = null
            hasNext = true
            currentSortBy = sortBy
            currentCategory = category
        }
        if (!hasNext) {
            return PageClubResponseDto(content = emptyList(), hasNext = false)
        }

        val response = service.getClubs(
            category = currentCategory,
            sortBy = currentSortBy,        // "name" | "like" | "status"
            cursorName = lastCursorName,   // null이면 첫 페이지
            size = pageSize
        )

        // 다음 페이지 커서 갱신 (서버 명세가 name 커서라 가정)
        lastCursorName = response.content.lastOrNull()?.name
        hasNext = response.hasNext

        return response
    }

    fun canLoadMore(): Boolean = hasNext
    fun resetPaging() {
        lastCursorName = null
        hasNext = true
    }
}