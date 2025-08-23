package com.appcenter.uniclub.ui.mypage

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.appcenter.uniclub.ui.components.DropdownField
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

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

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        profileImageUri = uri
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar( //상단바
            onBackClick = { navController.popBackStack() },
            title = "프로필 수정",
            rightIconResId = R.drawable.ic_save,
            //TODO: onRightIconClick =
        )
        Spacer(Modifier.height(33.dp))

        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .figmaSize(widthPx = 70f, heightPx = 69f)
        ){
            //프로필 이미지 (선택된 Uri가 있으면 표시, 없으면 기본 이미지)
            if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = "프로필 이미지",
                    modifier = Modifier.figmaSize(widthPx = 70f, heightPx = 69f)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_image),
                    contentDescription = "프로필 이미지",
                    modifier = Modifier.figmaSize(widthPx = 70f, heightPx = 69f)
                )
            }
            Image( //수정 버튼
                painter = painterResource(id = R.drawable.ic_profile_edit),
                contentDescription = "수정 버튼",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .figmaSize(widthPx = 20f, heightPx = 20f)
                    .clickable { //갤러리 실행
                        launcher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
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
            //학과 리스트 서버 연결 후 수정
            DropdownField(
                items = listOf("컴퓨터공학부", "전자공학과", "경영학과"),
                selectedValue = state.major,
                onItemSelected = { viewModel.updateMajor(it) },
                modifier = Modifier.figmaSize(widthPx = 200f, heightPx = 35f)
            )
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

        Text(
            text = "기본적으로 활동명은 실명으로 표시되며, \n" +
                    "닉네임을 설정하면 활동명이 닉네임으로 변경됩니다.",
            fontSize = figmaTextSizeSp(9f),
            fontFamily = NotoSansKR,
            lineHeight = 9.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = if (NicknameFocused) Color(0xFFFF5900) else Color.Transparent,
            modifier = Modifier.figmaPadding(startPx = 125f)
        )
    }
}