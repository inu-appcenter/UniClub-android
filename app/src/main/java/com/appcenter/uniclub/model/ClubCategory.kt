package com.appcenter.uniclub.model

import com.appcenter.uniclub.R

enum class ClubCategory(
    val displayName: String,
    val serverValues: List<String> // 여러 서버 값 허용
) {
    ACADEMIC("교양학술", listOf("LIBERAL_ACADEMIC", "ACADEMIC")),
    HOBBY("취미전시", listOf("HOBBY_EXHIBITION", "HOBBY")),
    SPORTS("체육", listOf("SPORTS")),
    RELIGION("종교", listOf("RELIGION")),
    VOLUNTEER("봉사", listOf("VOLUNTEER")),
    CULTURE("문화", listOf("CULTURE"));

    val primaryServerValue: String
        get() = serverValues.first() // 서버로 보낼 기본 값

    companion object {
        fun fromDisplayName(displayName: String): ClubCategory? =
            values().find { it.displayName == displayName }

        fun fromServerValue(serverValue: String): ClubCategory? =
            values().find { it.serverValues.contains(serverValue) }
    }
}

fun ClubCategory.getIconRes(): Int? = when (this) {
    ClubCategory.ACADEMIC -> R.drawable.ic_category_academic
    ClubCategory.HOBBY -> R.drawable.ic_category_hobby
    ClubCategory.SPORTS -> R.drawable.ic_category_sports
    ClubCategory.RELIGION -> R.drawable.ic_category_religion
    ClubCategory.VOLUNTEER -> R.drawable.ic_category_volunteer
    ClubCategory.CULTURE -> R.drawable.ic_category_culture
}