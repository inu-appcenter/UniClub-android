package com.appcenter.uniclub.ui.components.bottombar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Help
import androidx.compose.ui.graphics.vector.ImageVector

//하단바에서 사용될 각 탭의 정보를 한곳에 모아두는 sealed class
sealed class Navigation(val route: String, val icon: ImageVector, val label: String) {
    object QnA    : Navigation("qna",    Icons.Filled.Help,   "Q&A")
    object Home   : Navigation("home",   Icons.Default.Home,   "홈")
    object MyPage : Navigation("mypage", Icons.Default.Person, "MY")
}