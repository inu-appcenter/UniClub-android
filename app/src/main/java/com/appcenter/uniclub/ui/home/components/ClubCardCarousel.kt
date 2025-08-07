package com.appcenter.uniclub.ui.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

//동아리 추천 카드캐러셀
//현재는 카드 6개만
//추후 서버 연동 후 랜덤 값 불러오기, 새로고침 기능 등 추가해야함
@Composable
fun ClubCardCarousel(
    fullList: List<Pair<Int, String>>,
) {
    var clubList by remember { mutableStateOf(fullList.shuffled()) }
    var isRefreshing by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val visibleClubs = clubList.take(6)
    val displayList: List<Pair<Int, String>?> = visibleClubs + listOf(null) // 마지막 null은 로딩 카드

    // 마지막 아이템 도달 감지
    LaunchedEffect(listState, clubList) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastIndex = visibleItems.lastOrNull()?.index
                if (!isRefreshing && lastIndex == displayList.lastIndex) {
                    isRefreshing = true
                    coroutineScope.launch {
                        delay(1000L)
                        clubList = fullList.shuffled()
                        isRefreshing = false
                        listState.scrollToItem(0)
                    }
                }
            }
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val startPadding = (18f / 360f) * screenWidthDp
    val spacing = (15f / 360f) * screenWidthDp

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(start = startPadding.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing.dp) //카드 간 간격
    ) {
        items(displayList) { pair ->
            if (pair == null) {
                // 새로고침 아이콘
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 136f, heightPx = 206f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                }
            } else {
                ClubCard(imageResId = pair.first, clubName = pair.second)
            }
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

    Box(
        modifier = Modifier
            .figmaSize(widthPx = 136f, heightPx = 206f)
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
                .figmaPadding(startPx = 14f, topPx = 10f)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //동아리 이름
            Text(
                text = clubName,
                color = Color.White,
                fontSize = figmaTextSizeSp(13f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Medium,
                lineHeight = 13.sp * 1.5f, //행간
                letterSpacing = (-0.011).em, //자간
            )
            //즐겨찾기 아이콘
            Box(
                modifier = Modifier
                    .figmaSize(widthPx = 28f, heightPx = 28f) //고정 박스 크기
                    .figmaPadding(endPx = 14f) //오른쪽 여백
                    .clickable { isLiked = !isLiked },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = if (isLiked) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
                    ),
                    contentDescription = if (isLiked) "즐겨찾기 취소" else "즐겨찾기",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(if (isLiked) 1.6f else 1f) //빨간 하트 확대
                        .offset( //빨간 하트 위치조정
                            x = if (isLiked) (-0.5).dp else 0.dp,
                            y = if (isLiked) (-0.5).dp else 0.dp)
                )
            }
        }
    }
}