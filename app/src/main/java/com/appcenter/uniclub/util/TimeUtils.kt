package com.appcenter.uniclub.util

import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.Duration

object TimeUtils {

    //화면 표시용
    private val displayFormatter = DateTimeFormatter.ofPattern("MM.dd  HH:mm")

    private val serverDateFormatterNoMicro = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    private val serverDateFormatterMicro = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")

    //마이크로초 자리수가 0~6 자리로 와도 처리
    fun parseServerDateTime(time: String?): LocalDateTime? {
        if (time.isNullOrBlank()) return null

        return try {
            // ===== 마이크로초 길이 정규화 =====
            val fixed = normalizeMicroseconds(time)

            LocalDateTime.parse(fixed, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"))
        } catch (e: Exception) {
            null
        }
    }

    //서버 시간 문자열의 마이크로초(.xxxxx) 길이를 6자리로 맞추기.
    private fun normalizeMicroseconds(raw: String): String {
        //'.' 이 없는 경우 → 마이크로초 없음
        if (!raw.contains(".")) {
            return raw + ".000000"
        }

        val parts = raw.split(".")
        if (parts.size != 2) return raw //비정상일 경우 그대로

        val prefix = parts[0] // yyyy-MM-ddTHH:mm:ss
        val micro = parts[1] // 마이크로초 부분

        //마이크로초가 6자리보다 작으면 0을 뒤에 채워넣기
        return if (micro.length < 6) {
            prefix + "." + micro.padEnd(6, '0')
        }
        //6자리 이상이면 앞의 6자리만 사용
        else {
            prefix + "." + micro.substring(0, 6)
        }
    }

    fun toFormattedTime(time: String?): String {
        val dt = parseServerDateTime(time) ?: return ""
        return dt.format(displayFormatter)
    }

    //상대 시간 ("n분 전", "n시간 전")
    fun toRelativeTime(time: String?): String {
        val serverTime = parseServerDateTime(time) ?: return (time ?: "")

        val now = LocalDateTime.now(ZoneId.systemDefault())
        val diff = Duration.between(serverTime, now)

        val minutes = diff.toMinutes()
        val hours = diff.toHours()
        val days = diff.toDays()

        return when {
            minutes < 1 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days < 7 -> "${days}일 전"
            else -> "${days}일 전"
        }
    }
}
