package com.appcenter.uniclub.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

//상단바: 로고, 검색, 알림 아이콘 설정
@Composable
fun TopAppBarSection(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp), //좌우,상하 간격
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //임시 로고 텍스트 (나중에 이미지로 대체)
        Text(
            text = "로고",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0167FF),
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            //우선 기본 아이콘 설정
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "검색",
                modifier = Modifier.size(24.dp)
            )

            // 두 아이콘 사이 간격
            Spacer(modifier = Modifier.padding(start = 12.dp))

            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "알림",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}