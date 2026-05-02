package com.appcenter.uniclub.ui.qna

import android.view.WindowManager
import retrofit2.HttpException
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.appcenter.uniclub.App
import com.appcenter.uniclub.R
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.network.dto.AnswerResponseDto
import com.appcenter.uniclub.ui.components.Dialog
import com.appcenter.uniclub.ui.components.SetSoftInputMode
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.TimeUtils
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//등록된 질문 확인 페이지
//내부 상수
private val BAR_HEIGHT = 40.dp //댓글 입력바 높이
private val FIELD_INNER_H = 14.dp //텍스트 필드 안쪽 좌우 여백
private val SEND_TAP = 30.dp //전송 아이콘 터치 영역
private val EXTRA_LIFT_DP = 16.dp
private const val OPEN_DURATION_MS = 160 //열릴 때 추가 리프트 애니메이션 시간
private const val STABLE_FRAMES_REQUIRED = 3 //닫힘 안정화 프레임 수

//IME가 열릴 때만 적용되는 추가리프트
@Composable
private fun rememberImeExtraLiftPx(extraLiftWhenImeDp: Dp = EXTRA_LIFT_DP): Float {
    val density = LocalDensity.current
    val anim = remember { Animatable(0f) }
    //IME, 네비 인셋
    val imeBottomPx = WindowInsets.ime.getBottom(density).toFloat()
    val navBottomPx = WindowInsets.navigationBars.getBottom(density).toFloat()
    //LaunchedEffect 안에서 최신 네비 값을 읽을 수 있도록 상태 래핑
    val navPxState = rememberUpdatedState(navBottomPx)
    val imeVisible = imeBottomPx > 0f
    val targetPxWhenOpen = with(density) { extraLiftWhenImeDp.toPx() }

    LaunchedEffect(imeVisible) {
        if (imeVisible) {
            //IME 열림
            anim.animateTo(
                targetValue = targetPxWhenOpen,
                animationSpec = tween(durationMillis = OPEN_DURATION_MS, easing = FastOutSlowInEasing)
            )
        } else {
            //IME 닫힘
            var stable = 0
            var last = navPxState.value
            while (stable < STABLE_FRAMES_REQUIRED) {
                withContext(Dispatchers.Main) { awaitFrame() } // 다음 프레임 대기
                val now = navPxState.value
                if (now == last) {
                    stable++
                } else {
                    stable = 0
                    last = now
                }
            }
            anim.snapTo(0f)
        }
    }
    return anim.value
}

