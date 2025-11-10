package com.appcenter.uniclub.ui.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.NotificationItem
import com.appcenter.uniclub.model.NotificationType
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp

//알림 페이지 알림 카드
@Composable
fun NotificationCard(item: NotificationItem, onClick: () -> Unit) {
    //타입별로 카드 최소 높이 보장, 내용에 따라 더 커질 수 있게
    val minH: Dp = when (item.type) {
        NotificationType.CLUB,
        NotificationType.QNA -> 90.dp
        else -> 73.dp
    }
    //공통 컨테이너 (그림자,라운드,클릭 처리 등)
    NotificationCardContainer(minHeight = minH, onClick = onClick) {
        NotificationCardContent(item)
    }
}

//카드 공통 외곽
@Composable
private fun NotificationCardContainer(
    minHeight: Dp, //최소 높이
    cornerRadius: Dp = 20.dp,
    shadowElevation: Dp = 8.dp, //그림자 높이
    shadowColor: Color = Color(0x20000000), //그림자 색
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit //실제 내부 콘텐츠
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight) //최소 높이 보장
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(cornerRadius), //그림자도 라운드
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) { onClick() }
            .padding(start = 20.dp, top = 12.dp, end = 16.dp, bottom = 10.dp), //내부 패딩
        content = content
    )
}

//카드 내부 콘텐츠
@Composable
private fun NotificationCardContent(item: NotificationItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        //아이콘 + 라벨
        val (iconRes, label) = when (item.type) {
            NotificationType.CLUB -> R.drawable.ic_notify_favorite to "관심 동아리"
            NotificationType.QNA -> R.drawable.ic_notify_qna to "질의응답"
            NotificationType.FEDERATION -> R.drawable.ic_notify_federation to "총동연"
            NotificationType.SYSTEM ->  R.drawable.ic_notify_system to "시스템"
            NotificationType.PERSONAL -> null to "개인"
        }

        if (iconRes != null) { //아이콘 크기
            Box(modifier = Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier
                        .figmaSize(15f, 13f)
                        .offset(y=2.dp),
                    contentScale = ContentScale.Fit
                )
            }
        } else { //아이콘 없는 타입
            Spacer(Modifier.size(18.dp))
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text( //알림 제목
                text = label,
                fontSize = figmaTextSizeSp(11f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Medium,
                lineHeight = 11.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color(0xFF929292)
            )
            Spacer(Modifier.height(3.dp))

            Text( //알림 내용
                text = item.title,
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black
            )

            //하단 액션 텍스트
            val actionText: String? = when (item.type) {
                NotificationType.CLUB -> "지원하기"
                NotificationType.QNA -> "이동하기"
                else -> null
            }
            if (actionText != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = actionText,
                    fontSize = figmaTextSizeSp(10f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 10.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color(0xFFFF4E00)
                )
            }
        }

        //시간
        Text(
            text = item.time,
            fontSize = figmaTextSizeSp(11f),
            fontFamily = NotoSansKR,
            lineHeight = 11.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color(0xFF9D9D9D),
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}