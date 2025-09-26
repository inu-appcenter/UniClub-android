package com.appcenter.uniclub.ui.promotion

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaTextSizeSp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import org.burnoutcrew.reorderable.ReorderableItem
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcenter.uniclub.App
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import android.app.TimePickerDialog
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.ui.text.TextStyle
import java.util.*

//관리자용 홍보 페이지

//SNS 플랫폼 구분
private enum class Platform { APPLY, YOUTUBE, INSTAGRAM }

@Composable
fun AdminPromotionScreen(
    navController: NavHostController,
    clubId: Long,
    app: App = LocalContext.current.applicationContext as App,
    vm: AdminPromotionViewModel = viewModel(
        factory = AdminPromotionViewModelFactory(app, clubId)
    )
) {
    val ui by vm.ui.collectAsState()

    // 링크 다이얼로그 상태 (화면 지역 상태로 OK)
    var showLinkDialog by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<Platform?>(null) }
    var tempLink by remember { mutableStateOf("") }

    val bannerPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) vm.uploadBanner(uri) }

    val profilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) vm.uploadProfile(uri) }

    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }
    val activityPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val idx = selectedSlotIndex ?: return@rememberLauncherForActivityResult
        if (uri != null) vm.uploadActivityAt(idx, uri)
        selectedSlotIndex = null
    }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) //스크롤
                .zIndex(0f)
        ) {
            //배너 + 상단바 + 프로필 사진 겹치는 구조
            Box(modifier = Modifier.height(209.dp)) {
                //배너 (탭하여 업로드)
                EditableBanner(
                    bannerUri = ui.bannerUri,
                    onClick = {
                        bannerPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                //상단 뒤로가기 버튼
                Box(
                    modifier = Modifier
                        .figmaPadding(startPx = 18f, topPx = 18f)
                        .size(20.dp)
                        .align(Alignment.TopStart)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { navController.popBackStack() }
                        .zIndex(1f), //배너 위로
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back_white),
                        contentDescription = "Back",
                        modifier = Modifier.figmaSize(widthPx = 11f, heightPx = 20f)
                    )
                }

                //프로필 이미지
                EditableProfile(
                    profileUri = ui.profileUri,
                    onClick = {
                        profilePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }

            //SNS 버튼 (유튜브, 인스타)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp, end = 15.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {Image(
                painter = painterResource(id = R.drawable.ic_apply),
                contentDescription = "Apply",
                modifier = Modifier
                    .size(62.dp, 30.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        editing = Platform.APPLY
                        tempLink = ui.applyLink
                        showLinkDialog = true
                    }
            )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            editing= Platform.YOUTUBE //어떤 SNS 편집하는지 저장
                            tempLink = ui.youtubeLink //기존 값 프리필
                            showLinkDialog = true
                        }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            editing = Platform.INSTAGRAM
                            tempLink = ui.instagramLink
                            showLinkDialog = true
                        }
                )
            }

            //동아리명 + 모집상태
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 143f, heightPx = 30f)
                        .clip(RoundedCornerShape(45.dp))
                        .background(Color(0xFFFF5900)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text( //동아리명
                        text = ui.clubName.ifBlank { "동아리명" },
                        color = Color.White,
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = figmaTextSizeSp(14f),
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 14.sp * 1.5f, //행간
                        letterSpacing = (-0.011).em //자간
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Image( //모집상태
                    painter = painterResource(
                        id = when (ui.recruitStatus) {
                            RecruitStatus.ACTIVE -> R.drawable.ic_admin_recruiting
                            RecruitStatus.SCHEDULED -> R.drawable.ic_admin_upcoming
                            RecruitStatus.CLOSED -> R.drawable.ic_admin_closed
                        }
                    ),
                    contentDescription = ui.recruitStatus.name,
                    modifier = Modifier
                        .offset(x = (-20).dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            vm.update {
                                it.copy(
                                    recruitStatus = when (it.recruitStatus) {
                                        RecruitStatus.SCHEDULED -> RecruitStatus.ACTIVE
                                        RecruitStatus.ACTIVE -> RecruitStatus.CLOSED
                                        RecruitStatus.CLOSED -> RecruitStatus.SCHEDULED
                                    }
                                )
                            }
                        }
                )
            }

            //동아리 정보 3개
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EditableInfoItem(
                    label = "동아리방",
                    value = ui.clubRoom,
                    onValueChange = { v -> vm.update { it.copy(clubRoom = v) } },
                    placeholder = "예) 4호관 107호",
                    modifier = Modifier.width(90.dp)
                )
                EditableInfoItem(
                    label = "회장",
                    value = ui.leaderName,
                    onValueChange = { v -> vm.update { it.copy(leaderName = v) } },
                    placeholder = "예) 홍길동",
                    modifier = Modifier.width(55.dp)
                )
                EditableInfoItem(
                    label = "연락처",
                    value = ui.contact,
                    onValueChange = { v -> vm.update { it.copy(contact = v) } },
                    placeholder = "010-1234-5678",
                    phone = true, //숫자,하이픈,공백만 허용 필터링
                    modifier = Modifier.width(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            //한줄 소개
            EditableTextShadowBanner(
                text = ui.intro,
                onTextChange = { v -> vm.update { it.copy(intro = v) } },
                placeholder = "동아리를 한 줄로 표현해보세요"
            )

            Spacer(modifier = Modifier.height(35.dp))

            //모집 안내, 공지
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                DateTimePickerRow(
                    label = "모집시작",
                    value = ui.recruitStartIso,
                    onValueChange = { v -> vm.update { it.copy(recruitStartIso = v) } }
                )
                DateTimePickerRow(
                    label = "모집종료",
                    value = ui.recruitEndIso,
                    onValueChange = { v -> vm.update { it.copy(recruitEndIso = v) } }
                )
                Spacer(modifier = Modifier.height(10.dp))

                EditableAnnouncementItem(
                    label = "공지",
                    value = ui.noticeText,
                    onValueChange = { v -> vm.update { it.copy(noticeText = v) } },
                    placeholder = "예) 25일 동아리실 출입금지"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(15.dp))

            //동아리 설명
            EditableClubDescription(
                text = ui.clubDescription,
                onTextChange = { v -> vm.update { it.copy(clubDescription = v) } },
                placeholder =  "동아리 소개글을 작성해보세요"
            )

            Spacer(modifier = Modifier.height(5.dp))

            //활동 사진 캐러셀 영역
            Text(
                text = "대표이미지",
                color = Color.Black,
                modifier = Modifier.padding(start = 30.dp),
                fontSize = figmaTextSizeSp(13f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 14.sp * 1.5f, //행간
            )

            Spacer(modifier = Modifier.height(7.dp))

            RepresentativeImagesCarousel(
                images = ui.activityImages,
                onPickAt = { index ->
                    //슬롯을 탭하면 해당 인덱스 저장, 이미지 피커 실행
                    selectedSlotIndex = index
                    activityPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveAt = { index -> vm.removeActivityAt(index) },
                onReorder = { from, to -> vm.reorderActivity(from, to) },
                placeholderRes = R.drawable.img_rep_placeholder
            )

            Spacer(modifier = Modifier.height(30.dp))

            //저장 버튼
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.btn_save),
                    contentDescription = "저장하기",
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        vm.save(
                            app = app,
                            onSuccess = {
                                // 저장 성공 시 이전 화면에 refresh 신호 전달
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("refresh", true)
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(75.dp))
        }

        //링크 입력 다이얼로그
        if (showLinkDialog) {
            LinkInputDialog(
                value = tempLink,
                onValueChange = { tempLink = it },
                title = "링크 추가",
                placeholder = "http:// or https://",
                onBack = { showLinkDialog = false },
                onDone = {
                    fun normalize(raw: String) = raw.trim().let {
                        if (it.isNotEmpty() && !it.startsWith("http://") && !it.startsWith("https://")) "https://$it" else it
                    }
                    val v = normalize(tempLink)
                    when (editing) {
                        Platform.APPLY -> vm.update { it.copy(applyLink = v) }
                        Platform.YOUTUBE -> vm.update { it.copy(youtubeLink = v) }
                        Platform.INSTAGRAM -> vm.update { it.copy(instagramLink = v) }
                        null -> {}
                    }
                    showLinkDialog = false
                },
                doneEnabled = Patterns.WEB_URL.matcher(tempLink).matches()
            )
        }
    }
}

//상단 배너 수정: 비어있으면 업로드 안내 텍스트, 탭으로 이미지 선택
@Composable
private fun EditableBanner(
    bannerUri: Uri?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
            .clickable { onClick() }
    ) {
        if (bannerUri != null) { //이미지가 있으면 꽉 채워서 표시
            AsyncImage(
                model = bannerUri,
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else { //없으면 배경 + 문구
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF585858)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "탭하여 업로드",
                    color = Color.White,
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(11f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

//프로필 이미지 수정: 비어있으면 업로드 안내 텍스트, 탭으로 이미지 선택
@Composable
private fun EditableProfile(
    profileUri: Uri?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(113.dp)
            .offset(x = 24.dp, y = 209.dp - 113.dp / 2)
            .clip(RoundedCornerShape(40.dp))
            .zIndex(1f) //배너 위에 보이도록
            .clickable { onClick() }
    ) {
        if (profileUri != null) {
            AsyncImage(
                model = profileUri,
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFCDCDCD)),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "탭하여 업로드",
                    color = Color.Black,
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(10f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

//SNS 링크 입력 다이얼로그
@Composable
private fun LinkInputDialog(
    value: String,
    onValueChange: (String) -> Unit,
    title: String,
    placeholder: String,
    onBack: () -> Unit,
    onDone: () -> Unit,
    doneEnabled: Boolean
) {
    Dialog(onDismissRequest = onBack) {
        //카드형 컨테이너
        Box(
            modifier = Modifier
                .figmaSize(widthPx = 280f, heightPx = 120f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                //상단 바: 뒤로, 제목, 완료
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    Box( //뒤로 버튼
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_back_arrow),
                            contentDescription = "뒤로",
                            modifier = Modifier
                                .figmaSize(widthPx = 7f, heightPx = 13f)
                        )
                    }

                    Text( //제목
                        text = title,
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        fontSize = figmaTextSizeSp(11f),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Text( //완료 (유효할 때만 클릭 가능)
                        text = "완료",
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        fontSize = figmaTextSizeSp(11f),
                        color = if (doneEnabled) Color.Black else Color(0xFFBEBEBE),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable(
                                enabled = doneEnabled,
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onDone() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                //입력 라벨
                Text(
                    text = "URL",
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(10f),
                    fontWeight = FontWeight.Medium,
                    color = if (value.isNotBlank()) Color(0xFFFF5900) else Color(0xFFB0B0B0),
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                //입력 박스 (내용 있으면 주황 테두리 + X 버튼)
                val borderColor = if (value.isNotBlank()) Color(0xFFFF5900) else Color.Transparent

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF5F5F5))
                        .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color(0xFFB0B0B0),
                                fontSize = figmaTextSizeSp(12f),
                                fontFamily = NotoSansKR
                            )
                        }
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.Black,
                                fontSize = figmaTextSizeSp(12f),
                                fontFamily = NotoSansKR
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    //X 버튼 (내용 있을 때만 노출)
                    if (value.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable { onValueChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", color = Color(0xFFFF5900), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

//동아리 정보 수정
@Composable
fun EditableInfoItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    phone: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        //라벨
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))

        //입력 필드
        Box(modifier = Modifier.fillMaxWidth()){
            BasicTextField(
                value = value,
                onValueChange = { s ->
                    //전화번호 모드면 숫자, 하이픈, 공백만 허용
                    val v = if (phone) s.filter { it.isDigit() || it == '-' || it == ' ' } else s
                    onValueChange(v)
                },
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.Black,
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    lineHeight = 11.sp * 1.5f
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (phone) KeyboardType.Phone else KeyboardType.Text
                ),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = Color(0xFFB0B0B0),
                            fontSize = figmaTextSizeSp(11f),
                            fontFamily = NotoSansKR
                        )
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

//한줄 소개 수정
@Composable
fun EditableTextShadowBanner(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
            .graphicsLayer { //그림자 효과
                shadowElevation = 10.dp.toPx()
                shape = RoundedCornerShape(0.dp)
                clip = false
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF9F9F9),
                        Color(0xFFFFFFFF)
                    )
                ),
                shape = RoundedCornerShape(0.dp)
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color(0xFFFF5900),
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f,
                fontWeight = FontWeight.Bold
            ),
            decorationBox = { inner ->
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF868686),
                        fontSize = figmaTextSizeSp(12f),
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Normal
                    )
                }
                inner()
            },
            modifier = Modifier
                .padding(start = 30.dp)
                .fillMaxWidth()
        )
    }
}

//모집기간 날짜,시간 설정
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current
    val cal = Calendar.getInstance()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 라벨
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            color = Color.Black,
            modifier = Modifier.width(70.dp)
        )

        // 선택된 값 표시
        Text(
            text = if (value.isBlank()) "미선택" else value,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            color = if (value.isBlank()) Color(0xFFB0B0B0) else Color.Black
        )

        Spacer(Modifier.weight(1f))

        // 📅 달력 버튼
        IconButton(onClick = { showDatePicker = true }) {
            Icon(Icons.Default.DateRange, contentDescription = "날짜 선택")
        }

        // ⏰ 시계 버튼
        IconButton(onClick = {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val newTime = "%02d:%02d".format(hour, minute)
                    val datePart = value.take(10).ifBlank { "2025-01-01" }
                    onValueChange("$datePart $newTime")
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }) {
            Icon(Icons.Default.AccessTime, contentDescription = "시간 선택")
        }
    }

    // 📅 머티리얼 DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        cal.timeInMillis = millis
                        val y = cal.get(Calendar.YEAR)
                        val m = cal.get(Calendar.MONTH) + 1
                        val d = cal.get(Calendar.DAY_OF_MONTH)
                        val newDate = "%04d-%02d-%02d".format(y, m, d)
                        val timePart = value.substringAfter(" ", "00:00")
                        onValueChange("$newDate $timePart")
                    }
                }) { Text("확인") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

//공지
@Composable
fun EditableAnnouncementItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //좌측 라벨
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            color = Color.Black,
            modifier = Modifier.width(70.dp)
        )

        //우측 한 줄 입력
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f
            ),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFFB0B0B0),
                        fontSize = figmaTextSizeSp(12f),
                        fontFamily = NotoSansKR
                    )
                }
                inner()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

