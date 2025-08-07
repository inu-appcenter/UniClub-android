package com.appcenter.uniclub.ui.signup

import android.R.attr.top
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

@Composable
fun SignUpScreen() {
    var studentId by remember { mutableStateOf("") } //학번 입력값
    var password by remember { mutableStateOf("") } //비밀번호 입력값
    var isVerified by remember { mutableStateOf(false) } //학번,비번 인증 여부
    var showError by remember { mutableStateOf(false) } //인증 실패 시 오류 표시

    var name by remember { mutableStateOf("") } //이름 입력값
    var department by remember { mutableStateOf("") } //학과 입력값

    //버튼 활성화 조건 정의
    val canVerify = studentId.isNotBlank() && password.isNotBlank() //학번,비번 모두 입력 시 인증 버튼 활성화
    val canProceed = isVerified && name.isNotBlank() && department.isNotBlank() //인증 완료 후 이름,학과 모두 입력 시 다음 버튼 활성화

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
            UnderlineInputField(value = studentId, onValueChange = { studentId = it }, enabled = !isVerified)

            Spacer(modifier = Modifier.height(20.dp))

            //비밀번호 입력 필드
            InputLabel("비밀번호를 입력해주세요.", isEnabled = true)
            UnderlineInputField(value = password, onValueChange = { password = it }, enabled = !isVerified, isPassword = true)

            Spacer(modifier = Modifier.height(25.dp))

            Box( //인증 실패 시 오류 메시지 출력
                modifier = Modifier
                    .figmaPadding(startPx = 30f)
                    .height(26.dp) //항상 고정된 공간 확보
            ) {
                if (showError) {
                    Image(
                        painter = painterResource(id = R.drawable.error_invalid_credentials),
                        contentDescription = "오류 메시지",
                        modifier = Modifier.figmaSize(widthPx = 193f, heightPx = 26f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            //이름 입력 필드 (인증 후에만 활성화)
            InputLabel("이름을 입력해주세요.", isEnabled = isVerified)
            UnderlineInputField(value = name, onValueChange = { name = it }, enabled = isVerified)

            Spacer(modifier = Modifier.height(20.dp))

            //학과 입력 필드 (인증 후에만 활성화)
            InputLabel("학과를 선택해주세요.", isEnabled = isVerified)
            UnderlineInputField(value = department, onValueChange = { department = it }, enabled = isVerified) //회의 후 드롭다운으로 변경
        }

        Image( //하단 버튼
            painter = painterResource(
                id = if (!isVerified)
                    if (canVerify) R.drawable.btn_verify_enabled else R.drawable.btn_verify_disabled
                else
                    if (canProceed) R.drawable.btn_next_enabled else R.drawable.btn_next_disabled
            ),
            contentDescription = "하단 버튼",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .figmaPadding(bottomPx = 51f)
                .clickable(enabled = if (!isVerified) canVerify else canProceed) {
                    if (!isVerified) {
                        //인증 로직 처리
                        if (isValidStudentAccount(studentId, password)) {
                            isVerified = true
                            showError = false
                        } else {
                            showError = true
                        }
                    } else {
                        //다음 화면으로 이동
                    }
                }
        )
    }
}

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

@Composable
fun UnderlineInputField(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    isPassword: Boolean = false
) {
    val underlineColor = if (enabled) Color.Black else Color(0xFFBFBFBF) //비활성화 시 회색

    Column(modifier = Modifier
        .fillMaxWidth()
        .figmaPadding(startPx = 30f, endPx = 145f)
    ) {
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

//더미 인증 함수
fun isValidStudentAccount(studentId: String, password: String): Boolean {
    return studentId == "202500000" && password == "1234"
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen()
}
