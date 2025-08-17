package com.appcenter.uniclub.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

@Composable
fun NotificationCard(item: NotificationItem, onClick: () -> Unit) {
    //알림 타입에 따라 카드 종류 분리
    when (item.type) {
        NotificationType.INTERESTED_CLUB -> LargeNotificationCard(item, onClick)
        else -> SmallNotificationCard(item, onClick)
    }
}

//관심 동아리 알림 (큰 카드)
@Composable
fun LargeNotificationCard(item: NotificationItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .figmaSize(widthPx = 350f, heightPx = 100f)
            .clickable { onClick() }
    ) {
        //카드 배경 이미지
        Image(
            painter = painterResource(R.drawable.bg_notification_large),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        //카드 내용
        Row(
            modifier = Modifier
                .fillMaxSize()
                .figmaPadding(startPx = 25f, topPx = 15f, endPx = 30f),
            verticalAlignment = Alignment.Top
        ) {
            Image( //아이콘
                painter = painterResource(R.drawable.ic_favorite_filled),
                contentDescription = null,
                modifier = Modifier.figmaSize(widthPx = 17f, heightPx = 15f)
            )
            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text( //카드 제목
                    text = "관심 동아리",
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 11.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color(0xFF929292)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text( //알림 내용
                    text = item.title,
                    fontSize = figmaTextSizeSp(12f),
                    fontFamily = NotoSansKR,
                    lineHeight = 12.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = item.description,
                    fontSize = figmaTextSizeSp(10f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 10.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color(0xFFFF4E00)
                )
            }
            //시간 표시
            Text(text = item.time, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

//질문 답변, 총동연 공지 등 (작은 카드)
@Composable
fun SmallNotificationCard(item: NotificationItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .figmaSize(widthPx = 350f, heightPx = 80f)
            .clickable { onClick() }
    ) {
        //카드 배경 이미지
        Image(
            painter = painterResource(R.drawable.bg_notification_small),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        //카드 내용
        Row(
            modifier = Modifier
                .fillMaxSize()
                .figmaPadding(startPx = 25f, topPx = 15f, endPx = 30f),
            verticalAlignment = Alignment.Top
        ) {
            //아이콘, 라벨
            val (iconRes, label) = when (item.type) {
                NotificationType.ANSWER_RECEIVED -> R.drawable.ic_favorite_filled to "질의응답"
                NotificationType.NOTICE -> null to "총동연" //아이콘 없음
                else -> null to "기타"
            }
            if (iconRes != null) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.figmaSize(widthPx = 17f, heightPx = 15f)
                )
            } else {
                Spacer(modifier = Modifier.figmaSize(widthPx = 17f, heightPx = 15f)) // 🔹 아이콘 없을 때 여백
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Text( //카드 제목
                    text = label,
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 11.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color(0xFF929292)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text( //알림 내용
                    text = item.title,
                    fontSize = figmaTextSizeSp(12f),
                    fontFamily = NotoSansKR,
                    lineHeight = 12.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black
                )
            }

            Text(text = item.time, fontSize = 11.sp, color = Color.Gray)
        }
    }
}