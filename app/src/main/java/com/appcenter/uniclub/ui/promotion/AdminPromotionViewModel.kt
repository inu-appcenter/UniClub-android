package com.appcenter.uniclub.ui.promotion

import java.util.UUID
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appcenter.uniclub.App
import com.appcenter.uniclub.data.ClubRepository
import com.appcenter.uniclub.network.dto.ClubPromotionRegisterRequestDto
import com.appcenter.uniclub.network.dto.ClubPromotionResponseDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

enum class RecruitStatus { SCHEDULED, ACTIVE, CLOSED }

data class PromotionImage(
    val id: String = UUID.randomUUID().toString(), // 드래그/컴포즈용 안정 키
    val localUri: String?,                         // 로컬 선택 이미지 (코일이 String/Uri 둘 다 처리)
    val mediaLink: String? = null                  // 서버가 부여한 키 (uploads/....png)
) {
    val displayUri: String? get() = localUri ?: mediaLink
}

private fun now(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm")
    return LocalDateTime.now().format(formatter)
}

private val serverFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
private val uiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

// 서버에서 받은 값을 UI-friendly string 으로
fun isoToUi(iso: String): String {
    return try {
        LocalDateTime.parse(iso, serverFormatter).format(uiFormatter)
    } catch (e: Exception) {
        "" // 잘못된 값이면 공백
    }
}

