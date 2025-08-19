package com.appcenter.uniclub.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.components.DropdownField
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

//회원가입 화면
@Composable
fun SignUpScreen(
    onFinished: () -> Unit, //회원가입 완료 후 호출
    vm: SignUpViewModel
) {
    val ui by vm.ui.collectAsState()

    var selectedDept by remember { mutableStateOf("") }

    //버튼 활성화 조건 정의
    val canVerify = ui.canVerify //학번/비번 입력이 '인증 가능' 조건을 만족하는지
    val canProceed = ui.canProceed //이름/학과 등 추가 정보가 '다음 단계 가능'한지

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.figmaPadding(topPx = 79f, bottomPx = 113f)) {
            Text( //상단 타이틀
                text = "회원가입",
                fontSize = figmaTextSizeSp(32f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                modifier = Modifier.figmaPadding(startPx = 30f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image( //포털 계정 안내 이미지
                painter = painterResource(id = R.drawable.banner_school_account),
                contentDescription = "학교 포털 계정 안내",
                modifier = Modifier
                    .figmaPadding(startPx = 30f)
                    .figmaSize(widthPx = 184f, heightPx = 26f)
            )

            Spacer(modifier = Modifier.height(35.dp))

            //학번 입력 필드
            InputLabel("학번을 입력해주세요.", isEnabled = true)
            UnderlineInputField(
                value = ui.studentId, //vm 상태와 양방향 바인딩
                onValueChange = vm::onId, //vm 콜백 호출
                enabled = !ui.verified //인증 완료 후에는 수정 비활성화
            )

            Spacer(modifier = Modifier.height(20.dp))

            //비밀번호 입력 필드
            InputLabel("비밀번호를 입력해주세요.", isEnabled = true)
            UnderlineInputField(
                value = ui.password,
                onValueChange = vm::onPw,
                enabled = !ui.verified,
                isPassword = true //비밀번호 마스킹
            )

            Spacer(modifier = Modifier.height(25.dp))

            Box( //인증 실패 시 오류 메시지 출력
                modifier = Modifier
                    .figmaPadding(startPx = 10f)
                    .height(26.dp) //항상 고정된 공간 확보
            ) {
                if (ui.error != null) {
                    Image(
                        painter = painterResource(id = R.drawable.error_invalid_credentials),
                        contentDescription = "오류 메시지",
                        modifier = Modifier.figmaSize(widthPx = 193f, heightPx = 26f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            //이름 입력 필드 (인증 후에만 활성화)
            InputLabel("이름을 입력해주세요.", isEnabled = ui.verified)
            UnderlineInputField(
                value = ui.name,
                onValueChange = vm::onName,
                enabled = ui.verified
            )

            Spacer(modifier = Modifier.height(20.dp))

            //학과 입력 필드 (인증 후에만 활성화)
            //회의 후 드롭다운 수정
            InputLabel("학과를 선택해주세요.", isEnabled = ui.verified)
            DropdownField(
                items = listOf("컴퓨터공학부", "전자공학과", "경영학과"),
                selectedValue = selectedDept,
                onItemSelected = { selectedDept = it },
                modifier = Modifier
                    .figmaSize(widthPx = 220f, heightPx = 35f)
                    .figmaPadding(startPx = 30f)
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
                        vm.register(onDone = onFinished)
                    }
                }
        )
    }
}

//입력 라벨 컴포저블
//isEnabled 에 따라 색상만 회색/검정으로 구분
@Composable
fun InputLabel(text: String, isEnabled: Boolean) {
    Text(
        text = text,
        fontSize = figmaTextSizeSp(14f),
        fontFamily = NotoSansKR,
        lineHeight = 14.sp * 1.5f,
        letterSpacing = (-0.011).em,
        color = if (isEnabled) Color.Black else Color(0xFFBFBFBF), //비활성화 시 회색
        modifier = Modifier.figmaPadding(startPx = 30f, bottomPx = 10f)
    )
}

//밑줄 형태 입력 필드
@Composable
fun UnderlineInputField(
    value: String,
    onValueChange: (String) -> Unit, //텍스트 변경 콜백
    enabled: Boolean, //입력 가능 여부
    isPassword: Boolean = false
) {
    //밑줄 색상: 활성/비활성 구분
    val underlineColor = if (enabled) Color.Black else Color(0xFFBFBFBF) //비활성화 시 회색

    Column(modifier = Modifier
        .fillMaxWidth()
        .figmaPadding(startPx = 30f, endPx = 145f)
    ) {
        //실제 입력 영역
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = enabled,
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
                fontSize = 15.sp,
                fontFamily = NotoSansKR,
                lineHeight = 15.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = if (enabled) Color.Black else Color.Gray
            ),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            modifier = Modifier
                .fillMaxWidth()
                .figmaPadding(bottomPx = 4f)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(underlineColor)
        )
    }
}