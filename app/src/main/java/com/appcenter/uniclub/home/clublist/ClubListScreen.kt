package com.appcenter.uniclub.home.clublist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.components.TopBar

//동아리 리스트 페이지
@Composable
fun ClubListScreen(
    navController: NavHostController,
    categoryName: String = "전체" //카테고리명 기본값
) {
    //정렬 옵션 기본값
    var selectedSort by remember { mutableStateOf("정렬순") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar(
            onBackClick = { navController.navigateUp() }, //뒤로 가기 클릭 시 이전 화면으로
            onRightIconClick = { /* 검색 로직 */ }
        )

        Spacer(modifier = Modifier.height(15.dp))

        //카테고리 제목, 정렬 드롭다운 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //전달받은 카테고리명 표시
            Text(
                text = categoryName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            //정렬 옵션 드롭다운
            SortDropdown(
                selectedOption = selectedSort, //현재 선택된 옵션
                onOptionSelected = { selectedSort = it } //옵션 선택 시 상태 업데이트
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        //동아리 카드 리스트 영역
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //더미 데이터 개수만큼 반복하여 컴포즈
            items(dummyClubs.size) { index ->
                ClubCardList(club = dummyClubs[index])
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