package com.appcenter.uniclub.util

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId

object TimeUtils {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun formatRelativeTime(createdAt: String): String {
        return try {
            val created = LocalDateTime.parse(createdAt, formatter)
            val now = LocalDateTime.now(ZoneId.systemDefault())

            val duration = Duration.between(created, now)
            val minutes = duration.toMinutes()
            val hours = duration.toHours()
            val days = duration.toDays()

            when {
                minutes < 1 -> "방금 전"
                minutes < 60 -> "${minutes}분 전"
                hours < 24 -> "${hours}시간 전"
                days < 7 -> "${days}일 전"
                else -> created.toLocalDate().toString() // 오래된 건 날짜 표시
            }
        } catch (e: Exception) {
            createdAt // 파싱 실패하면 원본 그대로
        }
    }
}
