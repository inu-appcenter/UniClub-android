package com.appcenter.uniclub.network.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FcmRegisterRequestDto(
    @field:SerializedName("fcmToken")
    val fcmToken: String
)