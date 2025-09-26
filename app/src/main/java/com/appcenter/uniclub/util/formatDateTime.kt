package com.appcenter.uniclub.util

import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale

fun formatDateTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "-"
    return try {
        val parsed = LocalDateTime.parse(isoString) // e.g. 2025-09-09T14:30:00

        val hourPart = parsed.format(DateTimeFormatter.ofPattern("M월 d일 a h시", Locale.KOREAN))
            .replace("AM", "오전")
            .replace("PM", "오후")

        val minute = parsed.minute
        if (minute == 0) {
            hourPart
        } else {
            "$hourPart ${minute}분"
        }
    } catch (e: Exception) {
        isoString
    }
}

