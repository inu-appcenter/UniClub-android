package com.appcenter.uniclub.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.ui.home.components.EventImageCarousel
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.home.components.ClubCardCarousel
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.ui.home.components.MainTopBar
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun HomeScreen(modifier: Modifier = Modifier,
               navController: NavHostController) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { MainTopBar() }

        item {
            //예시 이미지 넣어둠 나중에 서버 연결 필요
            val sampleEvents = listOf(
                R.drawable.event_sample,
                R.drawable.event_sample_2,
                R.drawable.event_sample_3
            )
            EventImageCarousel(eventList = sampleEvents)
        }

        item {
            RecommendTitle()
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            //예시 이미지 넣어둠 나중에 서버 연결 필요
            val sampleClubs = listOf(
                R.drawable.club1 to "IUDC",
                R.drawable.club2 to "하양검정",
                R.drawable.club3 to "기우회",
                R.drawable.club1 to "appcenter",
                R.drawable.club2 to "봉사동아리",
                R.drawable.club3 to "크레퍼스(CREPERS)",
                R.drawable.club1 to "멋쟁이사자처럼"
            )
            ClubCardCarousel(fullList = sampleClubs)
        }

        item {
            CategorySection(
                navController = navController,
                onCategoryClick = { category ->
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
        fontSize = figmaTextSizeSp(16f),
        fontWeight = FontWeight.Bold,
        color = Color(0xFF000000),
        lineHeight = 24.sp,
        modifier = Modifier
            .figmaPadding(startPx = 26f, topPx = 27f)
    )
}

@Composable
fun CategorySection(
    navController: NavHostController,
    onCategoryClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        //상단: '카테고리' 제목 + '전체보기' 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .figmaPadding(startPx = 27f, endPx = 31f, topPx = 25f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "카테고리",
                fontSize = figmaTextSizeSp(16f),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF000000),
                lineHeight = 24.sp
            )

            Text(
                text = "전체보기",
                fontSize = figmaTextSizeSp(10f),
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB1B1B1),
                modifier = Modifier.clickable {
                    navController.navigate("clublist/전체")
                }
            )
        }

        val categories = listOf(
            "교양학술" to R.drawable.ic_category_academic,
            "취미전시" to R.drawable.ic_category_hobby,
            "체육" to R.drawable.ic_category_sports, //수정 필요
            "종교" to R.drawable.ic_category_religion, //수정 필요
            "봉사" to R.drawable.ic_category_volunteer,
            "문화" to R.drawable.ic_category_culture
        )

        //카테고리 버튼 배치
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), //한 행에 3개 버튼
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .figmaPadding(startPx = 27f, endPx = 27f, topPx = 20f),
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(38.dp), //가로간격
            verticalArrangement = Arrangement.spacedBy(12.dp) //세로간격
        ) {
            items(categories) { (label, icon) ->
                CategoryItem(
                    label = label,
                    iconResId = icon,
                    onClick = { selectedLabel -> onCategoryClick(selectedLabel) }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    label: String,
    iconResId: Int,
    onClick: (String) -> Unit) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.wrapContentHeight()
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier
                .figmaSize(widthPx = 80f, heightPx = 60.4f)
                .clickable { onClick(label) }
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = label,
            fontSize = figmaTextSizeSp(11f),
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3C3C3C),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}
