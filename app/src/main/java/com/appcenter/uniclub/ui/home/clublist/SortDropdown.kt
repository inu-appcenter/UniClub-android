package com.appcenter.uniclub.ui.home.clublist

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.util.figmaSize

//동아리 리스트 페이지에서 정렬 옵션을 선택할 수 있는 드롭다운 메뉴
@Composable
fun SortDropdown(
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(80.dp) //버튼과 드롭다운 모두 동일한 고정 폭
            .wrapContentHeight()
    ) {
        Column(horizontalAlignment = Alignment.End) {
            //드롭다운 버튼
            Image(
                painter = painterResource(id = R.drawable.ic_sort), // 드롭다운 트리거 이미지
                contentDescription = "정렬 아이콘",
                modifier = Modifier
                    .figmaSize(widthPx = 75f, heightPx = 30f)
                    .clickable { expanded = true }
            )

            //드롭다운 메뉴가 펼쳐졌을 경우만 표시
            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 75f, heightPx = 88f)
                        .align(Alignment.End)
                ) {
                    //드롭다운 배경 이미지
                    Image(
                        painter = painterResource(id = R.drawable.sort_dropdown), // 업로드하신 이미지
                        contentDescription = "정렬 옵션 메뉴",
                        modifier = Modifier.fillMaxSize()
                    )

                    // 투명 클릭 옵션 영역
                    Column(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(vertical = 7.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //"즐겨찾기", "모집중", "기본" 각 항목에 대해 투명한 클릭 영역 구성
                        listOf("즐겨찾기", "모집중", "기본").forEach { option ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .clickable {
                                        // TODO: 선택한 옵션에 따라 리스트 정렬 로직 실행 예정
                                        onOptionSelected(option)
                                        expanded = false
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}
