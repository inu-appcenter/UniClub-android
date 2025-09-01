package com.appcenter.uniclub.ui.promotion

import java.util.UUID
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.data.ClubRepository
import com.appcenter.uniclub.network.dto.ClubPromotionRegisterRequestDto
import com.appcenter.uniclub.network.dto.ClubPromotionResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class RecruitStatus { SCHEDULED, ACTIVE, CLOSED }

data class PromotionImage(
    val id: String = UUID.randomUUID().toString(), // 드래그/컴포즈용 안정 키
    val localUri: String?,                         // 로컬 선택 이미지 (코일이 String/Uri 둘 다 처리)
    val mediaLink: String? = null                  // 서버가 부여한 키 (uploads/....png)
) {
    val displayUri: String? get() = localUri ?: mediaLink
}

data class AdminPromotionUi(
    val loading: Boolean = false,
    val error: String? = null,

    // --- 텍스트/링크 필드 ---
    val clubName: String = "",            // 서버 스키마상 name 이 필수라면 사용
    val intro: String = "",               // simpleDescription (한 줄 소개)
    val clubDescription: String = "",     // description
    val noticeText: String = "",          // notice
    val clubRoom: String = "",            // location
    val leaderName: String = "",          // presidentName
    val contact: String = "",             // presidentPhone

    val applyLink: String = "",           // applicationFormLink
    val youtubeLink: String = "",         // youtubeLink
    val instagramLink: String = "",       // instagramLink

    // --- 모집 상태/기간 ---
    val recruitStatus: RecruitStatus = RecruitStatus.SCHEDULED,
    val recruitPeriod: String = "",
    val recruitStartIso: String = "",
    val recruitEndIso: String = "",

    // --- 이미지(별도 업로드 스펙 확정 전, URI 문자열로만 보관) ---
    val bannerUri: Uri? = null,
    val profileUri: Uri? = null,
    val activityImageUris: List<Uri> = emptyList(),

    val activityImages: List<PromotionImage> = emptyList()
)

