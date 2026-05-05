package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

//요청
@Keep
data class S3PresignedRequestDto(
    @field:SerializedName("filename") val filename: String
)

//응답
@Keep
data class S3PresignedResponseDto(
    @field:SerializedName("filename") val filename: String,
    @field:SerializedName("presignedUrl") val presignedUrl: String
)