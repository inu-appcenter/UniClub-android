package com.appcenter.uniclub.ui.promotion

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.appcenter.uniclub.App
import com.appcenter.uniclub.R
import com.appcenter.uniclub.network.dto.DescriptionMediaDto
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import com.appcenter.uniclub.util.formatDateTime
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

private fun recruitIconOf(status: String?): Int = when (status?.uppercase()) {
    "SCHEDULED" -> R.drawable.ic_upcoming    // 모집예정
    "ACTIVE"    -> R.drawable.ic_recruiting  // 모집중
    "CLOSED"    -> R.drawable.ic_closed      // 모집마감
    else        -> R.drawable.ic_upcoming
}

private fun openUrl(context: Context, url: String) {
    if (url.isBlank()) return
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

//사용자용 홍보 페이지
@Composable
fun UserPromotionScreen(
    navController: NavHostController,
    rootNavController: NavHostController,
    onBackClick: () -> Unit,
    clubId: Long,
    app: App,
    vm: UserPromotionViewModel = viewModel(
        factory = UserPromotionViewModelFactory(app, clubId)
    )
) {
    val state by vm.uiState.collectAsState()

    //연타/잔상 클릭 방지 (화면 단위)
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val data = state.data
    if (state.error != null || data == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("불러오기 실패: ${state.error ?: "알 수 없는 오류"}")
        }
        return
    }

    val refreshState = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow("refresh", false)
        ?.collectAsState()

    LaunchedEffect(refreshState?.value) {
        if (refreshState?.value == true) {
            vm.refresh()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("refresh", false)
        }
    }

    val scrollState = rememberScrollState()
    val statusIcon = remember(data.status) { recruitIconOf(data.status) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) //스크롤
                .zIndex(0f)
        ) {
            //배너 + TopBar + 프로필 사진 겹치는 구조
            Box(modifier = Modifier.height(209.dp)) {
                //배너
                if (!data.bannerUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data.bannerUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Banner Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
                            .background(Color(0xFF585858))
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.bg_promotion),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
                    )
                }

                PromotionTopBar( // 상단바
                    isLiked = data.favorite,

                    //뒤로가기 연타 방지 + (가능하면) 현재 화면일 때만 처리
                    onBackClick = {
                        scope.launch {
                            navGuard.run back@{
                                val route = navController.currentBackStackEntry
                                    ?.destination
                                    ?.route
                                    .orEmpty()

                                if (!route.contains("promotion")) return@back
                                onBackClick()
                            }
                        }
                    },

                    //좋아요 연타 방지 (서버 중복 호출 방지)
                    onLikeClick = {
                        scope.launch {
                            navGuard.run {
                                vm.onFavoriteClick(clubId)
                            }
                        }
                    },

                    showEdit = data.canEdit,

                    //수정 화면 진입 연타 방지 + launchSingleTop
                    onEditClick = {
                        scope.launch {
                            navGuard.run edit@{
                                val route = navController.currentBackStackEntry
                                    ?.destination
                                    ?.route
                                    .orEmpty()
                                if (!route.contains("promotion")) return@edit

                                navController.navigate("admin_promotion/$clubId") {
                                    launchSingleTop = true
                                }
                            }
                        }
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(63.dp)
                        .zIndex(1f) //배너 위에 배치
                )

                //프로필 이미지
                if (!data.profileUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data.profileUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(113.dp)
                            .offset(x = 24.dp, y = 209.dp - 113.dp / 2)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFFCDCDCD))
                            .zIndex(1f)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.default_image),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .offset(x = 21.dp, y = 209.dp - 113.dp / 2)
                            .clip(RoundedCornerShape(40.dp))
                            .zIndex(1f)
                    )
                }
            }

            //SNS 버튼 (유튜브, 인스타)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp, end = 15.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val yt = data.youtubeLink
                Image(
                    painter = painterResource(R.drawable.ic_youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            enabled = !yt.isNullOrBlank(),
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            //외부 링크도 연타 방지
                            scope.launch {
                                navGuard.run {
                                    if (!yt.isNullOrBlank()) openUrl(context, yt)
                                }
                            }
                        }
                )

                Spacer(Modifier.width(6.dp))

                val ig = data.instagramLink
                Image(
                    painter = painterResource(R.drawable.ic_instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            enabled = !ig.isNullOrBlank(),
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            //외부 링크도 연타 방지
                            scope.launch {
                                navGuard.run {
                                    if (!ig.isNullOrBlank()) openUrl(context, ig)
                                }
                            }
                        }
                )
            }

            //동아리명 + 모집상태
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 143f, heightPx = 30f)
                        .clip(RoundedCornerShape(45.dp))
                        .background(Color(0xFFFF5900)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text( //동아리명
                        text = data.name,
                        color = Color.White,
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = figmaTextSizeSp(14f),
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp * 1.5f,
                        letterSpacing = (-0.011).em
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Image( //모집상태
                    painter = painterResource(id = statusIcon),
                    contentDescription = null,
                    modifier = Modifier.offset(x = (-20).dp)
                )
            }

            //동아리 정보 3개
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                fun safe(s: String?) = if (s.isNullOrBlank()) "-" else s
                InfoItem(title = "동아리방", value = safe(data.location))
                InfoItem(title = "회장", value = safe(data.presidentName))
                InfoItem(title = "연락처", value = safe(data.presidentPhone))
            }

            Spacer(modifier = Modifier.height(25.dp))

            //한줄 소개
            TextShadowBanner(text = if (data.simpleDescription.isNullOrBlank()) "-" else data.simpleDescription!!)

            Spacer(modifier = Modifier.height(35.dp))

            //모집 안내, 공지
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                val period = if (!data.startTime.isNullOrBlank() && !data.endTime.isNullOrBlank()) {
                    "${formatDateTime(data.startTime)} ~ ${formatDateTime(data.endTime)}"
                } else {
                    "-"
                }
                AnnouncementItem(label = "모집기간", content = period)
                AnnouncementItem(label = "공지", content = if (data.notice.isNullOrBlank()) "-" else data.notice!!)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(15.dp))

            ClubDescription(description = if (data.description.isNullOrBlank()) "-" else data.description!!)

            Column {
                if (data.promoItems.isNotEmpty()) {
                    ActivityImageCarouselUrls(items = data.promoItems)
                }
                BottomActionButtons(
                    rootNavController = rootNavController,
                    clubId = clubId,
                    data = data,
                    applicationFormLink = data.applicationFormLink
                )
            }

            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}

