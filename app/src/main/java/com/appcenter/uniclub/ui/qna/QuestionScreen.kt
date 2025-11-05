package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.zIndex
import com.appcenter.uniclub.ui.components.Dialog
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController


//등록된 질문 확인 페이지
@Composable
fun QuestionScreen(
    navController: NavHostController,
    questionId: Long
) {
    //답변완료 버튼 노출 여부, 상태
    var showanswered by remember { mutableStateOf(true) }
    var answered by remember { mutableStateOf(false) }

    var mine by remember { mutableStateOf(true) } //내 글 여부 (삭제, 신고 분리)
    var showAction by remember { mutableStateOf(false) } //오버레이 노출

    var showDeleteDialog by remember { mutableStateOf(false) } //삭제, 신고 다이얼로그
    var showReportDialog by remember { mutableStateOf(false) }

    //하단바 치수
    val barH = 40.dp
    val listBottomPadding = barH + 24.dp + 12.dp

    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                //화면 빈 곳 탭하면 포커스 해제 + 키보드 내림
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        keyboard?.hide()
                    })
                },
            contentPadding = PaddingValues(
                top = 27.dp,
                bottom = listBottomPadding, // ← 하단바에 가리지 않도록
            )
        ) {
            item { //상단바
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopBar(
                        onBackClick = { navController.popBackStack() },
                        title = "질의응답"
                    )
                }
            }

            item { //질문 헤더
                Spacer(modifier = Modifier.height(33.dp))
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .figmaPadding(startPx = 35f, endPx = 25f)
                ) {
                    Image( //프로필 이미지
                        painter = painterResource(R.drawable.default_image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .figmaSize(widthPx = 35f, heightPx = 37f)
                            .clip(RoundedCornerShape(29.dp))
                    )

                    Spacer(modifier = Modifier.width(11.dp))

                    Column {
                        Text( //작성자명
                            text = "홍길동",
                            fontSize = figmaTextSizeSp(12f),
                            fontFamily = NotoSansKR,
                            lineHeight = 12.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            fontWeight = FontWeight.Medium
                        )
                        Text( //작성일시
                            text = "07. 07   13:13",
                            fontSize = figmaTextSizeSp(8f),
                            fontFamily = NotoSansKR,
                            lineHeight = 8.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color(0xFF898989)
                        )
                        Spacer(modifier = Modifier.height(7.dp))
                        Text( //동아리명
                            text = "@Appcenter",
                            fontSize = figmaTextSizeSp(11f),
                            fontFamily = NotoSansKR,
                            lineHeight = 11.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFF5900)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text( //본문
                            text = "동아리 질문 작성예시 입니다.",
                            fontSize = figmaTextSizeSp(11f),
                            fontFamily = NotoSansKR,
                            lineHeight = 11.sp * 1.5f,
                            letterSpacing = (-0.011).em
                        )
                    }
                    Spacer(Modifier.weight(1f)) //오른쪽 끝으로 밀기
                    Box( //답변완료 버튼 (운영진만 보임)
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ){answered = !answered}
                    ) {
                        if(showanswered){
                            Image(
                                painter = painterResource(
                                    if(answered) R.drawable.btn_answered
                                    else R.drawable.btn_answered_disabled
                                ),
                                contentDescription = null,
                                modifier = Modifier.figmaSize(widthPx = 70f, heightPx = 23f)
                            )
                        }
                    }
                }
            }

            item { //구분선
                Box(
                    modifier = Modifier.figmaPadding(startPx = 26f, topPx = 33f, endPx = 26f, bottomPx = 10f)
                ) {
                    Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
                }
            }

            items(count = 7, key = { it }) { idx ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (idx) {
                        0 -> CommentCardBox(text = "9월에 모집합니다", onMoreClick = { showAction = true })
                        1 -> ReplyCardBox(text = "면접이 있을까요?", onMoreClick = { showAction = true })
                        2 -> CommentCardBox(text = "@@", onMoreClick = { showAction = true })
                        3 -> ReplyCardBox(text = "감사합니다!!", onMoreClick = { showAction = true })
                        else -> CommentCardBox(text = "많은 지원부탁드립니다~!", onMoreClick = { showAction = true })
                    }
                }
            }
        }

        //하단바 (댓글 작성 영역)
        CommentInputBarImages(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .imePadding() //키보드 올라오면 바만 밀림
                .zIndex(2f),
            onSend = { /* 전송 */ }
        )

        //버튼 눌렀을 때 오버레이 버튼 (삭제, 신고)
        BottomOneActionPopup(
            visible = showAction,
            mine = mine,
            onDismiss = { showAction = false },
            onReportClick = { showReportDialog = true },
            onDeleteClick = { showDeleteDialog = true }
        )

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

