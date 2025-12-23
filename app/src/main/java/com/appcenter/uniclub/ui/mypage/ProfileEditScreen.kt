package com.appcenter.uniclub.ui.mypage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.appcenter.uniclub.App
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.Major
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.appcenter.uniclub.ui.components.MajorSelectBottomSheet
import com.appcenter.uniclub.ui.components.MajorSelectButton

//프로필 수정 화면
@Composable
fun ProfileEditScreen(navController: NavHostController) {
    val context = LocalContext.current.applicationContext as App
    val viewModel: ProfileEditViewModel = viewModel(
        factory = ProfileEditViewModelFactory(context)
    )
    val state by viewModel.uiState.collectAsState()
    var NameFocused by remember { mutableStateOf(false) }
    var NicknameFocused by remember { mutableStateOf(false) }

    //선택된 프로필 이미지 URI
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    var showAction by remember { mutableStateOf(false) }

    //화면 진입 시 기존 프로필 불러오기
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    //저장 성공 시 자동 뒤로가기
    LaunchedEffect(state.success) {
        if (state.success) {
            navController.popBackStack()
        }
    }

    //갤러리 실행 → 선택 시 updateProfileImage 호출
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        profileImageUri = uri
        if (uri != null) {
            viewModel.updateProfileImage(uri, context)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        TopBar( //상단바
            onBackClick = { navController.popBackStack() },
            title = "프로필 수정",
            rightIconResId = if (viewModel.isModified) R.drawable.ic_save else null,
            onRightIconClick = {
                if (viewModel.isModified && !state.profileUrl.isNullOrBlank()) {
                    viewModel.updateProfile()
                }
            }
        )
        Spacer(Modifier.height(33.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .figmaSize(widthPx = 70f, heightPx = 75f)
        ){
            //상태별로 프로필 이미지 출력
            when {
                profileImageUri != null -> {
                    //새로 선택한 이미지
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "프로필 이미지",
                        modifier = Modifier
                            .figmaSize(widthPx = 70f, heightPx = 69f)
                            .clip(RoundedCornerShape(23.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                state.profileUrl == "__deleted__" -> {
                    //삭제했을 때 -> 기본 이미지
                    Image(
                        painter = painterResource(id = R.drawable.default_image),
                        contentDescription = "기본 프로필 이미지",
                        modifier = Modifier
                            .figmaSize(widthPx = 70f, heightPx = 75f)
                            .clip(RoundedCornerShape(23.dp))
                    )
                }
                !state.profileUrl.isNullOrBlank() -> {
                    //서버에서 가져온 기존 프로필 이미지
                    Image(
                        painter = rememberAsyncImagePainter(state.profileUrl),
                        contentDescription = "프로필 이미지",
                        modifier = Modifier
                            .figmaSize(widthPx = 70f, heightPx = 69f)
                            .clip(RoundedCornerShape(23.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    //둘다 없을 때 기본 이미지
                    Image(
                        painter = painterResource(id = R.drawable.default_image),
                        contentDescription = "기본 프로필 이미지",
                        modifier = Modifier
                            .figmaSize(widthPx = 70f, heightPx = 75f)
                            .clip(RoundedCornerShape(23.dp))
                    )
                }
            }
            Image( //수정 버튼
                painter = painterResource(id = R.drawable.ic_profile_edit),
                contentDescription = "수정 버튼",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .figmaSize(widthPx = 20f, heightPx = 20f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { //클릭하면 오버레이 버튼
                        showAction = true
                    }
            )
        }

        Spacer(Modifier.height(55.dp))

        //학과 선택 영역
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.figmaPadding(startPx = 39f, bottomPx = 15f)
        ){
            Text(
                text = "학과",
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                modifier = Modifier.width(85.dp)
            )

            var majorFocused by remember { mutableStateOf(false) }
            var showMajorSheet by remember { mutableStateOf(false) } //바텀시트 오픈 상태
            val selectedDisplayName = Major.values() //현재 선택된 학과 displayName
                .find { it.name == state.major }
                ?.displayName
                ?: ""

            MajorSelectButton( //학과 선택 버튼
                selectedMajor = selectedDisplayName,
                onClick = { //클릭하면 바텀시트 열림
                    majorFocused = true
                    showMajorSheet = true
                },
                modifier = Modifier.figmaSize(widthPx = 200f, heightPx = 35f),
                isFocused = majorFocused
            )

            if (showMajorSheet) { //바텀시트
                MajorSelectBottomSheet(
                    onDismiss = {
                        showMajorSheet = false
                        majorFocused = false
                    },
                    onSelect = { major ->
                        viewModel.updateMajor(major.name)
                        showMajorSheet = false
                    }
                )
            }
        }

        //이름 입력 영역
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.figmaPadding(startPx = 39f, bottomPx = 15f)
        ){
            Text(
                text = "이름",
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                modifier = Modifier.width(85.dp)
            )
            Box( //입력 박스
                modifier = Modifier
                    .figmaSize(widthPx = 200f, heightPx = 35f)
                    .border(
                        width = 1.dp,
                        color = if (NameFocused) Color(0xFFFF5900) else Color.Transparent,
                        shape = RoundedCornerShape(13.dp)
                    )
                    .background(Color(0xFFF3F3F3), RoundedCornerShape(13.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = state.name,
                    onValueChange = { viewModel.updateName(it) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = figmaTextSizeSp(12f),
                        fontFamily = NotoSansKR,
                        lineHeight = 12.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            NameFocused = focusState.isFocused
                        }
                )
            }
        }

        //닉네임 입력 영역
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.figmaPadding(startPx = 39f, bottomPx = 6f)
        ){
            Text(
                text = "닉네임",
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                modifier = Modifier.width(85.dp)
            )
            Box( //입력 박스
                modifier = Modifier
                    .figmaSize(widthPx = 200f, heightPx = 35f)
                    .border(
                        width = 1.dp,
                        color = if (NicknameFocused) Color(0xFFFF5900) else Color.Transparent,
                        shape = RoundedCornerShape(13.dp)
                    )
                    .background(Color(0xFFF3F3F3), RoundedCornerShape(13.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = state.nickname,
                    onValueChange = { viewModel.updateNickname(it) },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = figmaTextSizeSp(12f),
                        fontFamily = NotoSansKR,
                        lineHeight = 12.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            NicknameFocused = focusState.isFocused
                        }
                )
            }
        }
    }

    //프로필 수정, 삭제 오버레이
    if(showAction) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .windowInsetsPadding(WindowInsets.navigationBars)
                .imePadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showAction = false },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 344f, heightPx = 116f)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.btn_profile_edit_delete),
                        contentDescription = "프로필 수정&삭제",
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
                                ) { //수정 동작
                                    showAction = false
                                    //갤러리 실행
                                    launcher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                        )
                        Box( //삭제 영역
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { //삭제 동작
                                    showAction = false
                                    viewModel.updateProfileImageDeleted()
                                }
                        )
                    }
                }
            }
        }
    }
}