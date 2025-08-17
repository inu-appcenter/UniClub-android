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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaTextSizeSp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import org.burnoutcrew.reorderable.ReorderableItem
import androidx.compose.ui.window.Dialog
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.util.UUID

//관리자용 홍보 페이지

//SNS 플랫폼 구분
private enum class SnsPlatform { YOUTUBE, INSTAGRAM }

//활동 사진 캐러셀의 각 아이템 모델
data class ImageSlot(
    val id: String = UUID.randomUUID().toString(), //절대 변하지 않는 식별자
    val uri: Uri //실제 이미지 리소스를 가리키는 Uri
)
//리스트 내 아이템 순서 변경 헬퍼
//from 위치의 요소를 제거하고 to 위치에 그대로 삽입
private fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to) return
    add(to, removeAt(from))
}

@Composable
fun AdminPromotionScreen(navController: NavHostController) {
    //서버 연동 전 임시 화면 상태
    var bannerUri by remember { mutableStateOf<Uri?>(null) } //상단 배너
    var profileUri by remember { mutableStateOf<Uri?>(null) } //프로필
    var isRecruiting by remember { mutableStateOf(true) } //모집중 토글
    var intro by remember { mutableStateOf("") } //한줄 소개

    //포토 피커 런처들
    val bannerPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) bannerUri = uri }

    val profilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) profileUri = uri }

    //SNS 링크 저장용
    var youtubeLink by remember { mutableStateOf("") }
    var instagramLink by remember { mutableStateOf("") }
    var showLinkDialog by remember { mutableStateOf(false) } //다이얼로그 표시 여부
    var editingSns by remember { mutableStateOf<SnsPlatform?>(null) } //어떤 SNS를 편집중인지
    var tempLink by remember { mutableStateOf("") } //다이얼로그 입력 임시값
    //http/https 빠진 경우 자동으로 https:// 붙여줌
    fun normalizeUrl(raw: String): String {
        val t = raw.trim()
        return if (t.isNotEmpty() && !t.startsWith("http://") && !t.startsWith("https://")) {
            "https://$t"
        } else t
    }
    fun isValidUrl(raw: String): Boolean =
        Patterns.WEB_URL.matcher(normalizeUrl(raw)).matches()

    var clubRoom by remember { mutableStateOf("") } //동아리방
    var leaderName by remember { mutableStateOf("") } //회장명
    var contact by remember { mutableStateOf("") } //연락처

    var recruitPeriod by remember { mutableStateOf("") } //모집기간
    var noticeText   by remember { mutableStateOf("") } //공지

    var clubDescription by remember { mutableStateOf("") } //동아리 소개

    //활동 이미지 리스트 상태
    var activityImages by remember { mutableStateOf<List<ImageSlot>>(emptyList()) }
    //어떤 슬롯을 탭했는지 기억, 포토 피커 콜백에서 교체/추가에 사용
    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }
    //활동 이미지 추가,교체용 포토 피커 런처
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val i = selectedSlotIndex
        //사용자가 이미지를 고르고, 어느 슬롯을 눌렀는지 정보가 있을 때만 처리
        if (uri != null && i != null) {
            activityImages = activityImages.toMutableList().apply {
                if (i < size) { //i가 기존 슬롯 범위 안이면 교체
                    this[i] = this[i].copy(uri = uri)
                } else if (size < 10) { //i가 placeholder이고 현재 개수가 10 미만이면 추가
                    add(ImageSlot(uri = uri))
                }
            }
        }
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
                    bannerUri = bannerUri,
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
                        .clickable { navController.popBackStack() }
                        .zIndex(1f), //배너 위로
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_back_white),
                        contentDescription = "Back",
                        modifier = Modifier
                            .figmaSize(widthPx = 11f, heightPx = 20f)
                    )
                }

                //프로필 이미지
                EditableProfile(
                    profileUri = profileUri,
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
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            editingSns = SnsPlatform.YOUTUBE //어떤 SNS 편집하는지 저장
                            tempLink = youtubeLink //기존 값 프리필
                            showLinkDialog = true
                        }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            editingSns = SnsPlatform.INSTAGRAM
                            tempLink = instagramLink
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
                        text = "크레퍼스(CREPERS)",
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
                        id = if (isRecruiting) R.drawable.ic_recruiting else R.drawable.ic_upcoming
                    ),
                    contentDescription = if (isRecruiting) "모집중" else "모집예정",
                    modifier = Modifier
                        .offset(x = (-18).dp)
                        .clickable { isRecruiting = !isRecruiting }
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
                    value = clubRoom,
                    onValueChange = { clubRoom = it },
                    placeholder = "예) 4호관 107호",
                    modifier = Modifier.width(90.dp)
                )
                EditableInfoItem(
                    label = "회장",
                    value = leaderName,
                    onValueChange = { leaderName = it },
                    placeholder = "예) 홍길동",
                    modifier = Modifier.width(55.dp)
                )
                EditableInfoItem(
                    label = "연락처",
                    value = contact,
                    onValueChange = { contact = it },
                    placeholder = "010-1234-5678",
                    phone = true, //숫자,하이픈,공백만 허용 필터링
                    modifier = Modifier.width(100.dp)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            //한줄 소개
            EditableTextShadowBanner(
                text = intro,
                onTextChange = { intro = it },
                placeholder = "동아리를 한 줄로 표현해보세요"
            )

            Spacer(modifier = Modifier.height(35.dp))

            //모집 안내, 공지
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                //서버 연결 후 날짜 형식 수정 필요해 보임
                EditableAnnouncementItem(
                    label = "모집기간",
                    value = recruitPeriod,
                    onValueChange = { recruitPeriod = it },
                    placeholder = "예) 7월 16일 ~ 24일 오후 6시"
                )
                EditableAnnouncementItem(
                    label = "공지",
                    value = noticeText,
                    onValueChange = { noticeText = it },
                    placeholder = "예) 25일 동아리실 출입금지"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(15.dp))

            //동아리 설명
            EditableClubDescription(
                text = clubDescription,
                onTextChange = { clubDescription = it },
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
                images = activityImages,
                onPickAt = { index ->
                    //슬롯을 탭하면 해당 인덱스 저장, 이미지 피커 실행
                    selectedSlotIndex = index
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveAt = { index ->
                    //삭제: 해당 인덱스를 제외한 새 리스트 생성
                    activityImages = activityImages.filterIndexed { i, _ -> i != index }
                },
                onReorder = { from, to ->
                    //드래그 중 위치 교차될 때마다 즉시 리스트 갱신(실시간 재배치)
                    activityImages = activityImages.toMutableList().apply { move(from, to) }
                },
                placeholderRes = R.drawable.img_rep_placeholder
            )

            Spacer(modifier = Modifier.height(30.dp))

            //저장 버튼 (서버 연결 후 구현 예정)
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                ImageButtonItem(
                    imageRes = R.drawable.btn_save,
                    contentDescription = "저장하기",
                    onClick = { /* TODO: 저장하기 기능 */ }
                )
            }

            Spacer(modifier = Modifier.height(75.dp))
        }

        //SNS 링크 입력 다이얼로그
        if (showLinkDialog) {
            LinkInputDialog(
                value = tempLink,
                onValueChange = { tempLink = it },
                title = "링크 추가",
                placeholder = "http:// or https://",
                onBack = { showLinkDialog = false },
                onDone = {
                    val final = normalizeUrl(tempLink)
                    when (editingSns) {
                        SnsPlatform.YOUTUBE -> youtubeLink = final
                        SnsPlatform.INSTAGRAM -> instagramLink = final
                        else -> {}
                    }
                    showLinkDialog = false
                },
                doneEnabled = isValidUrl(tempLink)
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
                            .clickable { onBack() },
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
                            .clickable(enabled = doneEnabled) { onDone() }
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

//모집기간 + 공지
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
            textStyle = androidx.compose.ui.text.TextStyle(
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
    images: List<ImageSlot>, //안정 키(id) 포함된 이미지 리스트
    onPickAt: (Int) -> Unit, //슬롯 탭 시 호출(해당 인덱스에 추가,교체)
    onRemoveAt: (Int) -> Unit, //x 버튼 탭 시 삭제
    onReorder: (from: Int, to: Int) -> Unit, //드래그 정렬 콜백
    placeholderRes: Int = R.drawable.img_rep_placeholder, //비어있는 슬롯 표시 기본 이미지
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
                        model = item.uri,
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
                            .clickable { onRemoveAt(index) },
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

@Preview(showBackground = true)
@Composable
fun AdminPromotionScreenPreview() {
    val nav = rememberNavController()
    AdminPromotionScreen(navController = nav)
}