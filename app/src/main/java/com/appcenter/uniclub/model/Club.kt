package com.appcenter.uniclub.model

data class Club(
    val id: Long,
    val name: String,
    val info: String,
    val status: String,
    val favorite: Boolean,
    val category: ClubCategory,
    val profileUrl: String? = null
)