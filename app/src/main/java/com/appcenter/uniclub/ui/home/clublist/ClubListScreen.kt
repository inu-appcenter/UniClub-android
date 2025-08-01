package com.appcenter.uniclub.ui.home.clublist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.data.dummyClubs
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.ClubCategory

//동아리 리스트 페이지
@Composable
fun ClubListScreen(
    navController: NavHostController,
    categoryName: String = "전체" //상단 타이틀 및 필터링에 쓰일 카테고리명 (기본값: 전체)
) {
    var selectedSort by remember { mutableStateOf("기본") } //선택된 정렬 옵션 상태

    val categoryFilter = categoryName.toClubCategoryOrNull() //categoryName 문자열을 enum 형태의 ClubCategory로 변환

    val filteredClubs = remember(categoryFilter) { //선택된 카테고리에 따라 dummy 데이터 필터링
        if (categoryFilter != null) {
            dummyClubs.filter { it.category == categoryFilter }
        } else {
            dummyClubs
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { //상단 TopBar
            TopBar(
                onBackClick = { navController.navigateUp() }, //뒤로 가기 처리
                title = categoryName, //현재 선택된 카테고리명
                rightIconResId = R.drawable.ic_search, //우측 아이콘 (검색 아이콘)
                onRightIconClick = { /* TODO: 검색 기능 연결 예정 */ }
            )
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }

        //정렬 버튼 (겹쳐 보이게 zIndex + 마진 조정)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .zIndex(1f)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    SortDropdown(onOptionSelected = { selectedSort = it })
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp)) // 카드와 버튼 간 살짝 간격
        }

        //동아리 카드 리스트
        items(filteredClubs.size) { index ->
            ClubCardList(
                club = filteredClubs[index],
                onClick = { navController.navigate("promotion") } //클릭 시 홍보 화면으로 이동
            )
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
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