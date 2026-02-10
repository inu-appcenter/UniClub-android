package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appcenter.uniclub.App
import com.appcenter.uniclub.R
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.launch

//질문 작성, 수정 페이지
@Composable
fun QuestionEditScreen(
    navController: NavHostController,
    questionId: Long = 0L,
    initialClubId: Long? = null, //초기 선택 동아리 (수정모드)
    initialClubName: String = "",
    initialContent: String = "" //초기 질문 내용 (수정모드)
) {
    val app = LocalContext.current.applicationContext as App
    val vm: QuestionEditViewModel = viewModel(
        factory = QuestionEditViewModelFactory(ServiceLocator.qnaRepository(app))
    )

    //연타 방지 (네비게이션/제출용)
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    var selectedClubId by remember { mutableStateOf(initialClubId) } //선택된 동아리아이디
    var selectedClubName by remember { mutableStateOf(initialClubName) } //선택된 동아리명
    var body by remember { mutableStateOf(initialContent) } //질문 본문
    var isAnonymous by remember { mutableStateOf(false) } //익명 여부

    //ClubSelect에서 넘어온 값 (id+name 저장)
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

    val ui by vm.ui.collectAsState()

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column {
                TopBar( //상단바
                    //뒤로가기 연타 방지
                    onBackClick = {
                        scope.launch {
                            navGuard.run { navController.popBackStack() }
                        }
                    },
                    title = if (questionId == 0L) "질문하기" else "질문 수정"
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
                            .let { base ->
                                if (questionId == 0L) {
                                    //신규 작성 - 클릭 가능
                                    base.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { //clubSelect 이동 연타 방지
                                        scope.launch {
                                            navGuard.run { navController.navigate("clubSelect") }
                                        }
                                    }
                                } else base //수정 모드 - 클릭 불가능
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
                                text = if (questionId == 0L)
                                    "질문할 동아리를 검색하세요."
                                else
                                    "동아리 변경이 불가능합니다",
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

                Box(
                    modifier = Modifier.figmaPadding(startPx = 26f, topPx = 19f, endPx = 26f, bottomPx = 19f)
                ) {
                    Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
                }

                //선택된 동아리 태그 + 본문
                Column(modifier = Modifier.figmaPadding(startPx = 39f, endPx = 39f)) {
                    if (selectedClubName.isNotBlank()) {
                        Text( //선택된 동아리 표시
                            text = "@$selectedClubName",
                            color = Color(0xFFFF5900),
                            fontWeight = FontWeight.Bold,
                            fontSize = figmaTextSizeSp(11f),
                            fontFamily = NotoSansKR,
                            lineHeight = 11.sp * 1.8f,
                            letterSpacing = (-0.011).em
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    BasicTextField( //질문 본문
                        value = body,
                        onValueChange = { body = it },
                        singleLine = false,
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = figmaTextSizeSp(11f),
                            fontFamily = NotoSansKR,
                            lineHeight = 11.sp * 1.8f,
                            letterSpacing = (-0.011).em
                        ),
                        decorationBox = { inner ->
                            if (body.isBlank()) { //플레이스 홀더
                                Text(
                                    text = "동아리 부원들에게 궁금한 것을 물어보세요.",
                                    fontSize = figmaTextSizeSp(11f),
                                    fontFamily = NotoSansKR,
                                    lineHeight = 11.sp * 1.5f,
                                    letterSpacing = (-0.011).em,
                                    color = Color(0xFF7D7D7D)
                                )
                            }
                            inner()
                        },
                        enabled = selectedClubId != null || questionId != 0L
                    )
                }
            }

            //하단바
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .figmaPadding(bottomPx = 20f)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .imePadding(),
                //가운데 정렬 + 두 버튼 사이 11
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    11.dp,
                    Alignment.CenterHorizontally
                )
            ) {
                Image(
                    painter = painterResource(
                        if (isAnonymous)
                            R.drawable.btn_anonymous
                        else
                            R.drawable.btn_anonymous_disabled
                    ),
                    contentDescription = "익명버튼",
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isAnonymous = !isAnonymous }
                )

                Image(
                    painter = painterResource(R.drawable.btn_qna_submit),
                    contentDescription = "질문 등록 버튼",
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        //등록/수정 연타 방지
                        scope.launch {
                            navGuard.run {
                                val trimmed = body.trim()
                                if (questionId == 0L) { //신규 등록
                                    val cid = selectedClubId ?: return@run
                                    vm.create(cid, trimmed, isAnonymous)
                                } else { //수정
                                    vm.update(questionId, trimmed, isAnonymous)
                                }
                            }
                        }
                    }
                )
            }

            //성공 시 뒤로가기
            if (ui.success) {
                LaunchedEffect(ui.success) {
                    navController.popBackStack()
                }
            }
        }
    }
}