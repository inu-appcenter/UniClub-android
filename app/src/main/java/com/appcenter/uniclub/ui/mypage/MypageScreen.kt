package com.appcenter.uniclub.ui.mypage

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.appcenter.uniclub.App
import com.appcenter.uniclub.ui.components.Dialog
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

//마이페이지 화면
@Composable
fun MypageScreen(navController: NavHostController, rootNavController: NavHostController) {
    val context = LocalContext.current.applicationContext as App
    val viewModel: MyPageViewModel = viewModel(
        factory = MyPageViewModelFactory(context)
    )
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyPage()
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .figmaPadding(topPx = 15f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "마이페이지",
            fontSize = figmaTextSizeSp(15f),
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Bold,
            lineHeight = 15.sp * 1.5f, // 행간
            letterSpacing = (-0.011).em, // 자간
            color = Color.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

    Column(
        modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .figmaPadding(startPx = 27f, endPx = 27f, topPx = 70f))
    {
        Row(verticalAlignment = Alignment.CenterVertically){
            if (!state.profileImageLink.isNullOrBlank()) {
                // 서버에서 저장된 프로필 이미지가 있으면 표시
                Image(
                    painter = rememberAsyncImagePainter(state.profileImageLink),
                    contentDescription = "프로필 이미지",
                    modifier = Modifier
                        .figmaSize(widthPx = 70f, heightPx = 70f)
                        .clip(RoundedCornerShape(23.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 없으면 기본 이미지
                Image(
                    painter = painterResource(R.drawable.default_image),
                    contentDescription = "기본 프로필 이미지",
                    modifier = Modifier.figmaSize(widthPx = 70f, heightPx = 75f)
                )
            }
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)){
                Text( //닉네임
                    text = state.nickname ?: "닉네임을 설정해보세요!",
                    fontSize = figmaTextSizeSp(10f),
                    fontFamily = NotoSansKR,
                    lineHeight = 10.sp * 1.5f, // 행간
                    letterSpacing = (-0.011).em, // 자간
                    color = if (state.nickname == null) Color(0xFFD3D3D3) else Color(0xFFFF5900)
                )
                Text( //이름
                    text = state.name,
                    fontSize = figmaTextSizeSp(16f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp * 1.5f, // 행간
                    letterSpacing = (-0.011).em, // 자간
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                Text( //학과
                    text = state.major,
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 11.sp * 1.5f, // 행간
                    letterSpacing = (-0.011).em, // 자간
                    color = Color(0xFF818181)
                )
                Spacer(Modifier.height(3.dp))
                Text( //학번
                    text = state.studentId,
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    lineHeight = 11.sp * 1.5f, // 행간
                    letterSpacing = (-0.011).em, // 자간
                    color = Color(0xFF818181)
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(20.dp))

        //계정 섹션
        Section(title = "계정")
        Spacer(modifier = Modifier.height(10.dp))

        MenuItem(
            text = "알림 설정",
            onClick = { navController.navigate("alarmSetting") }
        )
        MenuItem(
            text = "프로필 수정",
            onClick = { navController.navigate("profileEdit") }
        )
        MenuItem(
            text = "로그아웃",
            onClick = { showLogoutDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(20.dp))

        //이용안내 섹션
        Section(title = "이용안내")
        Spacer(modifier = Modifier.height(17.dp))

        MenuItem(
            text = "문의하기",
            onClick = { navController.navigate("inquiry") }
        )
        MenuItem(
            text = "이용약관",
            onClick = { navController.navigate("terms") }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(20.dp))

        //기타 섹션
        Section(title = "기타")
        Spacer(modifier = Modifier.height(17.dp))

        MenuItem(
            text = "계정 삭제",
            onClick = { navController.navigate("delete") }
        )

        Spacer(Modifier.height(70.dp))
    }

    //로그아웃 다이얼로그
    if (showLogoutDialog) {
        Dialog(
            title = "로그아웃하시겠습니까?",
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                rootNavController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun Section(title: String) {
    Text(
        text = title,
        fontSize = figmaTextSizeSp(17f),
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Bold,
        lineHeight = 17.sp * 1.5f, // 행간
        letterSpacing = (-0.011).em, // 자간
        color = Color.Black
    )
}

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp), //터치 영역 확보
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = figmaTextSizeSp(14f),
            fontFamily = NotoSansKR,
            lineHeight = 14.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color.Black
        )
    }
}