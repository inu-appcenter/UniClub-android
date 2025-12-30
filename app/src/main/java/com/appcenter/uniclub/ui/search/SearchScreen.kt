package com.appcenter.uniclub.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.components.ClubCard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appcenter.uniclub.App
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.network.dto.toClub
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.launch
import kotlin.collections.firstOrNull

//검색 페이지
@Composable
fun SearchScreen(navController: NavHostController) {
    val context = LocalContext.current
    val app = remember { context.applicationContext as App }
    val repo = remember(app) { ServiceLocator.searchRepository(app) }

    val vm: SearchViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SearchViewModel(repo) as T
            }
        }
    )

    //연타 방지 (취소 버튼용)
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    var query by remember { mutableStateOf("") } //검색어 상태를 저장
    val results by vm.results.collectAsState()

    val navBackStackEntry = navController.currentBackStackEntry

    LaunchedEffect(navBackStackEntry) {
        val lifecycle = navBackStackEntry?.lifecycle
        lifecycle?.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    //SearchScreen이 화면에 돌아왔을 때 실행됨
                    query = ""
                    vm.clear()
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 100.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color.White)
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            //검색 바 영역
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .figmaPadding(startPx = 20f, topPx = 25f, endPx = 15f)
            ) {
                //검색 입력 필드
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 272f, heightPx = 35f)
                        .background(
                            color = Color(0xFFD9D9D9),
                            shape = RoundedCornerShape(17.5.dp)
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .figmaPadding(startPx = 15f, endPx = 15f)
                            .fillMaxWidth()
                    ) {
                        BasicTextField(
                            value = query,
                            onValueChange = {
                                query = it
                                vm.search(it)
                            },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (query.isEmpty()) {
                                    Text(
                                        text = "동아리를 검색해보세요 :D",
                                        fontSize = figmaTextSizeSp(12f),
                                        fontFamily = NotoSansKR,
                                        lineHeight = 12.sp * 1.5f,
                                        letterSpacing = (-0.011).em,
                                        color = Color(0xFF595959)
                                    )
                                }
                                innerTextField()
                            }
                        )

                        //입력값 있을 때만 지우기 버튼 표시
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    query = ""
                                    vm.search("")
                                },
                                modifier = Modifier.size(18.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close),
                                    contentDescription = "검색어 지우기",
                                    tint = Color.Black,
                                    modifier = Modifier.figmaSize(widthPx = 12f, heightPx = 12f)
                                )
                            }
                        }
                    }
                }

                //취소 버튼
                Text(
                    text = "취소",
                    fontSize = figmaTextSizeSp(14f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 14.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black,
                    maxLines = 1,
                    modifier = Modifier
                        .figmaPadding(startPx = 15f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.launch {
                                navGuard.run {
                                    navController.popBackStack()
                                }
                            }
                        }
                )
            }

            //검색 결과 헤더 (검색어 표시)
            if (query.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .figmaPadding(startPx = 27f, topPx = 48f, endPx = 20f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = "검색 아이콘",
                        modifier = Modifier.figmaSize(widthPx = 18f, heightPx = 18f)
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = results.firstOrNull()?.name ?: "", //서버 결과에서 첫 번째 동아리 이름
                        fontSize = figmaTextSizeSp(14f),
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 14.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color.Black
                    )
                }
            }

            //검색 결과 동아리 카드
            LazyColumn(
                contentPadding = PaddingValues(vertical = 18.dp),
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally //가운데 정렬
            ) {
                items(results) { clubDto ->
                    ClubCard(
                        club = clubDto.toClub(),
                        onClick = { navController.navigate("promotion/${clubDto.id}") },
                        onToggleFavorite = { vm.onFavoriteClick(clubDto.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(70.dp)) }
            }
        }
    }
}