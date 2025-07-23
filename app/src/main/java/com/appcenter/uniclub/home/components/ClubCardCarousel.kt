package com.appcenter.uniclub.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration

//동아리 추천 카드캐러셀
//현재는 카드 6개만
//추후 서버 연동 후 랜덤 값 불러오기, 새로고침 기능 등 추가해야함
@Composable
fun ClubCardCarousel(
    clubList: List<Pair<Int, String>>
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp) //카드 간 간격
    ) {
        //6개까지 표시
        items(items = clubList.take(6)) { pair ->
            ClubCard(imageResId = pair.first, clubName = pair.second)
        }
    }
}

@Composable
fun ClubCard(
    imageResId: Int,
    clubName: String
) {

    //좋아요 상태 기억
    var isLiked by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val buttonWidth = screenWidthDp * 137f / 360f
    val buttonHeight = screenWidthDp * 206f / 360f
    Box(
        modifier = Modifier
            .width(buttonWidth.dp) //고정 너비
            .height(buttonHeight.dp) //고정 높이
            .clip(RoundedCornerShape(17.dp)) //모서리
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = clubName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //동아리 이름
            Text(
                text = clubName,
                color = Color.White,
                fontSize = 13.sp
                //fontWeight = FontWeight.Bold
            )
            //좋아요 아이콘
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isLiked) "좋아요 취소" else "좋아요",
                tint = if (isLiked) Color(0xFFF30000) else Color.White, //채워졌을 때 빨간색
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable { isLiked = !isLiked }
            )
        }
    }
}
