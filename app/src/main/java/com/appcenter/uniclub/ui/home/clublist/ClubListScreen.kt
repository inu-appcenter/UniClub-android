package com.appcenter.uniclub.ui.home.clublist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.data.dummyClubs
import com.appcenter.uniclub.R

//동아리 리스트 페이지
@Composable
fun ClubListScreen(
    navController: NavHostController,
    categoryName: String = "전체" //카테고리명 기본값
) {
    var selectedSort by remember { mutableStateOf("기본") } //정렬 옵션 상태

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar( //상단 바
            onBackClick = { navController.navigateUp() }, //뒤로 가기 클릭 시 이전 화면으로
            title = categoryName,
            rightIconResId = R.drawable.ic_search, //검색 아이콘
            onRightIconClick = { /* TODO: 검색 기능 연결 예정 */ }
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            //동아리 카드 리스트 영역
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //더미 데이터 개수만큼 반복하여 컴포즈
                items(dummyClubs.size) { index ->
                    ClubCardList(
                        club = dummyClubs[index],
                        onClick = { navController.navigate("promotion") })
                }
            }

            //드롭다운 버튼
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp)
                    .align(Alignment.TopEnd), //우측 상단에 위치
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SortDropdown(onOptionSelected = { selectedSort = it })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClubListScreenPreview() {
    val navController = rememberNavController()
    ClubListScreen(navController = navController)
}