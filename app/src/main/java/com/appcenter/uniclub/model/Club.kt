package com.appcenter.uniclub.model

data class Club(
    val id: Long, //아이디
    val name: String, //이름
    val info: String?, //한줄소개
    val status: String?, //모집현황
    val favorite: Boolean, //관심동아리
    val category: ClubCategory, //카테고리
    val profileUrl: String? = null //프로필 이미지
)