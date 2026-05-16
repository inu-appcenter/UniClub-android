package com.appcenter.uniclub.ui.home

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
import coil.compose.AsyncImage
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.google.accompanist.pager.*
import kotlinx.coroutines.delay

//이벤트 칸 카드뉴스 슬라이드형
@OptIn(ExperimentalPagerApi::class)
@Composable
fun EventImageCarousel(
    eventList: List<Any> //여러 장 이미지 받기
) {
    val pagerState = rememberPagerState()

    //자동 슬라이드
    LaunchedEffect(eventList.size) {
        if (eventList.size <= 1) return@LaunchedEffect

        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % eventList.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

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
                when (val item = eventList[page]) {
                    is Int -> {
                        //로컬 Drawable
                        Image(
                            painter = painterResource(id = item),
                            contentDescription = "배너 이미지",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is String -> {
                        //서버 URL (Coil 사용)
                        AsyncImage(
                            model = item,
                            contentDescription = "배너 이미지",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
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