//댓글 카드
@Composable
fun CommentCardBox(
    text: String,
    modifier: Modifier = Modifier,
    minWidth: Dp = 308.dp,
    minHeight: Dp = 79.dp,
    cornerRadius: Dp = 20.dp,
    shadowElevation: Dp = 8.dp,
    shadowColor: Color = Color(0x1A000000), //그림자 색상
    onMoreClick: () -> Unit = {}
) {
    val fixedPadding = PaddingValues( //카드 내부 고정 패딩
        start = 20.dp, top = 11.dp, end = 13.dp, bottom = 7.dp
    )

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(cornerRadius),
                clip = false,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White)
            .padding(fixedPadding) //고정 패딩 적용
    ) {
        val avatar = 30.dp
        val gap = 8.dp
        val bodyIndent = avatar + gap

        Column(modifier = Modifier.fillMaxWidth()) {
            //댓글 헤더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image( //프로필 이미지
                    painter = painterResource(R.drawable.default_image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatar)
                        .clip(RoundedCornerShape(29.dp))
                )
                Spacer(Modifier.width(gap))

                Column(modifier = Modifier.weight(1f)) {
                    Text( //작성자명 (익명)
                        text = "익명",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(11f),
                        fontWeight = FontWeight.Medium,
                        lineHeight = 11.sp * 1.5f,
                        letterSpacing = (-0.011).em
                    )
                    Text( //작성일시
                        text = "07.07  13:13",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(9f),
                        lineHeight = 9.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color(0xFF7D7D7D)
                    )
                }

                Box( //더보기 버튼 (누르면 삭제, 신고 버튼)
                    modifier = Modifier
                        .size(13.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onMoreClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more_vert),
                        contentDescription = "더보기",
                        tint = Color(0xFF7D7D7D)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))

            //본문(들여쓰기 적용)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = bodyIndent)
            ) {
                Text(
                    text = text,
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(11f),
                    lineHeight = 11.sp * 1.5f,
                    letterSpacing = (-0.011).em
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "답글쓰기",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(9f),
                        lineHeight = 9.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color(0xFFA9A9A9),
                        modifier = Modifier
                            .offset(y=(-5).dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                //TODO
                            }
                    )
                }
            }
        }
    }
}

//답글 카드
@Composable
fun ReplyCardBox(
    text: String,
    modifier: Modifier = Modifier,
    minWidth: Dp = 272.dp,
    minHeight: Dp = 79.dp,
    cornerRadius: Dp = 20.dp,
    shadowElevation: Dp = 8.dp,
    shadowColor: Color = Color(0x1A000000),
    onMoreClick: () -> Unit = {}
) {
    val fixedPadding = PaddingValues(
        start = 20.dp, top = 11.dp, end = 13.dp, bottom = 7.dp
    )

    Row {
        Image(
            painter = painterResource(R.drawable.ic_reply),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(17.dp))
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = minWidth, minHeight = minHeight)
                .shadow(
                    elevation = shadowElevation,
                    shape = RoundedCornerShape(cornerRadius),
                    clip = false,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .clip(RoundedCornerShape(cornerRadius))
                .background(Color.White)
                .padding(fixedPadding)
        ) {
            val avatar = 30.dp
            val gap = 8.dp
            val bodyIndent = avatar + gap

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(R.drawable.default_image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(avatar)
                            .clip(RoundedCornerShape(29.dp))
                    )
                    Spacer(Modifier.width(gap))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "익명",
                            fontFamily = NotoSansKR,
                            fontSize = figmaTextSizeSp(11f),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 11.sp * 1.5f,
                            letterSpacing = (-0.011).em
                        )
                        Text(
                            text = "07.07  13:13",
                            fontFamily = NotoSansKR,
                            fontSize = figmaTextSizeSp(9f),
                            lineHeight = 9.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color(0xFF7D7D7D)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onMoreClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_more_vert),
                            contentDescription = "더보기",
                            tint = Color(0xFF7D7D7D)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = bodyIndent)
                ) {
                    Text(
                        text = text,
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(11f),
                        lineHeight = 11.sp * 1.5f,
                        letterSpacing = (-0.011).em
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "답글쓰기",
                            fontFamily = NotoSansKR,
                            fontSize = figmaTextSizeSp(9f),
                            lineHeight = 9.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color(0xFFA9A9A9),
                            modifier = Modifier.offset(y=(-5).dp)
                        )
                    }
                }
            }
        }
    }
}

