package com.appcenter.uniclub.data

import android.content.Context
import android.net.Uri
import com.appcenter.uniclub.network.ProfileService
import com.appcenter.uniclub.network.dto.S3PresignedRequestDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileRepository(
    private val service: ProfileService
) {
    suspend fun uploadProfileImage(context: Context, uri: Uri): String {
        // 1. 파일 이름 생성
        val filename = "profile_${System.currentTimeMillis()}.jpg"

        // 2. presigned URL 요청
        val presigned = service.getPresignedUrl(S3PresignedRequestDto(filename))

        // 3. 로컬 파일 → 바이트 변환
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes() ?: throw IllegalArgumentException("파일 읽기 실패")
        inputStream.close()

        // 4. S3 presigned URL 로 업로드
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(presigned.presignedUrl)
            .put(bytes.toRequestBody("image/jpeg".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) throw RuntimeException("S3 업로드 실패")

        // 5. 업로드된 파일 경로 반환 (CDN URL or S3 URL)
        return presigned.presignedUrl.substringBefore("?") // ?쿼리 스트링 제외
    }
}
