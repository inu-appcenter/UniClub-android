package com.appcenter.uniclub.data

import android.content.Context
import android.net.Uri
import com.appcenter.uniclub.App
import com.appcenter.uniclub.network.ClubService
import com.appcenter.uniclub.network.dto.ClubPromotionRegisterRequestDto
import com.appcenter.uniclub.network.dto.ClubPromotionResponseDto
import com.appcenter.uniclub.network.dto.ClubMediaUploadRequestDto
import com.appcenter.uniclub.network.dto.PageClubResponseDto
import com.appcenter.uniclub.network.dto.S3PresignedRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/*
-서버의 club api를 호출하여 데이터를 가져오거나 수정하는 역할을 담당
-ui와 서버 api를 이어주는 데이터 계층
 */
class ClubRepository(
    private val service: ClubService, //Retrofit을 통해 서버와 통신하는 service
    private val okHttp: OkHttpClient, //OkHttp 클라이언트 (S3 업로드용)
    private val app: App
) {
    //페이지네이션 상태
    private var lastCursorName: String? = null //마지막으로 불러온 클럽의 name
    private var hasNext: Boolean = true //다음 페이지가 존재하는지 여부
    private var currentSortBy: String = "name" //현재 정렬 기준 (기본: "name")
    private var currentCategory: String? = null //현재 선택된 카테고리
    private val pageSize = 10

    suspend fun fetchClubs(
        category: String?, //null이면 전체
        sortBy: String,
        reset: Boolean = false
    ): PageClubResponseDto {
        //정렬, 카테고리 변경 시 상태 초기화
        if (reset || currentSortBy != sortBy || currentCategory != category) {
            lastCursorName = null
            hasNext = true
            currentSortBy = sortBy
            currentCategory = category
        }
        if (!hasNext) { //다음 페이지 없으면 빈 결과
            return PageClubResponseDto(content = emptyList(), hasNext = false)
        }

        val response = service.getClubs( //서버 api 호출
            category = currentCategory,
            sortBy = currentSortBy,
            cursorName = lastCursorName,
            size = pageSize
        )

        //다음 페이지 커서 갱신
        lastCursorName = response.content.lastOrNull()?.name
        hasNext = response.hasNext
        return response
    }

    fun canLoadMore(): Boolean = hasNext //더 불러올 수 있는 상태인지 확인
    fun resetPaging() { //페이지네이션 상태 초기화
        lastCursorName = null
        hasNext = true
    }

    //즐겨찾기 토글 api
    suspend fun toggleFavorite(clubId: Long): Result<Unit> = runCatching {
        service.toggleFavorite(clubId)
        Unit
    }

    //동아리 홍보 페이지 조회
    suspend fun getClubPromotion(clubId: Long): Result<ClubPromotionResponseDto> =
        runCatching { service.getClubPromotion(clubId) }


    //동아리 홍보 페이지 수정
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

    private suspend fun putToS3(
        presignedUrl: String,
        bytes: ByteArray,
        contentType: String
    ): Boolean = withContext(Dispatchers.IO) {
        val body = bytes.toRequestBody(contentType.toMediaType())
        val req = Request.Builder()
            .url(presignedUrl)
            .put(body)
            .build()

        okHttp.newCall(req).execute().use { resp ->
            android.util.Log.d("ClubRepo", "S3 업로드 응답 코드=${resp.code}")
            resp.isSuccessful
        }
    }

    private fun extFromMime(mime: String): String = when (mime.lowercase()) {
        "image/jpeg" -> "jpg"
        "image/png" -> "png"
        "image/webp" -> "webp"
        "image/heic", "image/heif" -> "heic"
        else -> "dat" // 알 수 없는 경우 안전하게 dat 처리
    }

    suspend fun uploadClubImage(
        clubId: Long,
        localUri: Uri,
        mediaType: String,
        main: Boolean,
        context: Context
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            try {
                // mimeType 추출
                val contentType = context.contentResolver.getType(localUri) ?: "application/octet-stream"
                val ext = extFromMime(contentType)
                val filename = "${mediaType.lowercase()}_${System.currentTimeMillis()}.$ext"

                // presigned 발급
                val presigned = service.createPresignedUrl(
                    clubId, listOf(S3PresignedRequestDto(filename))
                ).first()
                android.util.Log.d("ClubRepo", "2️⃣ presigned 발급 완료 → ${presigned.filename}")

                // 로컬 파일 읽기
                val inputStream = context.contentResolver.openInputStream(localUri)
                    ?: error("이미지 읽기 실패: $localUri")
                val bytes = inputStream.use { it.readBytes() }
                android.util.Log.d("ClubRepo", "1️⃣ readBytes 완료 (${bytes.size} bytes)")

                // S3 업로드 (이제는 IO 스레드에서 실행됨)
                val req = Request.Builder()
                    .url(presigned.presignedUrl)
                    .put(bytes.toRequestBody(contentType.toMediaType()))
                    .build()

                okHttp.newCall(req).execute().use { resp ->
                    android.util.Log.d("ClubRepo", "3️⃣ S3 업로드 응답 코드=${resp.code}")
                    if (!resp.isSuccessful) error("S3 업로드 실패 code=${resp.code}")
                }

                // 서버에 업로드 정보 등록
                val cleanPath = presigned.presignedUrl
                    .substringAfter("uploads/")
                    .substringBefore("?")
                val finalPath = "uploads/$cleanPath"

                val reqBody = ClubMediaUploadRequestDto(
                    mediaLink = finalPath,
                    mediaType = mediaType,
                    main = main
                )
                service.registerClubMedia(clubId, listOf(reqBody))
                android.util.Log.d("ClubRepo", "4️⃣ /upload 등록 완료 → $finalPath, mediaType=$mediaType, main=$main")

                finalPath
            } catch (e: Exception) {
                android.util.Log.e("ClubRepo", "❌ 업로드 실패: ${e.message}", e)
                throw e
            }
        }
    }
}