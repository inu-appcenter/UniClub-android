package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.Image
import com.appcenter.uniclub.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp

//Q&A 동아리 선택 페이지
@Composable
fun ClubSelectScreen(navController: NavHostController) {
    var name by remember { mutableStateOf("") } //동아리 이름 검색
    var selectedClub by remember { mutableStateOf<Pair<String, String>?>(null) } //현재 선택된 동아리

    Column(
        modifier = Modifier
            .fillMaxSize()
            .figmaPadding(topPx = 41f)
    ) {
        //상단바
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "동아리 선택",
                fontSize = figmaTextSizeSp(15f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 15.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )

            Text(
                text = "취소",
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .figmaPadding(endPx = 23f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { navController.popBackStack() } //직전 화면으로 복귀
            )
        }

        //검색바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .figmaPadding(topPx = 26f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .figmaSize(widthPx = 303f, heightPx = 35f)
                    .clip(RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image( //검색바 배경 이미지
                    painter = painterResource(R.drawable.bg_qna_search),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Row( //텍스트필드,버튼
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .figmaPadding(startPx = 16f, topPx = 8f, endPx = 13f, bottomPx = 8f)
                        .fillMaxWidth()
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f), //남은 폭 모두 사용
                        decorationBox = { innerTextField ->
                            innerTextField() //실제 입력 텍스트
                        }
                    )
                    //입력값 있을 때만 지우기 버튼 표시
                    if (name.isNotEmpty()) {
                        IconButton(
                            onClick = { name = "" },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = "검색어 지우기",
                                modifier = Modifier.figmaSize(widthPx = 12f, heightPx = 12f)
                            )
                        }
                    } else { //입력값 없을 땐 검색 아이콘
                        Image(
                            painter = painterResource(R.drawable.ic_qna_search),
                            contentDescription = null,
                            modifier = Modifier.figmaSize(widthPx = 12f, heightPx = 12f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val dummyClubs = listOf( //더미데이터
            "하늬울림" to "문화분과",
            "인인극회" to "문화분과",
            "INSDIES" to "문화분과",
            "젊은영상" to "문화분과",
            "IUDC" to "문화분과",
            "함성" to "문화분과",
            "크레퍼스" to "문화분과",
            "파이오니아" to "문화분과",
            "포크라인" to "문화분과",
            "INUO" to "문화분과",
            "울림" to "문화분과",
            "INUWFC" to "체육분과",
            "IVF" to "종교분과"
        )

        //리스트 영역
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(dummyClubs.size) { index ->
                    val (name, category) = dummyClubs[index]
                    ClubList(
                        name = name,
                        category = category,
                        selected = selectedClub?.first == name,
                        onClick = { selectedClub = name to category } //한번에 하나만 선택됨
                    )
                }
            }
        }

        //하단 선택 버튼 (동아리 선택 시에만 노출)
        if (selectedClub != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .figmaPadding(topPx = 10f ,bottomPx = 38f),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(R.drawable.btn_select),
                    contentDescription = null,
                    modifier = Modifier
                        .figmaSize(widthPx = 315f, heightPx = 48f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            //이전 화면의 savedStateHandle에 선택한 동아리 이름 저장
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selectedClub", selectedClub!!.first)

                            navController.popBackStack()
                        }
                )
            }
        }
    }
}

@Composable
fun ClubList(
    name: String,
    category: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 42f, bottomPx = 24f, endPx = 42f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
    ){
        Text( //동아리명
            text = name,
            fontSize = figmaTextSizeSp(14f),
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = if(selected) Color(0xFFFF5900) else Color.Black
        )
        Text( //분과
            text = category,
            fontSize = figmaTextSizeSp(10f),
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Medium,
            lineHeight = 10.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = if(selected) Color(0xFFFF5900) else Color.Black
        )
    }
}