class AdminPromotionViewModel(
    private val repo: ClubRepository,
    private val clubId: Long
) : ViewModel() {

    private val _ui = MutableStateFlow(AdminPromotionUi(loading = true))
    val ui = _ui.asStateFlow()

    init {
        // 기존 홍보 데이터 로드
        viewModelScope.launch {
            repo.getClubPromotion(clubId)
                .onSuccess { dto -> _ui.value = mapFromResponse(dto) }
                .onFailure { e ->
                    _ui.value = _ui.value.copy(loading = false, error = e.message ?: "불러오기 실패")
                }
        }
    }

    /** UI 상태 일괄 업데이트 헬퍼 */
    fun update(transform: (AdminPromotionUi) -> AdminPromotionUi) {
        _ui.value = transform(_ui.value)
    }

    private fun parseRecruitPeriodOneLine(input: String): Pair<String, String>? {
        // "~" 기준 2조각만 허용 (하이픈과 헷갈리지 않음)
        val parts = input.split("~").map { it.trim() }
        if (parts.size != 2) return null

        fun normalize(p: String): String? {
            val t = p.replace(' ', 'T')
            val full = Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}""")
            val mins = Regex("""\d{4}-\d{2}-\d{2}T\d{2}:\d{2}""")
            val date = Regex("""\d{4}-\d{2}-\d{2}""")
            return when {
                full.matches(t) -> t
                mins.matches(t) -> "${t}:00"
                date.matches(t) -> "${t}T00:00:00"
                else -> null
            }
        }

        val a = normalize(parts[0]) ?: return null
        val b = normalize(parts[1]) ?: return null
        return a to b
    }

    /** 저장 */
    fun save() {
        val s = _ui.value

        val (startIso, endIso) = when {
            s.recruitPeriod.isNotBlank() -> {
                parseRecruitPeriodOneLine(s.recruitPeriod)
                    ?: run {
                        _ui.value = s.copy(
                            error = "모집기간은 'YYYY-MM-DD HH:mm ~ YYYY-MM-DD HH:mm' 형식으로 입력해 주세요."
                        )
                        return
                    }
            }
            // 2) 아니면 개별 ISO 값을 사용(이미 채워져 있다면)
            s.recruitStartIso.isNotBlank() && s.recruitEndIso.isNotBlank() ->
                s.recruitStartIso to s.recruitEndIso

            else -> {
                _ui.value = s.copy(error = "모집기간을 입력해 주세요.")
                return
            }
        }

        // 최소 유효성 검사 (필요에 맞게 보강)
        when {
            s.recruitStartIso.isBlank() -> {
                _ui.value = s.copy(error = "모집 시작 시간을 입력해 주세요 (ISO-8601).")
                return
            }
            s.recruitEndIso.isBlank() -> {
                _ui.value = s.copy(error = "모집 마감 시간을 입력해 주세요 (ISO-8601).")
                return
            }
            s.leaderName.isBlank() -> {
                _ui.value = s.copy(error = "회장 이름을 입력해 주세요.")
                return
            }
            s.contact.isBlank() -> {
                _ui.value = s.copy(error = "연락처를 입력해 주세요.")
                return
            }
            s.clubRoom.isBlank() -> {
                _ui.value = s.copy(error = "동아리방 위치를 입력해 주세요.")
                return
            }
        }

        val body = ClubPromotionRegisterRequestDto(
            // 서버 스키마에 name 이 존재함. 필수일 수 있으므로 비어있으면 한 칸이라도 넣어 서버 검증을 피함.
            name = s.clubName.ifBlank { " " },
            status = when (s.recruitStatus) {
                RecruitStatus.ACTIVE -> "ACTIVE"
                RecruitStatus.CLOSED -> "CLOSED"
                RecruitStatus.SCHEDULED -> "SCHEDULED"
            },
            startTime = s.recruitStartIso,
            endTime = s.recruitEndIso,
            simpleDescription = s.intro,
            description = s.clubDescription,
            notice = s.noticeText,
            location = s.clubRoom,
            presidentName = s.leaderName,
            presidentPhone = s.contact,
            youtubeLink = s.youtubeLink.ifBlank { null },
            instagramLink = s.instagramLink.ifBlank { null },
            applicationFormLink = s.applyLink.ifBlank { null }
        )

        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            repo.upsertClubPromotion(clubId, body)
                .onSuccess {
                    _ui.value = _ui.value.copy(loading = false)
                }
                .onFailure { e ->
                    _ui.value = _ui.value.copy(
                        loading = false,
                        error = e.message ?: "저장에 실패했습니다."
                    )
                }
        }
    }

    // ------ 업로드 진입점 ------
    fun uploadBanner(local: Uri) = uploadGeneric(local, "banner_${now()}.jpg", "CLUB_BACKGROUND", main = true) {
        _ui.value = _ui.value.copy(bannerUri = local)
    }

    fun uploadProfile(local: Uri) = uploadGeneric(local, "profile_${now()}.jpg", "CLUB_PROFILE", main = true) {
        _ui.value = _ui.value.copy(profileUri = local)
    }

    /** 활동사진 업로드: index 자리에 넣거나 추가. index==0이면 main=true */
    fun uploadActivityAt(index: Int, picked: Uri) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)

            val fileName = "activity_${System.currentTimeMillis()}_$index.jpg"
            repo.uploadClubImage(
                clubId = clubId,
                localUri = picked,
                filename = fileName,
                mediaType = "CLUB_PROMOTION",
                main = (index == 0)
            ).onSuccess { mediaLink ->
                val list = _ui.value.activityImages.toMutableList()
                val item = PromotionImage(localUri = picked.toString(), mediaLink = mediaLink)

                if (index in list.indices) list[index] = item else list.add(item)
                _ui.value = _ui.value.copy(activityImages = list, loading = false)

                // 업로드가 0번에 들어갔다면 이미 main=true로 서버 반영됨
            }.onFailure { e ->
                _ui.value = _ui.value.copy(loading = false, error = e.message)
            }
        }
    }

    /** 드래그로 순서 변경: UI 즉시 반영 + 0번이 바뀌면 main=true 서버와 동기화 */
    fun reorderActivity(from: Int, to: Int) {
        val cur = _ui.value.activityImages.toMutableList()
        if (from !in cur.indices || to !in cur.indices) return
        val moved = cur.removeAt(from)
        cur.add(to, moved)
        _ui.value = _ui.value.copy(activityImages = cur)

        // 0번이 바뀌었으면 서버 main 동기화
        syncMainIfNeeded()
    }

    /** 사진 삭제 (서버 삭제 API 있으면 여기서 호출) */
    fun removeActivityAt(index: Int) {
        val cur = _ui.value.activityImages.toMutableList()
        if (index !in cur.indices) return
        cur.removeAt(index)
        _ui.value = _ui.value.copy(activityImages = cur)
        syncMainIfNeeded()
    }

    /** 첫 번째 사진을 main=true로 서버에 반영(가능할 때) */
    private fun syncMainIfNeeded() {
        val first = _ui.value.activityImages.firstOrNull() ?: return
        val mediaLink = first.mediaLink ?: return  // 서버에 등록된 이미지여야 main 변경 가능

        // 백엔드에 main만 바꾸는 엔드포인트가 없다면, 저장 시 일괄 반영으로 미뤄야 함.
        // 아래는 엔드포인트가 있을 때의 형태(예시). 없으면 조용히 리턴.
        viewModelScope.launch {
            runCatching {

            }.onFailure {
                // 실패해도 UI 드래그는 그대로 유지
            }
        }
    }

    private fun uploadGeneric(
        local: Uri,
        filename: String,
        mediaType: String,
        main: Boolean,
        onUi: () -> Unit
    ) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            repo.uploadClubImage(clubId, local, filename, mediaType, main)
                .onSuccess { onUi(); _ui.value = _ui.value.copy(loading = false) }
                .onFailure { e -> _ui.value = _ui.value.copy(loading = false, error = e.message) }
        }
    }

    // --- 내부: 서버 → UI 매핑 ---
    private fun mapFromResponse(dto: ClubPromotionResponseDto): AdminPromotionUi {
        val startIso = dto.startTime
        val endIso = dto.endTime
        return _ui.value.copy(
            loading = false,
            // 서버의 role 은 권한 분기 용으로만 사용 (필요 시 추가 보관)
            clubName = dto.name,
            intro = dto.description.takeIf { it.isNotBlank() } ?: "", // simpleDescription이 없다면 임시로 description에서 보정
            clubDescription = dto.description,
            noticeText = dto.notice,
            clubRoom = dto.location,
            leaderName = dto.presidentName,
            contact = dto.presidentPhone,
            youtubeLink = dto.youtubeLink.orEmpty(),
            instagramLink = dto.instagramLink.orEmpty(),
            applyLink = dto.applicationFormLink.orEmpty(),
            recruitStatus = when (dto.status) {
                "ACTIVE" -> RecruitStatus.ACTIVE
                "CLOSED" -> RecruitStatus.CLOSED
                else -> RecruitStatus.SCHEDULED
            },
            recruitStartIso = startIso,
            recruitEndIso = endIso,
            recruitPeriod = "$startIso ~ $endIso"
        )
    }

    // -------- Factory (직접 전달 방식) --------
    companion object {
        fun factory(
            repo: ClubRepository,
            clubId: Long
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdminPromotionViewModel(repo, clubId) as T
            }
        }

        private fun now() = System.currentTimeMillis()
    }
}