//하단 댓글 입력 바
@Composable
fun CommentInputBarImages(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit,
    paddingStart: Dp = 17.dp,
    paddingEnd: Dp = 17.dp,
    paddingBottom: Dp = 24.dp
) {
    // 입력값/익명상태
    var text by remember { mutableStateOf("") }
    var anonymous by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    // 레이아웃 상수 (Figma 기준)
    val barH = 40.dp
    val btnW = 50.dp
    val gap = 8.dp
    val innerHPadding = 14.dp
    val sendTap = 30.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = paddingStart, end = paddingEnd, bottom = paddingBottom)
            .height(barH),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barH),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //익명 버튼
            Image(
                painter = painterResource(
                    if (anonymous) R.drawable.btn_anonymous2
                    else R.drawable.btn_anonymous_disabled2
                ),
                contentDescription = "익명",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(width = btnW, height = barH)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { anonymous = !anonymous }
            )

            Spacer(Modifier.width(gap))

            //댓글 입력 필드
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barH),
                contentAlignment = Alignment.CenterStart
            ) {
                // 배경 PNG
                Image(
                    painter = painterResource(R.drawable.bg_comment_input),
                    contentDescription = "입력 배경",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )

                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = figmaTextSizeSp(11f),
                        lineHeight = 11.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        fontFamily = NotoSansKR
                    ),
                    cursorBrush = SolidColor(Color.White),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            val t = text.trim()
                            if (t.isNotEmpty()) {
                                onSend(t)
                                text = ""
                                focusManager.clearFocus()
                            }
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .padding(
                            start = innerHPadding,
                            end = if (text.isNotEmpty()) innerHPadding + sendTap else innerHPadding
                        ),
                    decorationBox = { inner ->
                        if (text.isEmpty()) {
                            Text(
                                text = "댓글을 입력하세요.",
                                fontSize = figmaTextSizeSp(11f),
                                fontFamily = NotoSansKR,
                                lineHeight = 11.sp * 1.5f,
                                letterSpacing = (-0.011).em,
                                color = Color(0xFFBFBFBF)
                            )
                        }
                        inner()
                    }
                )

                //전송 버튼(텍스트 있을 때만)
                if (text.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(sendTap)
                            .padding(end = 9.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                val t = text.trim()
                                if (t.isNotEmpty()) {
                                    onSend(t)
                                    text = ""
                                    focusManager.clearFocus()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.btn_send),
                            contentDescription = "전송",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

//하단 오버레이 버튼
@Composable
fun BottomOneActionPopup(
    visible: Boolean, //노출 여부
    mine: Boolean, //내 글 여부
    onDismiss: () -> Unit,
    onReportClick: () -> Unit, //신고 액션
    onDeleteClick: () -> Unit, //삭제 액션
    paddingStart: Dp = 17.dp,
    paddingEnd: Dp = 17.dp,
    paddingBottom: Dp = 9.dp
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) //어두운 배경
            .clickable( //바깥 클릭 시 닫기
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .zIndex(3f)
    ) {
        //하단 입력바와 같은 위치
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = paddingEnd, start = paddingStart, bottom = paddingBottom)
                .imePadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    if (mine) { //내 글: 삭제 버튼
                        R.drawable.btn_delete
                    } else { //남 글: 신고 버튼
                        R.drawable.btn_report
                    }
                ),
                contentDescription = if (mine) "삭제" else "신고",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .figmaSize(widthPx = 344f, heightPx = 67f)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (mine) onDeleteClick() else onReportClick()
                        onDismiss()
                    }
            )
        }
    }
}