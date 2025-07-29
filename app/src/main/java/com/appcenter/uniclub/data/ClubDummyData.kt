package com.appcenter.uniclub.data

import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.Club
import com.appcenter.uniclub.model.RecruitStatus

// 아직 서버 연결 전이므로 임시 데이터 미리 넣어두기
val dummyClubs = listOf(
    Club("appcenter(앱센터)", "추가정보", RecruitStatus.CLOSED, true, R.drawable.club1),
    Club("동아리 1", "추가정보", RecruitStatus.UPCOMING, true),
    Club("동아리 2", "추가정보", RecruitStatus.RECRUITING, false),
    Club("동아리 3", "추가정보", RecruitStatus.CLOSED, false, R.drawable.club2),
    Club("동아리 4", "추가정보", RecruitStatus.CLOSED, true),
    Club("appcenter(앱센터)", "추가정보", RecruitStatus.CLOSED, true),
    Club("동아리 1", "추가정보", RecruitStatus.UPCOMING, true),
    Club("동아리 2", "추가정보", RecruitStatus.RECRUITING, false),
    Club("동아리 3", "추가정보", RecruitStatus.CLOSED, false),
    Club("동아리 4", "추가정보", RecruitStatus.CLOSED, true)
)