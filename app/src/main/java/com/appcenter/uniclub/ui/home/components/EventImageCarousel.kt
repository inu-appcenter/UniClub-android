package com.appcenter.uniclub.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.google.accompanist.pager.*

//이벤트 칸 카드뉴스 슬라이드형
@OptIn(ExperimentalPagerApi::class)
@Composable
fun EventImageCarousel(
    eventList: List<Int> //여러 장 이미지 받기
) {
    val pagerState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 18f, endPx = 18f, topPx = 30f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .figmaSize(widthPx = 324f, heightPx = 249f)
                .clip(RoundedCornerShape(21.dp)) //모서리
                .clipToBounds() //내용 넘치지 않도록 자르기
        ) {
            HorizontalPager(
                count = eventList.size, //이미지 개수
                state = pagerState, //슬라이드 상태 연결
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp),
                itemSpacing = 0.dp
            ) { page ->
                Image(
                    painter = painterResource(id = eventList[page]),
                    contentDescription = "이벤트 이미지",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            //인디케이터 (박스 내부 하단 중앙) ...표시
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )
        }
    }
}
