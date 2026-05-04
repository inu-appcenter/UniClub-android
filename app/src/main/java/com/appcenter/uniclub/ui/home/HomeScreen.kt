package com.appcenter.uniclub.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appcenter.uniclub.App
import com.appcenter.uniclub.R
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.model.ClubCategory
import com.appcenter.uniclub.model.getIconRes
import com.appcenter.uniclub.ui.notification.NotificationBadgeViewModel
import com.appcenter.uniclub.ui.notification.NotificationBadgeViewModelFactory
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    bottomNavController: NavHostController,
    rootNavController: NavHostController,
    app: App = LocalContext.current.applicationContext as App
) {
    val homeVm: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(ServiceLocator.mainRepository(app))
    )
    val clubVm: ClubCarouselViewModel = viewModel(
        factory = ClubCarouselViewModelFactory(
            mainRepo = ServiceLocator.mainRepository(app),
            clubRepo = ServiceLocator.clubRepository(app)
        )
    )
    val badgeVm: NotificationBadgeViewModel = viewModel(
        factory = NotificationBadgeViewModelFactory(ServiceLocator.notificationRepository(app))
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                badgeVm.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    val bannerList by homeVm.bannerList.collectAsState() //로컬 fallback
    val remoteBanner by homeVm.remoteBanner.collectAsState() //서버 배너
    val hasUnread by badgeVm.hasUnread.collectAsState(initial = false)

    LazyColumn(modifier = modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        item { MainTopBar(navController = bottomNavController, rootNavController = rootNavController, hasUnread = hasUnread) }

        item {
            if (remoteBanner.isNotEmpty()) { //서버에서 이미지가 왔을 때
                EventImageCarousel(eventList = remoteBanner.map { it.mediaLink })
            } else { //서버에서 이미지가 없을 때 → 로컬 기본 이미지 사용
                val localList: List<Int> =
                    if (bannerList.isNotEmpty()) bannerList else listOf(R.drawable.bg_event_default)
                EventImageCarousel(
                    eventList = localList
                )
            }
        }

        item {
            RecommendTitle()
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            ClubCardCarousel(navController = bottomNavController, vm = clubVm)
        }

        item {
            CategorySection(navController = bottomNavController) { category ->
                bottomNavController.navigate("clublist/$category")
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RecommendTitle() {
    Text(
        text = "이런 동아리는 어떠세요?",
        fontSize = figmaTextSizeSp(16f),
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp * 1.5f, //행간
        letterSpacing = (-0.03).em, //자간
        color = Color(0xFF000000),
        modifier = Modifier
            .figmaPadding(startPx = 26f, topPx = 27f)
    )
}

@Composable
fun CategorySection(
    navController: NavHostController,
    onCategoryClick: (ClubCategory) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        //상단: '카테고리' 제목 + '전체보기' 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .figmaPadding(startPx = 27f, endPx = 31f, topPx = 45f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "카테고리",
                fontSize = figmaTextSizeSp(16f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp * 1.5f, //행간
                letterSpacing = (-0.02).em, //자간
                color = Color(0xFF000000)
            )

            Text(
                text = "전체보기",
                fontSize = figmaTextSizeSp(10f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Medium,
                lineHeight = 10.sp * 1.5f, //행간
                letterSpacing = (-0.011).em, //자간
                color = Color(0xFFB1B1B1),
                modifier = Modifier
                    .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                    ) {
                        navController.navigate("clublist/전체")
                    }
            )
        }

        val categories = ClubCategory.values()

        //카테고리 버튼 배치
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), //한 행에 3개 버튼
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .figmaPadding(startPx = 27f, endPx = 27f, topPx = 20f),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(38.dp), //가로간격
            verticalArrangement = Arrangement.spacedBy(15.dp) //세로간격
        ) {
            items(categories) { category ->
                val iconRes = category.getIconRes()
                CategoryItem(
                    label = category.displayName,
                    iconResId = iconRes,
                    onClick = { onCategoryClick(category) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    label: String,
    iconResId: Int?,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentHeight()
    ) {
        if (iconResId != null) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                modifier = Modifier
                    .figmaSize(widthPx = 50f, heightPx = 50f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onClick() }
            )
        }
        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            fontSize = figmaTextSizeSp(11f),
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Medium,
            lineHeight = 11.sp * 1.5f, //행간
            letterSpacing = (-0.03).em, //자간
            color = Color(0xFF3C3C3C),
            textAlign = TextAlign.Center
        )
    }
}