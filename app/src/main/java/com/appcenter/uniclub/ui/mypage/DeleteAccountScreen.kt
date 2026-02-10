package com.appcenter.uniclub.ui.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appcenter.uniclub.App
import com.appcenter.uniclub.R
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.ui.components.InputLabel
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.components.UnderlineInputField
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.launch

//계정 삭제 화면
@Composable
fun DeleteAccountScreen(
    navController: NavHostController
) {
    val app = LocalContext.current.applicationContext as App
    val repo = ServiceLocator.userRepository(app)

    val vm: DeleteAccountViewModel = viewModel(
        factory = DeleteAccountViewModelFactory(repo)
    )

    var password by remember { mutableStateOf("") } //사용자가 입력한 비밀번호 상태
    var isFieldEnabled by remember { mutableStateOf(false) } //입력 필드 활성화 여부

    val uiState by vm.uiState.collectAsState()

    //뒤로가기 연타/잔상 클릭 방지용
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopBar( //상단바
                onBackClick = {
                    scope.launch {
                        navGuard.run navGuardRun@{
                            val route =
                                navController.currentBackStackEntry?.destination?.route.orEmpty()
                            if (route != "delete") return@navGuardRun

                            navController.popBackStack()
                        }
                    }
                },
                title = "계정 삭제"
            )

            Spacer(Modifier.height(43.dp))

            Column(modifier = Modifier.figmaPadding(startPx = 36f)) {
                Text(
                    text = "계정을 삭제하시겠습니까?",
                    fontSize = figmaTextSizeSp(14f),
                    fontWeight = FontWeight.Bold,
                    fontFamily = NotoSansKR,
                    lineHeight = 14.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black
                )

                Spacer(Modifier.height(10.dp))

                Text( //설명 텍스트
                    text = "계정 삭제 시 활동 내역이 영구 삭제되며 복구가 불가능합니다.\n정말 삭제하시겠습니까?",
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    lineHeight = 11.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color(0xFF7D7D7D)
                )

                Spacer(Modifier.height(44.dp))

                //입력 필드
                InputLabel("비밀번호를 입력해주세요.", isEnabled = isFieldEnabled)
                UnderlineInputField(
                    modifier = Modifier
                        .figmaPadding(endPx = 132f)
                        .clickable { isFieldEnabled = true },
                    value = password,
                    onValueChange = { password = it },
                    enabled = isFieldEnabled
                )

                Spacer(Modifier.height(50.dp))

                Image(
                    painter = painterResource(
                        id = if (password.isNotBlank()) R.drawable.btn_delete_account
                        else R.drawable.btn_delete_account_disabled
                    ),
                    contentDescription = "계정 삭제 버튼",
                    modifier = Modifier
                        .figmaSize(widthPx = 157f, heightPx = 30f)
                        .clickable(enabled = password.isNotBlank()) {
                            vm.deleteAccount(password)
                        }
                )
            }
        }
    }
}