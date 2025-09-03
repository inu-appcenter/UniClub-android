package com.appcenter.uniclub.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.Major
import com.appcenter.uniclub.ui.components.DropdownField
import com.appcenter.uniclub.ui.components.InputLabel
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.components.UnderlineInputField
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp

//회원가입 화면
@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    onNext: () -> Unit, //회원가입 완료 후 호출
    vm: SignUpViewModel
) {
    val ui by vm.ui.collectAsState()

    //버튼 활성화 조건 정의
    val canVerify = ui.canVerify //학번/비번 입력이 '인증 가능' 조건을 만족하는지
    val canProceed = ui.canProceed //이름/학과 등 추가 정보가 '다음 단계 가능'한지

    Box(modifier = Modifier
            .fillMaxSize()
            .figmaPadding(topPx = 18f)
    ) {
        TopBar( //상단바
            onBackClick = onBack
        )

        Column(modifier = Modifier.figmaPadding(startPx = 30f, topPx = 79f, bottomPx = 113f)) {
            Text( //상단 타이틀
                text = "회원가입",
                fontSize = figmaTextSizeSp(32f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image( //포털 계정 안내 이미지
                painter = painterResource(id = R.drawable.banner_school_account),
                contentDescription = "학교 포털 계정 안내",
                modifier = Modifier
                    .figmaSize(widthPx = 184f, heightPx = 26f)
            )

            Spacer(modifier = Modifier.height(35.dp))

            //학번 입력 필드
            InputLabel("학번을 입력해주세요.", isEnabled = true)
            UnderlineInputField(
                modifier = Modifier.figmaPadding(endPx = 145f),
                value = ui.studentId, //vm 상태와 양방향 바인딩
                onValueChange = vm::onId, //vm 콜백 호출
                enabled = !ui.verified //인증 완료 후에는 수정 비활성화
            )

            Spacer(modifier = Modifier.height(20.dp))

            //비밀번호 입력 필드
            InputLabel("비밀번호를 입력해주세요.", isEnabled = true)
            UnderlineInputField(
                modifier = Modifier.figmaPadding(endPx = 145f),
                value = ui.password,
                onValueChange = vm::onPw,
                enabled = !ui.verified,
                isPassword = true //비밀번호 마스킹
            )

            Spacer(modifier = Modifier.height(25.dp))

            Box( //인증 실패 시 오류 메시지 출력
                modifier = Modifier.height(26.dp) //항상 고정된 공간 확보
            ) {
                if (ui.error != null) {
                    Image(
                        painter = painterResource(id = R.drawable.error_invalid_credentials),
                        contentDescription = "오류 메시지",
                        modifier = Modifier
                            .figmaSize(widthPx = 197f, heightPx = 30f)
                            .wrapContentWidth(Alignment.Start)
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            //이름 입력 필드 (인증 후에만 활성화)
            InputLabel("이름을 입력해주세요.", isEnabled = ui.verified)
            UnderlineInputField(
                modifier = Modifier.figmaPadding(endPx = 145f),
                value = ui.name,
                onValueChange = vm::onName,
                enabled = ui.verified
            )

            Spacer(modifier = Modifier.height(20.dp))

            //학과 입력 필드 (인증 후에만 활성화)
            //회의 후 드롭다운 수정
            InputLabel("학과를 선택해주세요.", isEnabled = ui.verified)
            val majorItems = Major.values().map { it.displayName }
            val selectedDisplayName = Major.values()
                .find { it.name == ui.major }   // ui.major == "COMPUTER_ENGINEERING"
                ?.displayName                   // "컴퓨터공학부"
                ?: ""   // 선택 안했을 때는 빈값
            DropdownField(
                items = majorItems,
                selectedValue = selectedDisplayName,
                onItemSelected = { displayName ->
                    vm.onMajor(displayName) // ← displayName을 enum.name으로 바꿔서 상태에 저장
                },
                modifier = Modifier.figmaSize(widthPx = 210f, heightPx = 35f)
            )
        }

        //하단 버튼
        //인증 전: '재학생 확인' (canVerify 충족 시 enabled 이미지)
        //인증 후: '다음' (canProceed 충족 시 enabled 이미지)
        Image(
            painter = painterResource(
                id = if (!ui.verified)
                    if (canVerify) R.drawable.btn_verify_enabled else R.drawable.btn_verify_disabled
                else
                    if (canProceed) R.drawable.btn_next_enabled else R.drawable.btn_next_disabled
            ),
            contentDescription = "하단 버튼",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .figmaPadding(bottomPx = 51f)
                .clickable(enabled = if (!ui.verified) canVerify else canProceed) {
                    if (!ui.verified) {
                        vm.verify()
                    } else {
                        //다음 화면으로 이동
                        onNext()
                    }
                }
        )
    }
}