// UI-friendly string 을 서버용 ISO string 으로
fun uiToIso(ui: String): String {
    return try {
        LocalDateTime.parse(ui, uiFormatter).format(serverFormatter)
    } catch (e: Exception) {
        "" // 잘못된 값이면 공백
    }
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
    fun save(app: App, onSuccess: (() -> Unit)? = null) {
        val s = _ui.value

        val startIso = if (s.recruitStartIso.isNotBlank()) uiToIso(s.recruitStartIso) else null
        val endIso   = if (s.recruitEndIso.isNotBlank()) uiToIso(s.recruitEndIso) else null

        val body = ClubPromotionRegisterRequestDto(
            name = s.clubName.ifBlank { " " },
            status = when (s.recruitStatus) {
                RecruitStatus.ACTIVE -> "ACTIVE"
                RecruitStatus.CLOSED -> "CLOSED"
                RecruitStatus.SCHEDULED -> "SCHEDULED"
            },
            startTime = startIso,
            endTime = endIso,
            simpleDescription = s.intro.ifBlank { null },
            description = s.clubDescription.ifBlank { null },
            notice = s.noticeText.ifBlank { null },
            location = s.clubRoom.ifBlank { null },
            presidentName = s.leaderName.ifBlank { null },
            presidentPhone = s.contact.ifBlank { null },
            youtubeLink = s.youtubeLink.ifBlank { null },
            instagramLink = s.instagramLink.ifBlank { null },
            applicationFormLink = s.applyLink.ifBlank { null }
        )

        viewModelScope.launch {
            try {
                // 1. 프로필 업로드
                s.profileUri?.let { local ->
                    repo.uploadClubImage(
                        clubId = clubId,
                        localUri = local,
                        mediaType = "CLUB_PROFILE",
                        main = false,
                        context = app
                    )
                }

                // 2. 배너 업로드
                s.bannerUri?.let { local ->
                    repo.uploadClubImage(
                        clubId = clubId,
                        localUri = local,
                        mediaType = "CLUB_BACKGROUND",
                        main = false,
                        context = app
                    )
                }

                // 3. 활동 사진 업로드 (순서대로, 첫 번째만 main=true)
                s.activityImages.forEachIndexed { idx, img ->
                    img.localUri?.let { local ->
                        repo.uploadClubImage(
                            clubId = clubId,
                            localUri = Uri.parse(local),
                            mediaType = "CLUB_PROMOTION",
                            main = (idx == 0),
                            context = app
                        )
                    }
                }

                // 4. 나머지 텍스트 정보 저장
                repo.upsertClubPromotion(clubId, body)

                onSuccess?.invoke()
            } catch (e: Exception) {
                android.util.Log.e("AdminPromotionVM", "저장 실패", e)
            }
        }
    }

    // ------ 업로드 진입점 ------
    fun uploadProfile(local: Uri) {
        _ui.value = _ui.value.copy(profileUri = local)
    }

    fun uploadBanner(local: Uri) {
        _ui.value = _ui.value.copy(bannerUri = local)
    }

    /** 활동사진 업로드: index 자리에 넣거나 추가. index==0이면 main=true */
    fun uploadActivityAt(index: Int, picked: Uri) {
        val list = _ui.value.activityImages.toMutableList()
        val item = PromotionImage(localUri = picked.toString())

        if (index in list.indices) {
            list[index] = item
        } else {
            list.add(item)
        }

        _ui.value = _ui.value.copy(activityImages = list)
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

    // --- 내부: 서버 → UI 매핑 ---
    private fun mapFromResponse(dto: ClubPromotionResponseDto): AdminPromotionUi {
        val startIso = dto.startTime.orEmpty()
        val endIso = dto.endTime.orEmpty()

        val simple = dto.simpleDescription.orEmpty()
        val desc = dto.description.orEmpty()

        // 서버가 내려주는 media 리스트 → UI용 PromotionImage 리스트로 변환
        val images = dto.mediaList?.map {
            PromotionImage(
                id = UUID.randomUUID().toString(),
                localUri = null,
                mediaLink = it.mediaLink // 서버에서 준 S3 경로
            )
        } ?: emptyList()

        // 배너 / 프로필 / 대표이미지 분리
        val banner = dto.mediaList
            ?.find { it.mediaType == "CLUB_BACKGROUND" }
            ?.mediaLink
            ?.let { Uri.parse(it) }

        val profile = dto.mediaList
            ?.find { it.mediaType == "CLUB_PROFILE" }
            ?.mediaLink
            ?.let { Uri.parse(it) }

        val activities = dto.mediaList
            ?.filter { it.mediaType == "CLUB_PROMOTION" }
            ?.map {
                PromotionImage(
                    id = UUID.randomUUID().toString(),
                    localUri = null,
                    mediaLink = it.mediaLink
                )
            }.orEmpty()


        return _ui.value.copy(
            loading = false,
            // 서버의 role 은 권한 분기 용으로만 사용 (필요 시 추가 보관)
            clubName = dto.name,
            intro = simple,
            clubDescription = desc,
            noticeText = dto.notice.orEmpty(),
            clubRoom = dto.location.orEmpty(),
            leaderName = dto.presidentName.orEmpty(),
            contact = dto.presidentPhone.orEmpty(),
            youtubeLink = dto.youtubeLink.orEmpty(),
            instagramLink = dto.instagramLink.orEmpty(),
            applyLink = dto.applicationFormLink.orEmpty(),
            recruitStatus = when (dto.status) {
                "ACTIVE" -> RecruitStatus.ACTIVE
                "CLOSED" -> RecruitStatus.CLOSED
                else -> RecruitStatus.SCHEDULED
            },
            recruitStartIso = isoToUi(dto.startTime.orEmpty()),
            recruitEndIso = isoToUi(dto.endTime.orEmpty()),
            recruitPeriod = if (startIso.isNotEmpty() && endIso.isNotEmpty()) {
                "$startIso ~ $endIso"
            } else "", // 값이 없을 경우 공백으로
            bannerUri = banner,
            profileUri = profile,
            activityImages = activities
        )
    }
}

class AdminPromotionViewModelFactory(
    private val app: App,
    private val clubId: Long
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminPromotionViewModel::class.java)) {
            val repo = com.appcenter.uniclub.di.ServiceLocator.clubRepository(app)
            @Suppress("UNCHECKED_CAST")
            return AdminPromotionViewModel(repo, clubId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
