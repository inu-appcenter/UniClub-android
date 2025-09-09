package com.appcenter.uniclub.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.appcenter.uniclub.App
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.ui.theme.NotoSansKR
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.yield

//동아리 추천 카드캐러셀
@Composable
fun ClubCardCarousel(
    navController: NavHostController,
    vm: ClubCarouselViewModel
) {
    val state by vm.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    //화면에 보여줄 리스트(마지막 null은 로딩 카드)
    val displayList = remember(state.clubs, state.isRefreshing) {
        state.clubs + listOf(null)
    }

    // 마지막 아이템 도달 감지 → API 재호출
    LaunchedEffect(listState, state.isRefreshing, state.clubs) {
        snapshotFlow {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            last?.let {
                // 마지막 아이템이고, 오른쪽 끝까지 다 보였는지 체크
                (it.index == totalItems - 1) &&
                        (it.offset + it.size <= listState.layoutInfo.viewportEndOffset)
            } ?: false
        }
            .distinctUntilChanged() // 같은 값이면 무시
            .collectLatest { fullyVisible ->
                if (fullyVisible && !state.isRefreshing) {
                    vm.refresh()
                    coroutineScope.launch {
                        yield() // 한 프레임 양보
                        listState.scrollToItem(0) // 맨 앞으로 스크롤
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
        items(displayList) { item ->
            if (item == null) {
                // 새로고침 아이콘
                Box(
                    modifier = Modifier.figmaSize(widthPx = 136f, heightPx = 206f),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isRefreshing) CircularProgressIndicator(strokeWidth = 2.dp)
                }
            } else {
                RecommendClub(
                    imageUrl = item.imageUrl,
                    clubName = item.name,
                    isFavorite = item.favorite,
                    onClick = { navController.navigate("promotion") },
                    onToggleFavorite = { vm.onFavoriteClick(item.clubId) }
                )
            }
        }
    }
}

@Composable
fun RecommendClub(
    imageUrl: String?,
    clubName: String,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Box(
        modifier = Modifier
            .figmaSize(widthPx = 136f, heightPx = 206f)
            .clip(RoundedCornerShape(17.dp)) //모서리
            .clickable { onClick() }
    ) {
        if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = clubName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .figmaSize(widthPx = 136f, heightPx = 206f)
                    .background(Color.Gray) // 회색 배경
            )
        }


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
                    .clickable { onToggleFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = if (isFavorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
                    ),
                    contentDescription = if (isFavorite) "즐겨찾기 취소" else "즐겨찾기",
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(if (isFavorite) 1.6f else 1f) //빨간 하트 확대
                        .offset( //빨간 하트 위치조정
                            x = if (isFavorite) (-0.5).dp else 0.dp,
                            y = if (isFavorite) (-0.5).dp else 0.dp)
                )
            }
        }
    }
}