//동아리 설명 수정
@Composable
fun EditableClubDescription(
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 20.dp)
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            singleLine = false, //여러 줄 입력
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = figmaTextSizeSp(13f),
                fontFamily = NotoSansKR,
                lineHeight = 13.sp * 1.75f,
                letterSpacing = (-0.03).em
            ),
            decorationBox = { inner ->
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFFB0B0B0),
                        fontSize = figmaTextSizeSp(13f),
                        fontFamily = NotoSansKR,
                        lineHeight = 13.sp * 1.75f,
                        letterSpacing = (-0.03).em
                    )
                }
                inner()
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp) //최소 높이
        )
    }
}

//활동 사진 캐러셀
@Composable
fun RepresentativeImagesCarousel(
    images: List<PromotionImage>, //안정 키(id) 포함된 이미지 리스트
    onPickAt: (Int) -> Unit, //슬롯 탭 시 호출(해당 인덱스에 추가,교체)
    onRemoveAt: (Int) -> Unit, //x 버튼 탭 시 삭제
    onReorder: (from: Int, to: Int) -> Unit, //드래그 정렬 콜백
    placeholderRes: Int, //비어있는 슬롯 표시 기본 이미지
    maxSlots: Int = 10 //최대 등록 가능 수
) {
    //항상 최신 리스트 길이를 보도록 유지
    val currentSize by rememberUpdatedState(images.size)

    val reorderState = rememberReorderableLazyListState(
        //드래그 중 아이템이 다른 위치로 넘어갈 때마다 자주 호출
        //즉시 onReorder(f,t) 호출하여 리스트를 실시간으로 변경
        onMove = { from, to ->
            val f = from.index
            val t = to.index.coerceIn(0, (currentSize - 1).coerceAtLeast(0))
            if (f != t && currentSize > 0) onReorder(f, t)
        },
        //placeholder 위로는 드롭 불가
        canDragOver = { _, to -> to.index < currentSize }
    )

    LazyRow(
        state = reorderState.listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 21.dp, top = 12.dp, bottom = 8.dp)
            .reorderable(reorderState),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //실제 이미지 아이템들
        itemsIndexed(
            items = images,
            key = { _, item -> item.id } //절대 변하지 않는 안정 키
        ) { index, item ->
            //ReorderavleItem으로 감싸야 드래깅 중 레이아웃, 애니메이션이 올바르게 동작
            ReorderableItem(reorderState, key = item.id) { isDragging ->
                val elev = if (isDragging) 10.dp else 0.dp //드래그 중 살짝 떠 보이는 효과
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 139f, heightPx = 183f)
                        .clip(RoundedCornerShape(25.dp))
                        .graphicsLayer { shadowElevation = elev.toPx() }
                        .detectReorderAfterLongPress(reorderState) //길게 눌러 드래그
                        .clickable { onPickAt(index) } //탭하면 교체용 피커 실행
                ) {
                    //실제 이미지 표시
                    AsyncImage(
                        model = item.displayUri,
                        contentDescription = "활동사진 $index",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    //삭제 버튼
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .figmaPadding(topPx = 10f, endPx = 9f)
                            .figmaSize(widthPx = 35f, heightPx = 35f)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onRemoveAt(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "삭제",
                            modifier = Modifier.figmaSize(widthPx = 35f, heightPx = 35f)
                        )
                    }
                }
            }
        }

        //빈 슬롯 (정렬 대상 아님)
        if (images.size < maxSlots) {
            item(key = "placeholder") {
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 139f, heightPx = 183f)
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color(0xFFEDEDED))
                        .clickable { onPickAt(images.size) }
                ) {
                    Image(
                        painter = painterResource(id = placeholderRes),
                        contentDescription = "빈 슬롯",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    //하단 개수 표시 (현재/최대)
    Row(
        modifier = Modifier.padding(start = 30.dp, top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${images.size} / $maxSlots",
            fontSize = figmaTextSizeSp(11f),
            fontFamily = NotoSansKR,
            color = Color(0xFF8A8A8A)
        )
    }
}