//동아리 정보 컴포넌트
@Composable
fun InfoItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = figmaTextSizeSp(11f),
            fontFamily = NotoSansKR,
            lineHeight = 11.sp * 1.5f,
            color = Color.Black
        )
    }
}

//한줄 소개 컴포넌트
@Composable
fun TextShadowBanner(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
            .graphicsLayer {
                shadowElevation = 10.dp.toPx() //높이 조절
                shape = RoundedCornerShape(0.dp)
                clip = false
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFF9F9F9), Color(0xFFFFFFFF))
                ),
                shape = RoundedCornerShape(0.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFFF5900),
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 30.dp)
        )
    }
}

//공지 컴포넌트
@Composable
fun AnnouncementItem(label: String, content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            color = Color.Black,
            modifier = Modifier.width(70.dp)
        )
        Text(
            text = content,
            fontWeight = FontWeight.Medium,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            color = Color.Black
        )
    }
}

//동아리 설명 컴포넌트
@Composable
fun ClubDescription(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 20.dp)
    ) {
        Text(
            text = description,
            fontSize = figmaTextSizeSp(13f),
            fontFamily = NotoSansKR,
            lineHeight = 13.sp * 1.75f,
            letterSpacing = (-0.03).em,
            color = Color.Black
        )
    }
}

//활동사진 캐러셀
@Composable
fun ActivityImageCarouselUrls(items: List<DescriptionMediaDto>) {
    val limited = remember(items) { items.take(10) } // 최대 10장

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp),
        contentPadding = PaddingValues(horizontal = 21.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(limited) { _, item ->
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(item.mediaLink)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .figmaSize(widthPx = 139f, heightPx = 183f)
                    .clip(RoundedCornerShape(25.dp))
            )
        }
    }
}

//하단 버튼 정렬 컴포넌트
@Composable
fun BottomActionButtons(
    rootNavController: NavHostController,
    clubId: Long,
    data: PromotionViewData,
    applicationFormLink: String?
) {
    val context = LocalContext.current

    //하단 버튼도 자체적으로 연타 방지
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
    ) {
        ImageButtonItem(
            imageRes = R.drawable.btn_question,
            contentDescription = "질문하기",
            enabled = true,
            onClick = {
                scope.launch {
                    navGuard.run {
                        val encodedName = URLEncoder.encode(
                            data.name,
                            StandardCharsets.UTF_8.toString()
                        )

                        rootNavController.navigate(
                            "questionEditFull?id=0&clubId=$clubId&clubName=$encodedName&content="
                        ) {
                            launchSingleTop = true
                        }
                    }
                }
            }
        )

        ImageButtonItem(
            imageRes = R.drawable.btn_apply,
            contentDescription = "지원하기",
            enabled = !applicationFormLink.isNullOrBlank(),
            onClick = {
                scope.launch {
                    navGuard.run {
                        applicationFormLink?.let { openUrl(context, it) }
                    }
                }
            }
        )
    }
}

@Composable
fun ImageButtonItem(
    imageRes: Int,
    contentDescription: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = contentDescription,
        modifier = Modifier.clickable(
            enabled = enabled,
            onClick = onClick,
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        )
    )
}