//화면
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuestionScreen(
    navController: NavHostController,
    questionId: Long
) {
    SetSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

    val app = LocalContext.current.applicationContext as App
    val vm: QuestionDetailViewModel = viewModel(
        factory = QuestionDetailViewModelFactory(ServiceLocator.qnaRepository(app))
    )

    //연타 방지용
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) } //뒤로가기/네비게이션
    val actionGuard = remember { NavGuard(lockMs = 600L) } //서버 액션(전송/삭제/완료 등)

    //로딩
    LaunchedEffect(questionId) { vm.load(questionId) }
    val ui by vm.ui.collectAsState()

    var showQuestionDeletedDialog by rememberSaveable { mutableStateOf(false) }

    //error가 바뀔 때마다 404인지 체크해서 다이얼로그 띄움
    LaunchedEffect(ui.error) {
        val http = ui.error as? HttpException
        if (http?.code() == 404) {
            showQuestionDeletedDialog = true
        }
    }

    //오버레이 상태
    var showAction by remember { mutableStateOf(false) }
    var selectedAnswerId by remember { mutableStateOf<Long?>(null) }
    var selectedAnswerMine by remember { mutableStateOf(false) }
    var showAnsweredDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var deletedIds by remember { mutableStateOf(setOf<Long>()) } //삭제 표시용
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val imeBottomDp = with(density) {
        WindowInsets.ime.getBottom(this).toDp()
    }
    val isImeVisible = imeBottomDp > 0.dp

    //답글 모드
    var replyParentId by remember { mutableStateOf<Long?>(null) }
    var replyTargetName by remember { mutableStateOf<String?>(null) }

    //입력바
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val inputFocusRequester = remember { FocusRequester() }

    var reportReason by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->

        if (showQuestionDeletedDialog) {
            Dialog(
                title = "삭제된 질문입니다.",
                message = "해당 질문을 찾을 수 없습니다.",
                onDismiss = {
                    showQuestionDeletedDialog = false
                    navController.popBackStack()
                },
                onConfirm = {
                    showQuestionDeletedDialog = false
                    navController.popBackStack()
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(2f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(
                    //뒤로가기 연타 방지
                    onBackClick = {
                        scope.launch {
                            navGuard.run { navController.popBackStack() }
                        }
                    },
                    title = "질의응답"
                )
            }

            //본문 리스트 (IME 무시)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        //빈 공간 탭 시 키보드 닫기 + 포커스 해제
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                            keyboard?.hide()

                            replyParentId = null
                            replyTargetName = null
                        })
                    }
                    .figmaPadding(topPx = 40f),
                contentPadding = PaddingValues(
                    bottom = if (isImeVisible) imeBottomDp + 130.dp else 90.dp
                )
            ) {

                val q = ui.data
                if (q != null) {
                    item { //질문 헤더
                        Spacer(modifier = Modifier.height(26.dp))
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .figmaPadding(startPx = 35f, endPx = 25f)
                        ) {
                            Image( //프로필 이미지
                                painter =
                                    if (!q.profile.isNullOrBlank())
                                        rememberAsyncImagePainter(q.profile)
                                    else painterResource(R.drawable.default_image),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .figmaSize(widthPx = 35f, heightPx = 37f)
                                    .clip(RoundedCornerShape(29.dp))
                            )

                            Spacer(modifier = Modifier.width(11.dp))

                            Column { //작성자, 시각, 동아리, 본문
                                Text(
                                    text = q.nickname,
                                    fontSize = figmaTextSizeSp(12f),
                                    fontFamily = NotoSansKR,
                                    lineHeight = 12.sp * 1.5f,
                                    letterSpacing = (-0.011).em,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = TimeUtils.toFormattedTime(q.updatedAt),
                                    fontSize = figmaTextSizeSp(12f),
                                    fontFamily = NotoSansKR,
                                    lineHeight = 8.sp * 1.5f,
                                    letterSpacing = (-0.011).em,
                                    color = Color(0xFF898989)
                                )
                                Spacer(modifier = Modifier.height(7.dp))
                                Text(
                                    text = "@${q.clubName}",
                                    fontSize = figmaTextSizeSp(11f),
                                    fontFamily = NotoSansKR,
                                    lineHeight = 11.sp * 1.5f,
                                    letterSpacing = (-0.011).em,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFFF5900)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = q.content,
                                    fontSize = figmaTextSizeSp(11f),
                                    fontFamily = NotoSansKR,
                                    lineHeight = 11.sp * 1.5f,
                                    letterSpacing = (-0.011).em
                                )
                            }

                            Spacer(Modifier.weight(1f))

                            if (q.president) {
                                var answered by remember(q.answered) { mutableStateOf(q.answered) }
                                Box( //답변완료 토글 (운영진한테만 노출)
                                    modifier = Modifier.clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        //다이얼로그 여는 것도 연타로 여러 번 뜨는 것 방지
                                        scope.launch {
                                            actionGuard.run { showAnsweredDialog = true }
                                        }
                                    }
                                ) {
                                    Image(
                                        painter = painterResource(
                                            if (answered) R.drawable.btn_answered
                                            else R.drawable.btn_answered_disabled
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.figmaSize(widthPx = 70f, heightPx = 23f)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Box(modifier = Modifier.figmaPadding(startPx = 26f, topPx = 33f, endPx = 26f, bottomPx = 10f)
                        ) { Divider(color = Color(0xFFEBEBEB), thickness = 1.dp) }
                    }

                    //댓글(부모), 대댓글(자식) 렌더
                    val parents = q.answers.filter { it.parentAnswerId == null }
                    val childrenMap = q.answers.filter { it.parentAnswerId != null }.groupBy { it.parentAnswerId!! }

                    itemsIndexed(parents, key = { _, item -> item.answerId }) { parentIndex, parent ->
                        val parentIsDeleted = parent.deleted || deletedIds.contains(parent.answerId)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AnswerParentItem(
                                ans = parent,
                                isDeleted = parentIsDeleted,
                                answered = q.answered,
                                isHighlighted = (replyParentId == parent.answerId),
                                onMore = { mine ->
                                    selectedAnswerId = parent.answerId
                                    selectedAnswerMine = mine
                                    showAction = true
                                },
                                onReplyClick = {
                                    replyParentId = parent.answerId
                                    replyTargetName = parent.nickname

                                    inputFocusRequester.requestFocus()
                                    keyboard?.show()

                                    scope.launch {
                                        delay(300)
                                        listState.animateScrollToItem(
                                            index = parentIndex + 2,
                                            scrollOffset = -450
                                        )
                                    }
                                }
                            )

                            val replies = childrenMap[parent.answerId].orEmpty()
                            replies.forEach { child ->
                                AnswerChildItem(
                                    ans = child,
                                    answered = q.answered,
                                    onMore = { mine ->
                                        selectedAnswerId = child.answerId
                                        selectedAnswerMine = mine
                                        showAction = true
                                    }
                                )
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(70.dp)) }
                }
            }

            //하단 댓글 입력 바
            CommentInputBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .zIndex(2f),
                focusRequester = inputFocusRequester,
                replyParentId = replyParentId,
                replyTargetName = replyTargetName,
                onSend = { text, anonymous, parentId ->
                    //전송 연타 방지 (중복 댓글/대댓글 방지)
                    scope.launch {
                        actionGuard.run {
                            vm.sendAnswer(
                                questionId = questionId,
                                parentId = parentId,
                                content = text,
                                anonymous = anonymous
                            )
                            replyParentId = null
                            replyTargetName = null
                        }
                    }
                }
            )

            //삭제,신고 오버레이
            BottomOneActionPopup(
                visible = showAction,
                mine = selectedAnswerMine,
                onDismiss = { showAction = false },
                onReportClick = { showReportDialog = true },
                onDeleteClick = {
                    //다이얼로그 여러 번 열리는 것 방지
                    scope.launch {
                        actionGuard.run {
                            showDeleteDialog = true
                            showAction = false
                        }
                    }
                }
            )

            //답변 완료 다이얼로그
            if (showAnsweredDialog) {
                Dialog(
                    title = "답변 완료로 변경하시겠습니까?",
                    message = "답변 완료 시 수정 및 삭제가 불가능합니다.",
                    onDismiss = { showAnsweredDialog = false },
                    onConfirm = {
                        //완료 처리 연타 방지
                        scope.launch {
                            actionGuard.run {
                                vm.markAnswered(questionId)
                                showAnsweredDialog = false
                            }
                        }
                    }
                )
            }

            //삭제,신고 다이얼로그
            if (showDeleteDialog) {
                Dialog(
                    title = "이 댓글을 삭제하시겠습니까?",
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        val id = selectedAnswerId ?: return@Dialog
                        //삭제 연타 방지
                        scope.launch {
                            actionGuard.run {
                                vm.deleteAnswer(id, questionId)
                                deletedIds = deletedIds + id
                                showDeleteDialog = false
                            }
                        }
                    }
                )
            }
            if (showReportDialog) {
                ReportDialog(
                    title = "이 답변을 신고하시겠습니까?",
                    reason = reportReason,
                    onReasonChange = { reportReason = it },
                    onDismiss = {
                        showReportDialog = false
                        reportReason = ""
                    },
                    onConfirm = { reason ->
                        val answerId = selectedAnswerId ?: return@ReportDialog

                        vm.reportAnswer(answerId, reason) {
                            showReportDialog = false
                            reportReason = ""
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AnswerParentItem(
    ans: AnswerResponseDto,
    isDeleted: Boolean,
    answered: Boolean,
    isHighlighted: Boolean,
    onMore: (mine: Boolean) -> Unit,
    onReplyClick: () -> Unit
) {
    CommentCardBox(
        name = ans.nickname,
        text = ans.content,
        profile = ans.profile,
        time = ans.updateTime,
        isPresident = ans.president,
        isDeleted = isDeleted,
        answered = answered,
        mine = ans.owner,
        isHighlighted = isHighlighted,
        onMoreClick = { onMore(ans.owner) },
        onReplyClick = onReplyClick
    )
}

@Composable
private fun AnswerChildItem(
    ans: AnswerResponseDto,
    answered: Boolean,
    onMore: (mine: Boolean) -> Unit
) {
    ReplyCardBox(
        name = ans.nickname,
        text = ans.content,
        profile = ans.profile,
        time = ans.updateTime,
        isPresident = ans.president,
        answered = answered,
        mine = ans.owner,
        onMoreClick = { onMore(ans.owner) }
    )
}

//댓글 카드
@Composable
fun CommentCardBox(
    name: String,
    text: String,
    isDeleted: Boolean,
    profile: String? = null,
    time: String,
    isPresident: Boolean,
    answered: Boolean,
    mine: Boolean,
    isHighlighted: Boolean,
    modifier: Modifier = Modifier,
    minWidth: Dp = 308.dp,
    minHeight: Dp = 79.dp,
    cornerRadius: Dp = 22.dp,
    shadowElevation: Dp = 8.dp,
    shadowColor: Color = Color(0x20000000),
    onMoreClick: () -> Unit = {},
    onReplyClick: () -> Unit = {}
) {
    //카드 내부 고정 패딩
    val fixedPadding = PaddingValues(start = 20.dp, top = 11.dp, end = 13.dp, bottom = 7.dp)

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
            .background(
                if (isHighlighted) Color(0xFFCACACA) else Color.White
            )
            .padding(fixedPadding)
    ) {
        val avatar = 30.dp
        val gap = 8.dp
        val bodyIndent = avatar + gap //본문 들여쓰기 (프로필+간격만큼)

        Column(modifier = Modifier.fillMaxWidth()) { //댓글 헤더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter =
                        if (!profile.isNullOrBlank())
                            rememberAsyncImagePainter(profile)
                        else painterResource(R.drawable.default_image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(avatar)
                        .clip(RoundedCornerShape(29.dp))
                )
                Spacer(Modifier.width(gap))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = name,
                            fontFamily = NotoSansKR,
                            fontSize = figmaTextSizeSp(11f),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 11.sp * 1.5f,
                            letterSpacing = (-0.011).em
                        )
                        if (isPresident) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Image(
                                painter = painterResource(R.drawable.ic_president_mark),
                                contentDescription = "동아리 회장",
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                    Text(
                        text = TimeUtils.toFormattedTime(time),
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(9f),
                        lineHeight = 9.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color(0xFF7D7D7D)
                    )
                }

                if (!isDeleted && !(answered && mine)) {
                    Box( //더보기 버튼
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
            }

            Spacer(Modifier.height(10.dp))

            Column( //본문, 답글쓰기 버튼
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = bodyIndent)
            ) {
                Text(
                    text = if (isDeleted) "삭제된 댓글입니다." else text,
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(11f),
                    lineHeight = 11.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = if (isDeleted) Color(0xFF9F9F9F) else Color.Black
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.weight(1f))
                    if (!isDeleted) {
                        Text(
                            text = "답글쓰기",
                            fontFamily = NotoSansKR,
                            fontSize = figmaTextSizeSp(9f),
                            lineHeight = 9.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color(0xFFA9A9A9),
                            modifier = Modifier
                                .offset(x = (-5).dp, y = (-1).dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onReplyClick() }
                        )
                    }
                }
            }
        }
    }
}

//답글 카드
@Composable
fun ReplyCardBox(
    name: String,
    text: String,
    profile: String? = null,
    time: String,
    isPresident: Boolean,
    answered: Boolean,
    mine: Boolean,
    modifier: Modifier = Modifier,
    minWidth: Dp = 272.dp,
    minHeight: Dp = 79.dp,
    cornerRadius: Dp = 20.dp,
    shadowElevation: Dp = 8.dp,
    shadowColor: Color = Color(0x1A000000),
    onMoreClick: () -> Unit = {}
) {
    val fixedPadding = PaddingValues(start = 20.dp, top = 11.dp, end = 13.dp, bottom = 7.dp)

    Row {
        Image( //답글 용 화살표
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
                        painter =
                            if (!profile.isNullOrBlank())
                                rememberAsyncImagePainter(profile)
                            else painterResource(R.drawable.default_image),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(avatar)
                            .clip(RoundedCornerShape(29.dp))
                    )
                    Spacer(Modifier.width(gap))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = name,
                                fontFamily = NotoSansKR,
                                fontSize = figmaTextSizeSp(11f),
                                fontWeight = FontWeight.Medium,
                                lineHeight = 11.sp * 1.5f,
                                letterSpacing = (-0.011).em
                            )

                            if (isPresident) {
                                Spacer(modifier = Modifier.width(2.dp))
                                Image(
                                    painter = painterResource(R.drawable.ic_president_mark),
                                    contentDescription = "동아리 회장",
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                        Text(
                            text = TimeUtils.toFormattedTime(time),
                            fontFamily = NotoSansKR,
                            fontSize = figmaTextSizeSp(9f),
                            lineHeight = 9.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color(0xFF7D7D7D)
                        )
                    }

                    if (!(answered && mine)) {
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
                }
            }
        }
    }
}

