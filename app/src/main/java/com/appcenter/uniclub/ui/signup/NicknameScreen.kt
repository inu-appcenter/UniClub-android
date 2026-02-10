package com.appcenter.uniclub.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.components.InputLabel
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.components.UnderlineInputField
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.launch

@Composable
fun NicknameScreen(
    navController: NavHostController,
    onNext: () -> Unit,
    vm: SignUpViewModel
) {
    val ui by vm.ui.collectAsState()
    val canProceed = ui.nickname.trim().isNotEmpty()

    //연타 방지
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    Scaffold(
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TopBar( //상단바
                onBackClick = {
                    scope.launch {
                        navGuard.run nicknameBack@{
                            val route = navController.currentBackStackEntry
                                ?.destination
                                ?.route
                                .orEmpty()
                            if (!route.startsWith("nickname")) return@nicknameBack

                            navController.popBackStack()
                        }
                    }
                }
            )

            Column(modifier = Modifier.figmaPadding(startPx = 30f, topPx = 65f, bottomPx = 410f)) {
                Text( //상단 타이틀
                    text = "회원가입",
                    fontSize = figmaTextSizeSp(32f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 32.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(25.dp))

                InputLabel("닉네임을 입력해주세요.", isEnabled = true)
                UnderlineInputField(
                    modifier = Modifier.figmaPadding(endPx = 145f),
                    value = ui.nickname,
                    onValueChange = vm::onNickname,
                    enabled = true
                )
            }

            Image(
                painter = painterResource(
                    id = if (canProceed) R.drawable.btn_next_enabled else R.drawable.btn_next_disabled
                ),
                contentDescription = "하단 버튼",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .figmaPadding(bottomPx = 51f)
                    .clickable(
                        enabled = canProceed,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        //다음 버튼도 연타 방지
                        scope.launch {
                            navGuard.run nextOnce@{
                                val route = navController.currentBackStackEntry
                                    ?.destination
                                    ?.route
                                    .orEmpty()
                                if (!route.startsWith("nickname")) return@nextOnce

                                onNext()
                            }
                        }
                    }
            )
        }
    }
}