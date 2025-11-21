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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcenter.uniclub.App
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.ui.components.Dialog
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

//메인 Q&A 페이지
@Composable
fun QnAScreen(navController: NavHostController) {
    val app = LocalContext.current.applicationContext as App
    val vm: QnaListViewModel = viewModel (
        factory = QnaListViewModelFactory(ServiceLocator.qnaRepository(app))
    )

    var query by remember { mutableStateOf("") } //검색어
    var answered by remember { mutableStateOf(false) } //답변 완료만
    var my by remember { mutableStateOf(false) } //내 질문만

    var selectedQuestionId by remember { mutableStateOf<Long?>(null) } //버튼 누른 질문 아이디

    //하단 메뉴 상태: true = 내 질문, false = 남 질문, null = 닫힘
    var activeMenuForMine by remember { mutableStateOf<Boolean?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) } //삭제,신고 다이얼로그
    var showReportDialog by remember { mutableStateOf(false) }

    //선택된 동아리
    var selectedClubName by remember { mutableStateOf("") }
    var selectedClubId by remember { mutableStateOf<Long?>(null) }

    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Long>("selectedClubId")
        ?.observeForever {
            selectedClubId = it
            navController.currentBackStackEntry?.savedStateHandle?.remove<Long>("selectedClubId")
        }
    navController.currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selectedClubName")
        ?.observeForever {
            selectedClubName = it
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedClubName")
        }

    LaunchedEffect(query, selectedClubId, answered, my) {
        vm.keyword = query.ifBlank { null }
        vm.clubId = selectedClubId
        vm.answered = answered
        vm.onlyMy = my
        vm.refresh()
    }

    val ui by vm.ui.collectAsState()

    Scaffold( //상단바 있는 기본 스캐폴드
        containerColor = Color.Transparent,
        topBar = { //상단바
            Column() {
                Spacer(modifier = Modifier.height(24.dp))
                TopBar(
                    onBackClick = { navController.popBackStack() },
                    title = "질의응답"
                )
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                item {
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
                }

                item {
                    Spacer(modifier = Modifier.height(33.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .figmaPadding(startPx = 30f, endPx = 30f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        //동아리 선택 버튼
                        ClubSelectButton(navController, selectedValue = selectedClubName)

                        Row { //필터 토글
                            Box( //답변 완료만
                                modifier = Modifier
                                    .figmaSize(widthPx = 68f, heightPx = 17f)
                                    .offset(y = 5.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { answered = !answered }
                            ) {
                                Image(
                                    painter = painterResource(if (answered) R.drawable.btn_only_answered else R.drawable.btn_only_answered_disabled),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(3.dp))
                            Box( //내 질문만
                                modifier = Modifier
                                    .figmaSize(widthPx = 59f, heightPx = 17f)
                                    .offset(y = 5.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { my = !my }
                            ) {
                                Image(
                                    painter = painterResource(if (my) R.drawable.btn_my else R.drawable.btn_my_disabled),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(13.dp))
                }

                //질문 리스트
                items(ui.items, key = { it.questionId }) { q ->
                    val item = QnaListItem(
                        id = q.questionId,
                        authorName = q.nickname,
                        authorProfileUrl = q.profile,
                        clubName = q.clubName,
                        createdAt = q.updatedAt,
                        question = q.content,
                        hasAnswer = q.countAnswer > 0,
                        answerCount = q.countAnswer.toInt(),
                        isMine = q.owner
                    )
                    val onMenuClick: (Boolean, Long) -> Unit = { isMine, id ->
                        activeMenuForMine = isMine
                        selectedQuestionId = id
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .figmaPadding(startPx = 26f, endPx = 26f, bottomPx = 13f)
                    ){
                        if (item.hasAnswer && item.answerCount > 0) {
                            QnaCardAnswered(item, onMenuClick, navController)
                        } else {
                            QnaCardNoAnswer(item, onMenuClick, navController)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }

            Image(
                painter = painterResource(id = R.drawable.btn_qna),
                contentDescription = "질문하기",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .figmaSize(widthPx = 307f, heightPx = 48f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // TODO: 질문하기 액션
                        navController.navigate("questionEdit")
                    }
                    .zIndex(1f),
                contentScale = ContentScale.Crop
            )
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
                        modifier = Modifier.padding(bottom = 10.dp)
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
                                            val selected = ui.items.firstOrNull { it.questionId == selectedQuestionId }
                                            selected?.let { q ->
                                                val encodedClub = URLEncoder.encode(q.clubName, StandardCharsets.UTF_8.toString())
                                                val encodedContent = URLEncoder.encode(q.content, StandardCharsets.UTF_8.toString())

                                                navController.navigate(
                                                    "questionEditFull?id=${q.questionId}&clubId=${q.clubId}&clubName=$encodedClub&content=$encodedContent"
                                                )
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
                    selectedQuestionId?.let { id ->
                        vm.deleteQuestion(id) {
                            activeMenuForMine = null
                        }
                    }
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