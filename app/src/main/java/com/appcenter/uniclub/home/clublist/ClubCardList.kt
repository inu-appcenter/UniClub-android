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

//동아리 리스트 화면의 동아리 카드
@Composable
fun ClubCardList(club: Club) {
    //좋아요 상태 기억
    var isLiked by remember { mutableStateOf(false) }

    val bgColor = when (club.isRecruiting) {
        RecruitStatus.RECRUITING -> Color(0xFFCCCCCC) //모집중 색상
        RecruitStatus.UPCOMING -> Color(0xFFFFCCCC) //모집예정 색상
        RecruitStatus.CLOSED -> Color(0xFFEEEEEE) //모집마감 색상
    }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val buttonWidth = screenWidthDp * 329f / 360f
    val buttonHeight = screenHeightDp * 69f / 800f

    //카드 컨테이너
    Box(
        modifier = Modifier
            .width(buttonWidth.dp)
            .height(buttonHeight.dp)
            .padding(bottom = 12.dp) //카드 간 간격
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center //내부 내용 중앙 정렬
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, //수직 중앙
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            //동아리 이미지 영역
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
                        .size(54.dp)
                        .offset(y = 4.dp), //중앙 맞추기
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.BottomCenter
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            //동아리 이름, 추가정보 영역
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = club.name, //동아리 이름
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = club.description, //추가정보
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            //좋아요, 모집상태 영역
            Column(
                horizontalAlignment = Alignment.End, //우측 정렬
                verticalArrangement = Arrangement.Center
            ) {
                //좋아요 아이콘
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isLiked) "좋아요 취소" else "좋아요",
                    tint = if (isLiked) Color(0xFFF30000) else Color.White, //눌렀을 때 빨간색
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