package com.appcenter.uniclub.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize

//메인상단바: 로고, 검색, 알림 아이콘 설정
@Composable
fun MainTopBar(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 22f, topPx = 18f),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //로고 이미지
        Image(
            painter = painterResource(id = R.drawable.main_logo),
            contentDescription = "로고",
            modifier = Modifier.figmaSize(widthPx = 101f, heightPx = 24f)
        )

        Spacer(modifier = Modifier.figmaPadding(startPx = 143f)) //로고-아이콘 사이 간격

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            //검색 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "검색",
                modifier = Modifier.figmaSize(widthPx = 24f, heightPx = 23f)
            )

            Spacer(modifier = Modifier.figmaPadding(startPx = 22f)) //아이콘 사이 간격

            //알림 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_notification),
                contentDescription = "알림",
                modifier = Modifier.figmaSize(widthPx = 20f, heightPx = 21f)
            )
        }
    }
}