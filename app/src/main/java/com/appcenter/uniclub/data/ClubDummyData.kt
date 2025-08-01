package com.appcenter.uniclub.data

import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.Club
import com.appcenter.uniclub.model.ClubCategory
import com.appcenter.uniclub.model.RecruitStatus

// 아직 서버 연결 전이므로 임시 데이터 미리 넣어두기
val dummyClubs = listOf(
    Club("appcenter(앱센터)", "추가정보", RecruitStatus.CLOSED, true, ClubCategory.ACADEMIC, R.drawable.club1),
    Club("동아리 1", "추가정보", RecruitStatus.UPCOMING, true, ClubCategory.HOBBY),
    Club("동아리 2", "추가정보", RecruitStatus.RECRUITING, false, ClubCategory.SPORTS),
    Club("동아리 3", "추가정보", RecruitStatus.CLOSED, false, ClubCategory.RELIGION, R.drawable.club2),
    Club("동아리 4", "추가정보", RecruitStatus.CLOSED, true, ClubCategory.VOLUNTEER),
    Club("동아리 5", "추가정보", RecruitStatus.CLOSED, true, ClubCategory.CULTURE),
    Club("동아리 6", "추가정보", RecruitStatus.UPCOMING, true, ClubCategory.SPORTS),
    Club("동아리 7", "추가정보", RecruitStatus.RECRUITING, false, ClubCategory.HOBBY),
    Club("동아리 8", "추가정보", RecruitStatus.CLOSED, false, ClubCategory.ACADEMIC),
    Club("동아리 9", "추가정보", RecruitStatus.CLOSED, true, ClubCategory.RELIGION)
)