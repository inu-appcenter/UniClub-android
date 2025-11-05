package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp

//질문 작성, 수정 페이지
@Composable
fun QuestionEditScreen(
    navController: NavHostController,
    questionId: Long,
    initialClub: String = "", //초기 선택 동아리 (수정모드)
    initialContent: String = "" //초기 질문 내용 (수정모드)
) {
    var selectedClub by remember { mutableStateOf(initialClub) } //선택된 동아리명
    var query by remember { mutableStateOf(initialContent) } //질문 본문
    var isAnonymous by remember { mutableStateOf(false) } //익명 여부

    //ClubSelect에서 넘어온 값
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    savedStateHandle?.getLiveData<String>("selectedClub")?.observeForever { result ->
        selectedClub = result
        savedStateHandle.remove<String>("selectedClub") // 한 번 쓰고 제거
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.figmaPadding(topPx = 27f)) {
            TopBar( //상단바
                onBackClick = { navController.popBackStack() },
                title = "질문하기"
            )

            Box( //동아리 선택바
                modifier = Modifier
                    .fillMaxWidth()
                    .figmaPadding(topPx = 26f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 303f, heightPx = 35f)
                        .clip(RoundedCornerShape(13.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigate("clubSelect") //동아리 선택 화면으로 이동
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.bg_qna_search),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Row( //내부 컨텐츠
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .figmaPadding(startPx = 16f, topPx = 8f, endPx = 13f, bottomPx = 8f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "질문할 동아리를 검색하세요.",
                            fontSize = figmaTextSizeSp(12f),
                            fontFamily = NotoSansKR,
                            lineHeight = 12.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color(0xFF595959),
                            modifier = Modifier.weight(1f)
                        )
                        Image(
                            painter = painterResource(R.drawable.ic_qna_search),
                            contentDescription = null,
                            modifier = Modifier.figmaSize(widthPx = 12f, heightPx = 12f)
                        )
                    }
                }
            }

            Box(modifier = Modifier.figmaPadding(startPx = 26f, topPx = 19f, endPx = 26f, bottomPx = 19f)){
                Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
            }

            //선택된 동아리 태그 + 본문
            Column(modifier = Modifier.figmaPadding(startPx = 39f, endPx = 39f)) {
                Text( //선택된 동아리 표시
                    text = if(selectedClub.isNotEmpty()) "@"+selectedClub else "",
                    color = Color(0xFFFF5900),
                    fontWeight = FontWeight.Bold,
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    lineHeight = 11.sp * 1.8f,
                    letterSpacing = (-0.011).em
                )

                Spacer(modifier = Modifier.height(25.dp))

                BasicTextField( //질문 본문
                    value = query,
                    onValueChange = { query = it },
                    singleLine = false, //여러 줄
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = figmaTextSizeSp(11f),
                        fontFamily = NotoSansKR,
                        lineHeight = 11.sp * 1.8f,
                        letterSpacing = (-0.011).em
                    ),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) { //플레이스 홀더
                            androidx.compose.material3.Text(
                                text = "동아리 부원들에게 궁금한 것을 물어보세요.",
                                fontSize = figmaTextSizeSp(11f),
                                fontFamily = NotoSansKR,
                                lineHeight = 11.sp * 1.5f,
                                letterSpacing = (-0.011).em,
                                color = Color(0xFF7D7D7D)
                            )
                        }
                        innerTextField()
                    },
                    enabled = selectedClub.isNotEmpty() //동아리 선택 전엔 입력 비활성화
                )
            }
        }

        //하단바
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .figmaPadding(bottomPx = 20f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp, Alignment.CenterHorizontally)
            //가운데 정렬 + 두 버튼 사이 11
        ) {
            Image(
                painter = painterResource(
                    if (isAnonymous) R.drawable.btn_anonymous else R.drawable.btn_anonymous_disabled
                ),
                contentDescription = "익명버튼",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { isAnonymous = !isAnonymous } // 반복 클릭 시 토글
            )
            Image(
                painter = painterResource(R.drawable.btn_qna_submit),
                contentDescription = "질문 등록 버튼",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
            )
        }
    }
}