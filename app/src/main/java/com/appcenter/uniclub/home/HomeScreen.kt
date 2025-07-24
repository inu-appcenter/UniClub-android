package com.appcenter.uniclub.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.home.components.EventImageCarousel
import com.appcenter.uniclub.R
import com.appcenter.uniclub.home.components.ClubCardCarousel
import com.appcenter.uniclub.home.components.TopAppBarSection
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.platform.LocalConfiguration
import androidx.navigation.NavController

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               navController: NavController) {
    LazyColumn(modifier = modifier
                .fillMaxSize()
    ) {
        item { TopAppBarSection() }

        item {
            Spacer(modifier = Modifier.height(13.dp))
            //예시 이미지 넣어둠 나중에 서버 연결 필요
            val sampleEvents = listOf(
                R.drawable.event_sample,
                R.drawable.event_sample_2,
                R.drawable.event_sample_3
            )
            EventImageCarousel(eventList = sampleEvents)
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            RecommendTitle()
        }

        item {
            Spacer(modifier = Modifier.height(13.dp))
            //예시 이미지 넣어둠 나중에 서버 연결 필요
            val sampleClubs = listOf(
                R.drawable.club1 to "IUDC",
                R.drawable.club2 to "하양검정",
                R.drawable.club3 to "기우회",
                R.drawable.club1 to "appcenter",
                R.drawable.club2 to "봉사동아리",
                R.drawable.club3 to "크레퍼스(CREPERS)"
            )
            ClubCardCarousel(clubList = sampleClubs)
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
            CategorySection(onCategoryClick = { category ->
                // 🔧 카테고리 클릭 시 ClubList로 이동
                navController.navigate("clublist/${category}")
            })
        }
    }
}

@Composable
fun RecommendTitle() {
    Text(
        text = "이런 동아리는 어떠세요?",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0167FF),
        lineHeight = 24.sp,
        modifier = Modifier
            .padding(start = 20.dp)
    )
}

@Composable
fun CategorySection(onCategoryClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp) //좌우, 상하 여백
    ) {
        //상단: '카테고리' 제목 + '전체보기' 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "카테고리",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0167FF),
                lineHeight = 24.sp
            )

            Text(
                text = "전체보기",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB1B1B1),
                modifier = Modifier.clickable {
                    // TODO: 클릭 시 동아리 리스트로 이동 처리 예정
                }
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

        //서버 연동 후 변경
        val categories = listOf(
            "교양학술", "취미전시", "체육", "종교",
            "봉사", "문화", "+", "+"
        )

        //카테고리 버튼 배치
        LazyVerticalGrid(
            columns = GridCells.Fixed(4), //한 행에 4개 버튼
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(15.dp), //가로간격
            verticalArrangement = Arrangement.spacedBy(12.dp) //세로간격
        ) {
            items(categories) { label ->
                CategoryItem(label = label, onClick = { selectedLabel ->
                    onCategoryClick(selectedLabel)
                })
            }
        }
    }
}

@Composable
fun CategoryItem(label: String, onClick: (String) -> Unit) {
    //피그마 비율 반영
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val buttonWidth = screenWidthDp * 68.39f / 360f
    val buttonHeight = screenWidthDp * 48.41f / 360f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(buttonWidth.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = buttonWidth.dp, height = buttonHeight.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFF9D00)) // 주황색
                .clickable { onClick(label) },
            contentAlignment = Alignment.Center
        ) {
            //추후 아이콘으로 변경
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFFF9D00),
            textAlign = TextAlign.Center
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
*/