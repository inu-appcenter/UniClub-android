package com.appcenter.uniclub.ui.promotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.App
import com.appcenter.uniclub.data.ClubRepository
import com.appcenter.uniclub.network.dto.DescriptionMediaDto
import com.appcenter.uniclub.network.dto.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PromotionViewData(
    val name: String,
    val status: String?,
    val bannerUrl: String?,                // CLUB_BACKGROUND
    val profileUrl: String?,               // CLUB_PROFILE
    val promoItems: List<DescriptionMediaDto>, // CLUB_PROMOTION (정렬 완료)
    val canEdit: Boolean,                  // ADMIN/PRESIDENT
    val youtubeLink: String?,
    val instagramLink: String?,
    val simpleDescription: String?,
    val description: String?,
    val location: String?,
    val presidentName: String?,
    val presidentPhone: String?,
    val startTime: String?,
    val endTime: String?,
    val notice: String?,
    val applicationFormLink: String?,
    val favorite: Boolean
)

data class PromotionUiState(
    val loading: Boolean = true,
    val data: PromotionViewData? = null,
    val error: String? = null
)

class UserPromotionViewModel(
    private val repo: ClubRepository,
    private val clubId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(PromotionUiState())
    val uiState: StateFlow<PromotionUiState> = _uiState

    private val toggling = mutableSetOf<Long>() // 연타 방지

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _uiState.value = PromotionUiState(loading = true)
        repo.getClubPromotion(clubId)
            .onSuccess { dto ->
                val bannerUrl  = dto.mediaList
                    .filter { it.mediaType == "CLUB_BACKGROUND" }
                    .maxByOrNull { it.updatedAt }?.mediaLink

                val profileUrl = dto.mediaList
                    .filter { it.mediaType == "CLUB_PROFILE" }
                    .maxByOrNull { it.updatedAt }?.mediaLink

                val promos = dto.mediaList.filter { it.mediaType == "CLUB_PROMOTION" }
                val firstMain = promos.indexOfFirst { it.main }
                val orderedPromos =
                    if (firstMain >= 0) listOf(promos[firstMain]) + promos.filterIndexed { i, _ -> i != firstMain }
                    else promos

                val canEdit = dto.role == Role.ADMIN || dto.role == Role.PRESIDENT

                _uiState.value = PromotionUiState(
                    loading = false,
                    data = PromotionViewData(
                        name = dto.name,
                        status = dto.status,
                        bannerUrl = bannerUrl,
                        profileUrl = profileUrl,
                        promoItems = orderedPromos,
                        canEdit = canEdit,
                        youtubeLink = dto.youtubeLink,
                        instagramLink = dto.instagramLink,
                        simpleDescription = dto.simpleDescription,
                        description = dto.description,
                        location = dto.location,
                        presidentName = dto.presidentName,
                        presidentPhone = dto.presidentPhone,
                        startTime = dto.startTime,
                        endTime = dto.endTime,
                        notice = dto.notice,
                        applicationFormLink = dto.applicationFormLink,
                        favorite = dto.favorite
                    )
                )
            }
            .onFailure { e ->
                _uiState.value = PromotionUiState(loading = false, error = e.message)
            }
    }

    fun onFavoriteClick(clubId: Long) {
        if (toggling.contains(clubId)) return
        toggling.add(clubId)

        val before = _uiState.value
        val current = before.data ?: run { toggling.remove(clubId); return }

        // 1) 낙관적 토글
        val optimistic = current.copy(favorite = !current.favorite)
        _uiState.value = before.copy(data = optimistic, error = null)

        // 2) 서버 호출
        viewModelScope.launch {
            val result = repo.toggleFavorite(clubId)
            if (result.isFailure) {
                // 3) 실패 시 롤백
                _uiState.value = before.copy(error = result.exceptionOrNull()?.message)
            }
            toggling.remove(clubId)
        }
    }
}

class UserPromotionViewModelFactory(
    private val app: App,
    private val clubId: Long
) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        val repo = com.appcenter.uniclub.di.ServiceLocator.clubRepository(app)
        return UserPromotionViewModel(repo, clubId) as T
    }
}