package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import com.appcenter.uniclub.ui.components.Dialog
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

//메인 Q&A 페이지
@Composable
fun QnAScreen(navController: NavHostController) {
    var query by remember { mutableStateOf("") } //검색어
    var answered by remember { mutableStateOf(false) } //답변 완료만
    var my by remember { mutableStateOf(false) } //내 질문만

    var selectedQuestionId by remember { mutableStateOf<Long?>(null) } //버튼 누른 동아리 아이디

    //하단 메뉴 상태: true = 내 질문, false = 남 질문, null = 닫힘
    var activeMenuForMine by remember { mutableStateOf<Boolean?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) } //삭제,신고 다이얼로그
    var showReportDialog by remember { mutableStateOf(false) }

    //선택된 동아리
    var selectedClub by remember { mutableStateOf("") }
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    savedStateHandle?.getLiveData<String>("selectedClub")?.observeForever { result ->
        selectedClub = result
        savedStateHandle.remove<String>("selectedClub") // 한 번만 쓰고 지우기
    }

    val sampleItems = remember { //더미 리스트
        listOf(
            QnaListItem(
                id = 1, authorName = "김가나", authorProfileUrl = null,
                clubName = "Appcenter", createdAt = "07.07 13:13",
                question = "동아리 질문 작성 예시입니다. 긴 질문을 작성할 경우 이렇게 보이면서 ...",
                hasAnswer = true, answerCount = 4, isMine = false
            ),
            QnaListItem(
                id = 2, authorName = "영훈", authorProfileUrl = null,
                clubName = "동아리 1", createdAt = "07.07 13:13",
                question = "동아리원 모집 언제하나요?",
                hasAnswer = false, answerCount = 0, isMine = true
            ),
            QnaListItem(
                id = 3, authorName = "Chan", authorProfileUrl = null,
                clubName = "동아리 2", createdAt = "07.07 13:13",
                question = "면접은 어떻게 진행되나요?",
                hasAnswer = true, answerCount = 1, isMine = false
            )
        )
    }

    Scaffold( //상단바, 하단바 있는 기본 스캐폴드
        containerColor = Color.Transparent,
        topBar = { //상단바
            Column(modifier = Modifier.figmaPadding(topPx = 27f)) {
                TopBar(
                    onBackClick = { navController.popBackStack() },
                    title = "질의응답"
                )
            }
        },
        bottomBar = { //하단바
            if (activeMenuForMine == null) { //오버레이 닫혀 있을 땐 질문하기 버튼
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Transparent)
                        .padding(vertical = 12.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .imePadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.btn_qna),
                        contentDescription = "질문하기",
                        modifier = Modifier
                            .figmaSize(widthPx = 307f, heightPx = 48f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // TODO: 질문하기 액션
                                navController.navigate("questionEdit")
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    ) { innerPadding ->
        Box( //스캐폴드 인셋 반영 + 전체 레이아웃
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(26.dp))

                Row( //검색바 영역
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .figmaPadding(startPx = 29f, endPx = 29f)
                ) {
                    Box(
                        modifier = Modifier
                            .figmaSize(widthPx = 303f, heightPx = 35f)
                            .clip(RoundedCornerShape(13.dp))
                    ){
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
                        ){
                            Image(
                                painter = painterResource(R.drawable.ic_qna_search),
                                contentDescription = null,
                                modifier = Modifier.figmaSize(widthPx = 12f, heightPx = 12f)
                            )
                            Spacer(modifier = Modifier.width(9.dp))
                            BasicTextField( //검색 입력 텍스트필드
                                value = query,
                                onValueChange = { query = it },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                textStyle = TextStyle(
                                    color = Color.Black,
                                    fontSize = figmaTextSizeSp(12f),
                                    fontFamily = NotoSansKR,
                                    lineHeight = 12.sp * 1.5f,
                                    letterSpacing = (-0.011).em
                                ),
                                decorationBox = { innerTextField ->
                                    if (query.isEmpty()) { //플레이스홀더 렌더
                                        Text(
                                            text = "질문을 검색해보세요.",
                                            fontSize = figmaTextSizeSp(12f),
                                            fontFamily = NotoSansKR,
                                            lineHeight = 12.sp * 1.5f,
                                            letterSpacing = (-0.011).em,
                                            color = Color(0xFF595959)
                                        )
                                    }
                                    innerTextField() //실제 입력 텍스트 표시
                                }
                            )

                            //입력값 있을 때만 우측 지우기 버튼 표시
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
                }

                Spacer(modifier = Modifier.height(26.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .figmaPadding(startPx = 30f, endPx = 30f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    //동아리 선택 버튼
                    ClubSelectButton(navController, selectedValue = selectedClub)

                    Row { //필터 토글
                        Box( //답변 완료만
                            modifier = Modifier
                                .figmaSize(widthPx = 68f, heightPx = 17f)
                                .offset(y=5.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { answered = !answered }
                        ){
                            if(answered){
                                Image(
                                    painter = painterResource(id = R.drawable.btn_only_answered),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.btn_only_answered_disabled),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(3.dp))
                        Box( //내 질문만
                            modifier = Modifier
                                .figmaSize(widthPx = 59f, heightPx = 17f)
                                .offset(y=5.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { my = !my }
                        ){
                            if(my){
                                Image(
                                    painter = painterResource(id = R.drawable.btn_my),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.btn_my_disabled),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                //리스트 섹션
                QnaListSection(
                    allItems = sampleItems,
                    query = query,
                    selectedClub = selectedClub,
                    answeredOnly = answered,
                    myOnly = my,
                    onMenuClick = { isMine, id ->
                        activeMenuForMine = isMine
                        selectedQuestionId = id
                    },
                    navController = navController
                )
            }
        }
        //오버레이 하단 버튼
        if (activeMenuForMine != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { activeMenuForMine = null },
                contentAlignment = Alignment.BottomCenter
            ) {
                if (activeMenuForMine == true) {
                    //내 질문 → 수정/삭제
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .figmaSize(widthPx = 344f, heightPx = 116f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.btn_edit_delete),
                                contentDescription = "수정&삭제",
                                modifier = Modifier.matchParentSize(),
                                contentScale = ContentScale.Crop
                            )

                            //터치 영역을 위/아래 절반으로 나눔
                            Column(modifier = Modifier.matchParentSize()) {
                                Box( //수정 영역
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            // TODO: 수정 동작
                                            activeMenuForMine = null
                                            val selectedItem = sampleItems.find { it.id == selectedQuestionId }
                                            selectedItem?.let { item ->
                                                val encodedClub = URLEncoder.encode(item.clubName, StandardCharsets.UTF_8.toString())
                                                val encodedContent = URLEncoder.encode(item.question, StandardCharsets.UTF_8.toString())

                                                navController.navigate("questionEdit?id=${item.id}&clubName=$encodedClub&content=$encodedContent")
                                            }
                                        }
                                )
                                Box( //삭제 영역
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            activeMenuForMine = null
                                            showDeleteDialog = true
                                        }
                                )
                            }
                        }
                    }
                } else {
                    //남 질문 → 신고하기
                    Image(
                        painter = painterResource(id = R.drawable.btn_report),
                        contentDescription = "신고하기",
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                            .figmaSize(widthPx = 344f, heightPx = 67f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                activeMenuForMine = null
                                showReportDialog = true
                            },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        if (showDeleteDialog) { //삭제 다이얼로그
            Dialog(
                title = "이 질문을 삭제하시겠습니까?",
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    // TODO: 삭제 동작
                }
            )
        }
        if (showReportDialog) { //신고 다이얼로그
            Dialog(
                title = "이 질문을 신고하시겠습니까?",
                onDismiss = { showReportDialog = false },
                onConfirm = {
                    showReportDialog = false
                    // TODO: 신고 동작
                }
            )
        }
    }
}

//동아리 선택 버튼
@Composable
fun ClubSelectButton(
    navController: NavHostController,
    selectedValue: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .figmaSize(widthPx = 105f, heightPx = 28f)
            .background(Color(0xFFFF5900), RoundedCornerShape(13.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                navController.navigate("clubSelect")
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .figmaPadding(startPx = 12f, endPx = 12f)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_club_search),
                contentDescription = null,
                modifier = Modifier.figmaSize(widthPx = 9f, heightPx = 9f)
            )
            Spacer(modifier = modifier.width(8.dp))
            Text(
                text = if (selectedValue.isEmpty()) "동아리 선택" else selectedValue,
                fontSize = figmaTextSizeSp(11f),
                fontFamily = NotoSansKR,
                lineHeight = 11.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.White,
                modifier = Modifier.weight(1f),
                maxLines = 1, //한 줄만
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QnaListSection(
    allItems: List<QnaListItem>,
    query: String,
    selectedClub: String,
    answeredOnly: Boolean,
    myOnly: Boolean,
    onMenuClick: (Boolean, Long) -> Unit,
    navController: NavHostController
) {
    //리스트 필터링
    val filtered = remember(allItems, query, selectedClub, answeredOnly, myOnly) {
        allItems.asSequence()
            .filter { if (query.isBlank()) true else it.question.contains(query, ignoreCase = true) }
            .filter { if (selectedClub.isBlank()) true else it.clubName.equals(selectedClub, ignoreCase = true) }
            .filter { if (answeredOnly) it.hasAnswer else true }
            .filter { if (myOnly) it.isMine else true }
            .toList()
    }

    LazyColumn{
        items(filtered, key = { it.id }) { item ->
            //댓글 유무에 따라 다른 배경 사용
            if (item.hasAnswer && item.answerCount > 0) {
                QnaCardAnswered(item, onMenuClick, navController)
            } else {
                QnaCardNoAnswer(item, onMenuClick, navController)
            }
        }
    }
}