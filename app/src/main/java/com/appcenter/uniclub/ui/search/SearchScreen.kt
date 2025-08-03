package com.appcenter.uniclub.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.appcenter.uniclub.data.dummyClubs
import com.appcenter.uniclub.R
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.appcenter.uniclub.ui.components.ClubCard
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

@Composable
fun SearchScreen(navController: NavHostController) {
    var query by remember { mutableStateOf("") } //검색어 상태를 저장

    //검색어에 따라 필터링된 동아리 리스트 생성
    val filteredClubs = remember(query) {
        if (query.isBlank()) emptyList()
        else dummyClubs.filter {
            it.name.contains(query, ignoreCase = true) //대소문자 무시 검색
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        //배경 이미지
        Image(
            painter = painterResource(id = R.drawable.search_bg_top),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 83.dp), //상단으로부터
            contentScale = ContentScale.FillWidth
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
                            onValueChange = { query = it },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                if (query.isEmpty()) {
                                    Text(
                                        text = "동아리를 검색해보세요 :D",
                                        fontSize = figmaTextSizeSp(12f),
                                        color = Color(0xFF595959)
                                    )
                                }
                                innerTextField() //실제 입력 텍스트
                            }
                        )

                        //입력값 있을 때만 지우기 버튼 표시
                        if (query.isNotEmpty()) {
                            IconButton(
                                onClick = { query = "" },
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

                //취소 버튼 (뒤로가기)
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                ) {
                    Text(
                        text = "취소",
                        fontSize = figmaTextSizeSp(14f),
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        maxLines = 1
                    )
                }
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

                    //검색어에 해당하는 첫 번째 동아리 이름 표시
                    val matchedClubName = dummyClubs.firstOrNull {
                        it.name.contains(query, ignoreCase = true)
                    }?.name

                    Text(
                        text = matchedClubName ?: "", //일치하는 이름만
                        fontSize = figmaTextSizeSp(14f),
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            //검색 결과 동아리 카드
            LazyColumn(
                contentPadding = PaddingValues(vertical = 18.dp),
                modifier = Modifier.fillMaxWidth(), // ← 전체 너비 확보
                horizontalAlignment = Alignment.CenterHorizontally // ← 가운데 정렬
            ) {
                items(filteredClubs) { club ->
                    ClubCard(
                        club = club,
                        onClick = { navController.navigate("promotion") })
                }
                item { Spacer(modifier = Modifier.height(70.dp)) }
            }
        }
    }
}