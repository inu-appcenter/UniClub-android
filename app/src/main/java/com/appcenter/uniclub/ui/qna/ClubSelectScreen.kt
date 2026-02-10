package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.Image
import com.appcenter.uniclub.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.App
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.model.toCategoryDisplayName
import com.appcenter.uniclub.network.dto.QnaClubResponseDto
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.launch

//Q&A 동아리 선택 페이지
@Composable
fun ClubSelectScreen(navController: NavHostController) {
    val app = LocalContext.current.applicationContext as App
    val repo = remember { ServiceLocator.qnaRepository(app) }
    val coroutine = rememberCoroutineScope()

    //연타 방지 가드
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 300L) }

    var name by remember { mutableStateOf("") } //동아리 이름 검색
    var selectedClub by remember { mutableStateOf<QnaClubResponseDto?>(null) } //현재 선택된 동아리
    var results by remember { mutableStateOf<List<QnaClubResponseDto>>(emptyList()) }

    suspend fun search(keyword: String) {
        val key = keyword.ifBlank { null }
        repo.searchClubsForQna(keyword = key)
            .onSuccess { results = it }
            .onFailure { results = emptyList() }
    }

    LaunchedEffect(Unit) { search("") }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp) //선택 버튼 공간 확보
            ) {
                //Spacer(modifier = Modifier.height(40.dp))

                //상단바
                Box(modifier = Modifier.fillMaxWidth().figmaPadding(startPx = 23f, endPx = 23f, topPx = 15f)) {
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
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                scope.launch {
                                    navGuard.run { navController.popBackStack() }
                                }
                            }
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
                                onValueChange = {
                                    name = it
                                    coroutine.launch { search(name) }
                                },
                                singleLine = true,
                                modifier = Modifier.weight(1f), //남은 폭 모두 사용
                                decorationBox = { innerTextField ->
                                    innerTextField() //실제 입력 텍스트
                                }
                            )
                            //입력값 있을 때만 지우기 버튼 표시
                            if (name.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        name = ""
                                        coroutine.launch { search("") }
                                    },
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

                //결과 리스트
                LazyColumn(Modifier.weight(1f)) {
                    items(results.size) { idx ->
                        val c = results[idx]
                        ClubList(
                            name = c.clubName,
                            category = c.categoryType.toCategoryDisplayName(),
                            selected = selectedClub?.clubId == c.clubId,
                            onClick = { selectedClub = c }
                        )
                    }
                }
            }

            //하단 선택 버튼 (동아리 선택 시에만 노출)
            if (selectedClub != null) {
                Image(
                    painter = painterResource(R.drawable.btn_select),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(bottom = 20.dp)
                        .figmaSize(widthPx = 315f, heightPx = 48f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            //이전 화면의 savedStateHandle에 선택한 동아리 이름 저장
                            navController.previousBackStackEntry?.savedStateHandle?.apply {
                                set("selectedClubId", selectedClub!!.clubId)
                                set("selectedClubName", selectedClub!!.clubName)
                            }
                            navController.popBackStack()
                        },
                    contentScale = ContentScale.Crop
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
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 42f, bottomPx = 24f, endPx = 42f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
    ) {
        Text( //동아리명
            text = name,
            fontSize = figmaTextSizeSp(14f),
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Medium,
            lineHeight = 14.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = if (selected) Color(0xFFFF5900) else Color.Black
        )
        Text( //분과
            text = category,
            fontSize = figmaTextSizeSp(10f),
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Medium,
            lineHeight = 10.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = if (selected) Color(0xFFFF5900) else Color.Black
        )
    }
}