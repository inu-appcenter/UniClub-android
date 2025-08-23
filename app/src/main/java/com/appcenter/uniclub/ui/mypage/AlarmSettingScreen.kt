package com.appcenter.uniclub.ui.mypage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaTextSizeSp
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appcenter.uniclub.data.UserRepository

//알림 설정 화면
@Composable
fun AlarmSettingScreen(
    navController: NavHostController,
    repository: UserRepository
) {
    val viewModel: AlarmSettingViewModel = viewModel(
        factory = AlarmSettingViewModelFactory(repository)
    )
    val checked by viewModel.checked.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar( //상단바
            onBackClick = { navController.popBackStack() },
            title = "알림 설정"
        )
        Spacer(Modifier.height(43.dp))

        //설명 텍스트
        Text(
            text = "질의응답, 관심 동아리, 총동아리연합회 소식 등 동아리의 다양한 소식을 알려드릴게요.",
            fontSize = figmaTextSizeSp(11f),
            fontFamily = NotoSansKR,
            lineHeight = 11.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color(0xFF7D7D7D),
            modifier = Modifier.padding(horizontal = 36.dp)
        )

        Spacer(Modifier.height(30.dp))

        //앱 푸시 알림 스위치
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
        ) {
            Text(
                text = "앱 푸시 알림",
                fontSize = figmaTextSizeSp(14f),
                fontFamily = NotoSansKR,
                lineHeight = 11.sp * 1.5f, // 행간
                letterSpacing = (-0.011).em, // 자간
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = { viewModel.onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFFF5900) //활성 시
                )
            )
        }
    }
}