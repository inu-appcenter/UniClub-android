package com.appcenter.uniclub.ui.home.clublist

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.data.dummyClubs
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.ClubCategory
import com.appcenter.uniclub.ui.components.ClubCard
import com.appcenter.uniclub.ui.util.figmaSize

//동아리 리스트 페이지
@Composable
fun ClubListScreen(
    navController: NavHostController,
    categoryName: String = "전체"
) {
    var selectedSort by remember { mutableStateOf("기본") } //선택된 정렬 옵션 저장
    var dropdownExpanded by remember { mutableStateOf(false) } //드롭다운 메뉴 확장 여부 상태

    val categoryFilter = categoryName.toClubCategoryOrNull() //카테고리명 문자열을 enum으로 변환 (null이면 전체)
    val filteredClubs = remember(categoryFilter) { //카테고리 필터링된 동아리 리스트 (dummy data 기반)
        if (categoryFilter != null) dummyClubs.filter { it.category == categoryFilter }
        else dummyClubs
    }

    //Box로 감싸서 드롭다운 메뉴가 LazyColumn 위에 겹쳐 보이게 처리
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { //상단바
                TopBar(
                    onBackClick = { navController.navigateUp() },
                    title = categoryName,
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
                                .clickable { dropdownExpanded = !dropdownExpanded }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }

            //동아리 카드 리스트 출력
            items(filteredClubs.size) { index ->
                ClubCard(
                    club = filteredClubs[index],
                    onClick = { navController.navigate("promotion") }
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
                    modifier = Modifier
                        .figmaSize(widthPx = 82f, heightPx = 95f)
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
                        listOf("즐겨찾기", "모집중", "기본").forEach { option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clickable {
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


fun String.toClubCategoryOrNull(): ClubCategory? = when (this) {
    "교양학술" -> ClubCategory.ACADEMIC
    "취미전시" -> ClubCategory.HOBBY
    "체육" -> ClubCategory.SPORTS
    "종교" -> ClubCategory.RELIGION
    "봉사" -> ClubCategory.VOLUNTEER
    "문화" -> ClubCategory.CULTURE
    else -> null
}

@Preview(showBackground = true)
@Composable
fun ClubListScreenPreview() {
    val navController = rememberNavController()
    ClubListScreen(navController = navController)
}