//하단 댓글 입력 바
@Composable
fun CommentInputBar(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    replyParentId: Long?,
    replyTargetName: String?,
    onSend: (String, Boolean, Long?) -> Unit,
    paddingStart: Dp = 17.dp,
    paddingEnd: Dp = 17.dp,
    paddingBottom: Dp = 24.dp
) {
    var text by remember { mutableStateOf("") }
    var anonymous by remember { mutableStateOf(false) } //익명 여부
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val btnW = 50.dp
    val gap = 8.dp
    val innerHPadding = FIELD_INNER_H
    val sendTap = SEND_TAP
    val maxBarHeight = 110.dp

    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeBottom > 0
    val actualBottomPadding = if (isImeVisible) 0.dp else paddingBottom

    Box(
        modifier = modifier
            .imePadding()
            .padding(start = paddingStart, end = paddingEnd, bottom = actualBottomPadding)
            .heightIn(min = BAR_HEIGHT, max = maxBarHeight),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = BAR_HEIGHT, max = maxBarHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image( //익명 버튼
                painter = painterResource(
                    if (anonymous) R.drawable.btn_anonymous2
                    else R.drawable.btn_anonymous_disabled2
                ),
                contentDescription = "익명",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .size(width = btnW, height = BAR_HEIGHT)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { anonymous = !anonymous }
            )

            Spacer(Modifier.width(gap))

            Box( //댓글 필드
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = BAR_HEIGHT, max = maxBarHeight),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(shape = RoundedCornerShape(18.dp))
                        .background(Color(0xFF2B2B2B))
                )

                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = false,
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
                                onSend(t, anonymous, replyParentId)
                                text = ""
                                keyboardController?.hide()
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
                        )
                        .focusRequester(focusRequester),
                    decorationBox = { inner ->
                        if (text.isEmpty()) {
                            Text(
                                text = if (replyParentId != null && !replyTargetName.isNullOrBlank())
                                    "대댓글을 입력하세요."
                                else
                                    "댓글을 입력하세요.",
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

                if (text.isNotEmpty()) { //전송 버튼 (텍스트 있을 때만 노출)
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
                                    onSend(t, anonymous, replyParentId)
                                    text = ""
                                    keyboardController?.hide()
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
    visible: Boolean,
    mine: Boolean,
    onDismiss: () -> Unit,
    onReportClick: () -> Unit,
    onDeleteClick: () -> Unit,
    paddingStart: Dp = 17.dp,
    paddingEnd: Dp = 17.dp,
    paddingBottom: Dp = 9.dp
) {
    if (!visible) return

    //입력바랑 동일한 방식
    val extraLiftPx = rememberImeExtraLiftPx(EXTRA_LIFT_DP)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) //반투명 배경
            .zIndex(3f)
    ) {
        //빈 공간만 닫기되도록
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        )

        Row(
            modifier = Modifier
                .graphicsLayer { translationY = -extraLiftPx }
                .align(Alignment.BottomEnd)
                .padding(start = paddingStart, end = paddingEnd, bottom = paddingBottom),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    if (mine) R.drawable.btn_delete else R.drawable.btn_report
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