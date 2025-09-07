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
        val (bytes, contentType) = readBytes(localUri) //로컬 파일 바이트 배열로

        //서버에 presigned URL 요청
        val presigned = service.createPresignedUrl(
            clubId, listOf(S3PresignedRequestDto(filename))
        ).first()

        //presigned URL 사용해 S3에 PUT 업로드
        val ok = putToS3(presigned.presignedUrl, bytes, contentType)
        require(ok) { "S3 업로드 실패" }

        //서버에 업로드된 파일 메타데이터 등록
        service.registerClubMedia(
            clubId,
            listOf(ClubMediaUploadRequestDto(mediaLink = presigned.filename, mediaType = mediaType, main = main))
        )

        presigned.filename //서버 key 반환
    }
}