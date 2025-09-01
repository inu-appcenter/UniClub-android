package com.appcenter.uniclub.data

import android.net.Uri
import com.appcenter.uniclub.App
import com.appcenter.uniclub.network.ClubService
import com.appcenter.uniclub.network.dto.ClubPromotionRegisterRequestDto
import com.appcenter.uniclub.network.dto.ClubPromotionResponseDto
import com.appcenter.uniclub.network.dto.ClubMediaUploadRequestDto
import com.appcenter.uniclub.network.dto.PageClubResponseDto
import com.appcenter.uniclub.network.dto.S3PresignedRequestDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class ClubRepository(
    private val service: ClubService,
    private val okHttp: OkHttpClient,
    private val app: App
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

    suspend fun toggleFavorite(clubId: Long): Result<Unit> = runCatching {
        service.toggleFavorite(clubId)
        Unit
    }

    suspend fun getClubPromotion(clubId: Long): Result<ClubPromotionResponseDto> =
        runCatching { service.getClubPromotion(clubId) }


    suspend fun upsertClubPromotion(
        clubId: Long,
        body: ClubPromotionRegisterRequestDto
    ): Result<Unit> = runCatching {
        val res = service.upsertClubPromotion(clubId, body)
        if (!res.isSuccessful) error("Upsert failed: HTTP ${res.code()}")
    }

    private fun readBytes(uri: Uri): Pair<ByteArray, String> {
        val cr = app.contentResolver
        val mime = cr.getType(uri) ?: "image/*"
        val bytes = cr.openInputStream(uri)?.use { it.readBytes() }
            ?: error("이미지 읽기 실패: $uri")
        return bytes to mime
    }

    private fun putToS3(presignedUrl: String, bytes: ByteArray, contentType: String): Boolean {
        val body = bytes.toRequestBody(contentType.toMediaType())
        val req = Request.Builder().url(presignedUrl).put(body).build()
        okHttp.newCall(req).execute().use { resp -> return resp.isSuccessful }
    }

    suspend fun uploadClubImage(
        clubId: Long,
        localUri: Uri,
        filename: String,
        mediaType: String,
        main: Boolean
    ): Result<String> = runCatching {
        val (bytes, contentType) = readBytes(localUri)

        // 1) presigned
        val presigned = service.createPresignedUrl(
            clubId, listOf(S3PresignedRequestDto(filename))
        ).first()

        // 2) S3 PUT
        val ok = putToS3(presigned.presignedUrl, bytes, contentType)
        require(ok) { "S3 업로드 실패" }

        // 3) 서버 등록
        service.registerClubMedia(
            clubId,
            listOf(ClubMediaUploadRequestDto(mediaLink = presigned.filename, mediaType = mediaType, main = main))
        )

        presigned.filename // 서버 key 반환
    }
}