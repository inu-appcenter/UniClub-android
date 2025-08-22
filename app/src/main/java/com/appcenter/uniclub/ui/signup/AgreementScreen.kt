package com.appcenter.uniclub.ui.signup

import com.appcenter.uniclub.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp
import java.nio.file.WatchEvent

@Composable
fun AgreementScreen(
    onFinished: () -> Unit,
    vm: SignUpViewModel
){
    val listState = rememberLazyListState()
    var isEndReached by remember { mutableStateOf(false) }

    // 스크롤이 마지막에 닿았는지 감지
    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val lastItem = visibleItems.last()
                // 마지막 아이템의 bottom 이 LazyColumn 높이보다 작거나 같으면 끝까지 내려온 것
                lastItem.offset + lastItem.size <= layoutInfo.viewportEndOffset
            } else {
                false
            }
        }.collect { reached ->
            isEndReached = reached
        }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .figmaPadding(topPx = 79f)
    ) {
        Image(
            painter = painterResource(R.drawable.main_logo),
            contentDescription = null,
            modifier = Modifier
                .figmaPadding(startPx = 31f)
                .figmaSize(widthPx = 101f, heightPx = 24f)
        )
        Text(
            text = "약관동의",
            fontSize = figmaTextSizeSp(32f),
            fontFamily = NotoSansKR,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color.Black,
            modifier = Modifier.figmaPadding(startPx = 31f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(
            state = listState,
            modifier = Modifier
            .figmaPadding(startPx = 23f, endPx = 23f)
            .figmaSize(widthPx = 315f, heightPx = 495f)
        ) {
            item {
                Text(
                    text = "### **회원가입 시 개인정보 수집 및 이용 동의**\n" +
                            "\n" +
                            "**개인정보 수집 및 이용 동의서**\n" +
                            "\n" +
                            "앱(이하 \"서비스\")는 고객님의 개인정보 보호를 중요하게 생각하며, 관련 법령을 준수하고 있습니다. 서비스 회원가입을 위해 아래와 같이 개인정보 수집 및 이용에 동의해 주시기 바랍니다.\n" +
                            "\n" +
                            "1. **수집하는 개인정보 항목**\n" +
                            "    - 필수항목: 이름, 연락처(휴대폰 번호), 이메일, 생년월일, 성별, 서비스 이용 기록\n" +
                            "    - 선택항목: 프로필 사진\n" +
                            "2. **개인정보의 수집 및 이용 목적**\n" +
                            "    - 회원관리: 서비스 이용을 위한 회원 인증 및 본인 확인\n" +
                            "    - 마케팅 및 광고: 서비스 이용 통계 분석 및 이벤트 정보 제공 (선택적 동의)\n" +
                            "3. **개인정보 보유 및 이용 기간**\n" +
                            "    - 서비스 탈퇴 시까지 보유하며, 탈퇴 후 즉시 삭제됩니다. 단, 관련 법령에 따라 일정 기간 보관이 필요할 경우, 해당 기간 동안 저장됩니다.\n" +
                            "4. **동의 거부 권리 및 동의 거부 시 불이익**\n" +
                            "    - 회원가입을 위한 필수항목에 대한 동의를 거부하실 수 있으나, 이 경우 회원가입 및 서비스 이용이 제한될 수 있습니다.\n" +
                            "\n" +
                            "**동의서 체크박스**\n" +
                            "\n" +
                            "- [ ]  개인정보 수집 및 이용에 동의합니다. (필수)\n" +
                            "- [ ]  마케팅 및 광고 활용에 동의합니다. (선택)",
                    fontSize = figmaTextSizeSp(15f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black
                )
            }

        }
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(
                if (isEndReached) R.drawable.btn_agree else R.drawable.btn_agree_disabled
            ),
            contentDescription = null,
            modifier = Modifier
                .figmaSize(widthPx = 210f, heightPx = 51f)
                .align(Alignment.CenterHorizontally)
                .clickable(enabled = isEndReached) {
                    vm.onAgree(true)
                    vm.register(onDone = { onFinished() })
                }
        )
    }
}