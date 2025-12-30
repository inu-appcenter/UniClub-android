package com.appcenter.uniclub.ui.home.clublist

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appcenter.uniclub.App
import com.appcenter.uniclub.R
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.model.ClubCategory
import com.appcenter.uniclub.network.dto.toClub
import com.appcenter.uniclub.ui.components.ClubCard
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaSize
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class SortOption(val label: String, val serverValue: String) {
    NAME("기본", "name"),
    LIKE("즐겨찾기", "like"),
    STATUS("모집중", "status")
}

// 동아리 리스트 페이지
@Composable
fun ClubListScreen(
    navController: NavHostController,
    categoryName: String = "전체"
) {
    val app = LocalContext.current.applicationContext as App

    val vm: ClubListViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ClubListViewModel(ServiceLocator.clubRepository(app)) as T
            }
        }
    )
    val state by vm.uiState.collectAsState()

    val category = ClubCategory.fromDisplayName(categoryName)
        ?: ClubCategory.fromServerValue(categoryName)

    var selectedSort by remember { mutableStateOf(SortOption.NAME) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    //네비게이션 연타/레이스 방지용
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    //화면 진입 시 서버에서 데이터 불러오기
    LaunchedEffect(categoryName, selectedSort) {
        val serverCategory = category?.primaryServerValue
        vm.reset()
        vm.loadClubs(category = serverCategory, sortBy = selectedSort.serverValue, reset = true)
    }

    val listState = rememberLazyListState()

    //끝까지 스크롤 시 다음 페이지 로드
    LaunchedEffect(listState, state.clubs, state.hasNext) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collectLatest { lastVisible ->
                val lastIndex = state.clubs.lastIndex
                if (lastVisible != null &&
                    lastIndex >= 0 &&
                    lastVisible >= lastIndex - 1 &&
                    state.hasNext &&
                    !state.loading
                ) {
                    vm.loadNextPage()
                }
            }
    }

    //Box로 감싸서 드롭다운 메뉴가 LazyColumn 위에 겹쳐 보이게 처리
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { //상단바
                TopBar(
                    onBackClick = {
                        scope.launch {
                            navGuard.run navGuardRun@{
                                //현재 목적지가 진짜 clublist일 때만 pop (전환 직후 늦게 들어온 클릭 무시)
                                val route = navController.currentBackStackEntry?.destination?.route.orEmpty()
                                val isClubListRoute =
                                    route == "clublist/{categoryName}" ||
                                            route.startsWith("clublist/") ||
                                            route.startsWith("clublist")

                                if (!isClubListRoute) return@navGuardRun

                                navController.popBackStack()
                            }
                        }
                    },
                    title = when {
                        categoryName == "전체" -> "전체"
                        category != null -> category.displayName
                        else -> categoryName
                    },
                    rightIconResId = R.drawable.ic_search,
                    onRightIconClick = { navController.navigate("search") }
                )
            }

            item { Spacer(modifier = Modifier.height(40.dp)) }

            item { //정렬 버튼
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 21.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(top = 10.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_sort),
                            contentDescription = "정렬 아이콘",
                            modifier = Modifier
                                .figmaSize(widthPx = 75f, heightPx = 30f)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { dropdownExpanded = !dropdownExpanded }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            items(state.clubs) { clubDto ->
                ClubCard(
                    club = clubDto.toClub(),
                    onClick = { navController.navigate("promotion/${clubDto.id}") },
                    onToggleFavorite = { vm.onFavoriteClick(clubDto.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }

        //드롭다운 메뉴
        if (dropdownExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 135.dp, end = 19.dp)
                    .zIndex(10f), //LazyColumn 위에 오도록 설정
                contentAlignment = Alignment.TopEnd
            ) {
                Box(
                    modifier = Modifier.figmaSize(widthPx = 82f, heightPx = 95f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sort_dropdown),
                        contentDescription = "정렬 옵션 메뉴",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )
                    //클릭 가능한 정렬 옵션 영역
                    Column(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(vertical = 6.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        listOf(SortOption.LIKE, SortOption.STATUS, SortOption.NAME).forEach { option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        selectedSort = option
                                        dropdownExpanded = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}