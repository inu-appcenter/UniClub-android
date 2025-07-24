package com.appcenter.uniclub.home.clublist

import com.appcenter.uniclub.R

// 아직 서버 연결 전이므로 임시 데이터를 미리 넣어두기
val dummyClubs = listOf(
    Club("appcenter(앱센터)", "추가정보", RecruitStatus.CLOSED, true, R.drawable.club_list1),
    Club("동아리 1", "추가정보", RecruitStatus.UPCOMING, true, R.drawable.club_list2),
    Club("동아리 2", "추가정보", RecruitStatus.RECRUITING, false, R.drawable.club_list3),
    Club("동아리 3", "추가정보", RecruitStatus.CLOSED, false, R.drawable.club_list1),
    Club("동아리 4", "추가정보", RecruitStatus.CLOSED, true, R.drawable.club_list2),
    Club("appcenter(앱센터)", "추가정보", RecruitStatus.CLOSED, true, R.drawable.club_list1),
    Club("동아리 1", "추가정보", RecruitStatus.UPCOMING, true, R.drawable.club_list2),
    Club("동아리 2", "추가정보", RecruitStatus.RECRUITING, false, R.drawable.club_list3),
    Club("동아리 3", "추가정보", RecruitStatus.CLOSED, false, R.drawable.club_list1),
    Club("동아리 4", "추가정보", RecruitStatus.CLOSED, true, R.drawable.club_list2)
)