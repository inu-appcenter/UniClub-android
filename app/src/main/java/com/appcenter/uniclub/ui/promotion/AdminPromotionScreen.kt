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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

private enum class SnsPlatform { YOUTUBE, INSTAGRAM }

@Composable
fun AdminPromotionScreen(navController: NavHostController) {
    // --- 화면 상태 (서버 연동 전 임시) ---
    var bannerUri by remember { mutableStateOf<Uri?>(null) }
    var profileUri by remember { mutableStateOf<Uri?>(null) }
    var isRecruiting by remember { mutableStateOf(true) }
    var intro by remember { mutableStateOf("") }

    // --- Photo Picker 런처들 ---
    val bannerPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) bannerUri = uri }

    val profilePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) profileUri = uri }

    // SNS 링크 저장용 (서버 연결 전 임시)
    var youtubeLink by remember { mutableStateOf("") }
    var instagramLink by remember { mutableStateOf("") }

    // 다이얼로그 제어용
    var showLinkDialog by remember { mutableStateOf(false) }
    var editingSns by remember { mutableStateOf<SnsPlatform?>(null) }
    var tempLink by remember { mutableStateOf("") } // 다이얼로그 입력 임시 값

    fun normalizeUrl(raw: String): String {
        val t = raw.trim()
        return if (t.isNotEmpty() && !t.startsWith("http://") && !t.startsWith("https://")) {
            "https://$t"
        } else t
    }
    fun isValidUrl(raw: String): Boolean =
        Patterns.WEB_URL.matcher(normalizeUrl(raw)).matches()

    var clubRoom by remember { mutableStateOf("") }
    var leaderName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }

    var recruitPeriod by remember { mutableStateOf("") }   // 모집기간 입력값
    var noticeText   by remember { mutableStateOf("") }    // 공지 입력값

    var clubDescription by remember { mutableStateOf("") }

    // 활동 이미지들
    var activityImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val i = selectedSlotIndex
        if (uri != null && i != null) {
            activityImages = if (i < activityImages.size) {
                // 교체
                activityImages.toMutableList().also { it[i] = uri }
            } else if (activityImages.size < 10) {
                // 빈 슬롯(placeholder) → 추가 (끝에 붙임; i가 size여도 OK)
                activityImages + uri
            } else activityImages
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
                // 배너 (클릭해서 교체)
                EditableBanner(
                    bannerUri = bannerUri,
                    onClick = {
                        bannerPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                //상단바
                Box(
                    modifier = Modifier
                        .figmaPadding(startPx = 18f, topPx = 18f)
                        .size(20.dp)
                        .align(Alignment.TopStart)
                        .clickable { navController.popBackStack() }
                        .zIndex(1f),
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
                            editingSns = SnsPlatform.YOUTUBE
                            tempLink = youtubeLink  // 기존 값 있으면 프리필
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
                    phone = true,
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

            EditableClubDescription(
                text = clubDescription,
                onTextChange = { clubDescription = it },
                placeholder =  "동아리 소개글을 작성해보세요"
            )

            Spacer(modifier = Modifier.height(5.dp))

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
                    selectedSlotIndex = index
                    imagePicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemoveAt = { index ->
                    activityImages = activityImages.filterIndexed { i, _ -> i != index }
                },
                onReorder = { from, to ->
                    // ✅ 실제 리스트 재배치
                    activityImages = activityImages.toMutableList().apply {
                        add(to, removeAt(from))
                    }
                },
                placeholderRes = R.drawable.img_rep_placeholder // 네가 가진 기본 카드 이미지
            )

            Spacer(modifier = Modifier.height(30.dp))

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

        if (showLinkDialog) {
            LinkInputDialog(
                value = tempLink,
                onValueChange = { tempLink = it },
                title = "링크 추가",
                placeholder = "http:// or https://",
                onBack = { showLinkDialog = false }, // 저장 없이 닫기
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
        if (bannerUri != null) {
            AsyncImage(
                model = bannerUri,
                contentDescription = "Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
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
            .zIndex(1f)
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
        // 반투명 배경 + 라운드 카드
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
                // 상단 바: 뒤로, 제목, 완료
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    // 뒤로 아이콘 (검정/회색 아무 아이콘으로, 필요하면 리소스 교체)
                    Box(
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

                    Text(
                        text = title,
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        fontSize = figmaTextSizeSp(11f),
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    Text(
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

                // 라벨
                Text(
                    text = "URL",
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(10f),
                    fontWeight = FontWeight.Medium,
                    color = if (value.isNotBlank()) Color(0xFFFF5900) else Color(0xFFB0B0B0),
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                // 입력 박스 (내용 있으면 주황 테두리 + X 버튼)
                val borderColor = if (value.isNotBlank()) Color(0xFFFF5900) else Color.Transparent

                // URL 입력 박스 (스샷처럼 심플한 회색 배경)
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

                    // X 버튼 (내용 있을 때만 노출)
                    if (value.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .clickable { onValueChange("") },
                            contentAlignment = Alignment.Center
                        ) {
                            // 아이콘 리소스가 없으면 문자로
                            Text("✕", color = Color(0xFFFF5900), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

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
        // 라벨 (기존 스타일과 동일)
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

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            // 값 입력 (텍스트 스타일은 기존 value 텍스트와 동일)
            BasicTextField(
                value = value,
                onValueChange = { s ->
                    val v = if (phone) s.filter { it.isDigit() || it == '-' || it == '.' || it == ' ' } else s
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
            .graphicsLayer {
                shadowElevation = 10.dp.toPx() //높이 조절
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
        // 라벨: 사용자 UI와 동일하게 고정 폭
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = figmaTextSizeSp(12f),
            fontFamily = NotoSansKR,
            lineHeight = 12.sp * 1.5f,
            color = Color.Black,
            modifier = Modifier.width(70.dp)
        )

        // 오른쪽 content: 한 줄 입력
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
            // 여러 줄 입력
            singleLine = false,
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
                .heightIn(min = 120.dp) // 에디터 영역 최소 높이
        )
    }
}

@Composable
fun RepresentativeImagesCarousel(
    images: List<Uri>,
    onPickAt: (Int) -> Unit,                 // 탭 → 해당 위치에 추가/교체
    onRemoveAt: (Int) -> Unit,               // X 버튼 → 삭제
    onReorder: (from: Int, to: Int) -> Unit, // 드래그 정렬 결과
    placeholderRes: Int = R.drawable.img_rep_placeholder,
    maxSlots: Int = 10
) {
    val listState = rememberLazyListState()
    val totalSlots = minOf(maxSlots, images.size + 1)

    val reorderState = rememberReorderableLazyListState(
        listState = listState,
        onMove = { from, to ->
            val fromIdx = from.index
            val rawTo   = to.index
            if (fromIdx < images.size) {
                // placeholder(마지막 슬롯)로 드롭하면 끝으로 취급
                val target = rawTo.coerceIn(0, (images.size - 1).coerceAtLeast(0))
                if (fromIdx != target) onReorder(fromIdx, target)
            }
        }
    )

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 21.dp, top = 12.dp, bottom = 8.dp)
            .reorderable(reorderState),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(
            count = totalSlots,
            key = { index ->
                if (index < images.size) images[index].toString() else "placeholder-$index"
            }
        ) { index ->
            val hasImage = index < images.size
            val itemKey = if (hasImage) images[index].toString() else "placeholder-$index"

            ReorderableItem(reorderState, key = itemKey) { isDragging ->
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 139f, heightPx = 183f)
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color(0xFFEDEDED))
                        // ✅ 이미지만 길게 눌러 드래그 시작 (placeholder는 드래그 불가)
                        .then(if (hasImage) Modifier.detectReorderAfterLongPress(reorderState) else Modifier)
                        .clickable { onPickAt(index) }
                ) {
                    if (hasImage) {
                        AsyncImage(
                            model = images[index],
                            contentDescription = "대표이미지 $index",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // 삭제 버튼
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xAA000000))
                                .clickable { onRemoveAt(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✕", color = Color.White, fontSize = 12.sp)
                        }
                    } else {
                        Image(
                            painter = painterResource(id = placeholderRes),
                            contentDescription = "빈 슬롯 $index",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    Row(
        modifier = Modifier.padding(start = 30.dp, top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${images.size} / $maxSlots", fontSize = figmaTextSizeSp(11f), fontFamily = NotoSansKR, color = Color(0xFF8A8A8A))
    }
}

@Preview(showBackground = true)
@Composable
fun AdminPromotionScreenPreview() {
    val nav = rememberNavController()
    AdminPromotionScreen(navController = nav)
}