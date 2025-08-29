package com.appcenter.uniclub.network.dto

// 요청
data class S3PresignedRequestDto(
    val filename: String
)

// 응답
data class S3PresignedResponseDto(
    val filename: String,
    val presignedUrl: String
)