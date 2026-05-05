package com.appcenter.uniclub.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield

//동아리 추천 카드캐러셀
@Composable
fun ClubCardCarousel(
    navController: NavHostController,
    vm: ClubCarouselViewModel
) {
    val state by vm.uiState.collectAsState()
    val listState = rememberLazyListState()

    var showLoadingCard by remember { mutableStateOf(false) }
    var isCarouselWorking by remember { mutableStateOf(false) }

    val displayList = remember(state.clubs) {
        state.clubs + listOf(null)
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val lastClubIndex = state.clubs.lastIndex

            if (lastClubIndex < 0) {
                false
            } else {
                listState.layoutInfo.visibleItemsInfo.any { item ->
                    item.index == lastClubIndex &&
                            item.offset + item.size <= listState.layoutInfo.viewportEndOffset
                }
            }
        }
            .distinctUntilChanged()
            .collectLatest { lastClubFullyVisible ->
                if (
                    lastClubFullyVisible &&
                    !state.isRefreshing &&
                    !isCarouselWorking
                ) {
                    isCarouselWorking = true

                    try {
                        //마지막 카드 잠깐 보여주기
                        delay(800)

                        //로딩 카드로 이동
                        showLoadingCard = true
                        listState.animateScrollToItem(state.clubs.size)

                        delay(300)

                        //API 재호출
                        vm.refresh()

                        //refresh 시작 감지
                        withTimeoutOrNull(1000) {
                            snapshotFlow { state.isRefreshing }
                                .first { it }
                        }

                        //refresh 종료 감지
                        withTimeoutOrNull(5000) {
                            snapshotFlow { state.isRefreshing }
                                .first { !it }
                        }

                        //무조건 맨 처음 카드로 이동
                        listState.scrollToItem(0)

                        delay(300)
                    } finally {
                        showLoadingCard = false
                        isCarouselWorking = false
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
        horizontalArrangement = Arrangement.spacedBy(spacing.dp)
    ) {
        items(displayList) { item ->
            if (item == null) {
                Box(
                    modifier = Modifier.figmaSize(widthPx = 136f, heightPx = 206f),
                    contentAlignment = Alignment.Center
                ) {
                    if (showLoadingCard || state.isRefreshing) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                }
            } else {
                RecommendClub(
                    imageUrl = item.imageUrl,
                    clubName = item.name,
                    isFavorite = item.favorite,
                    onClick = { navController.navigate("promotion/${item.clubId}") },
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
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(17.dp),
                clip = false
            )
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
            Image(
                painter = painterResource(R.drawable.bg_carousel),
                contentDescription = null,
                modifier = Modifier.matchParentSize()
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(Color(0xFF141414).copy(alpha = 0.45f))
                .align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .figmaPadding(startPx = 14f, topPx = 5f)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //동아리 이름
                Text(
                    text = clubName,
                    modifier = Modifier
                        .weight(1f)
                        .basicMarquee(),
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
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onToggleFavorite() },
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
}