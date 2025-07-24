package com.appcenter.uniclub.home.clublist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ClubCardList(club: Club) {
    var isLiked by remember { mutableStateOf(false) }

    val bgColor = when (club.isRecruiting) {
        RecruitStatus.RECRUITING -> Color(0xFFCCCCCC)
        RecruitStatus.UPCOMING -> Color(0xFFFFCCCC)
        RecruitStatus.CLOSED -> Color(0xFFEEEEEE)
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val buttonWidth = screenWidthDp * 329f / 360f
    val buttonHeight = screenHeightDp * 69f / 800f

    Box(
        modifier = Modifier
            .width(buttonWidth.dp)
            .height(buttonHeight.dp)
            .padding(bottom = 12.dp)             // 카드 사이 간격
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center      // 내부 Row 중앙정렬
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = club.imageResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(54.dp)                   // 가로세로 같은 사이즈로 통일
                        .offset(y = 4.dp),              // 부모 Row 높이에 맞추고
                    contentScale = ContentScale.Crop,  // 잘라내서(container 꽉 채우기)
                    alignment = Alignment.BottomCenter     // 중앙 정렬
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = club.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = club.description,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "좋아요 취소" else "좋아요",
                    tint = if (isLiked) Color(0xFFF30000) else Color.White,
                    modifier = Modifier
                        .size(width = 15.dp, height = 13.dp)
                        .clickable { isLiked = !isLiked }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (club.isRecruiting) {
                        RecruitStatus.RECRUITING -> "모집중"
                        RecruitStatus.UPCOMING -> "모집예정"
                        RecruitStatus.CLOSED -> "모집마감"
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
            }
        }
    }
}