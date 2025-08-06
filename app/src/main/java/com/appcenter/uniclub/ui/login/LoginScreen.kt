package com.appcenter.uniclub.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appcenter.uniclub.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    //학번과 비밀번호 입력 상태
    var studentId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    //회원가입 안내 이미지 표시 여부
    var showJoinWarning by remember { mutableStateOf(false) }

    //로그인 버튼 활성화 조건 (둘다 비어있지 X)
    val isLoginEnabled = studentId.isNotBlank() && password.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        //로고 이미지
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.figmaPadding(startPx = 39f, topPx = 80f, bottomPx = 290f)){
                Image(
                    painter = painterResource(R.drawable.ic_logo_uniclub),
                    contentDescription = "로고",
                    modifier = Modifier.figmaSize(widthPx = 188f, heightPx = 36f)
                )
            }

            //입력 필드 및 버튼 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .figmaPadding(startPx = 37f, endPx = 37f),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                LoginInputField("학번", studentId, { studentId = it })
                Spacer(modifier = Modifier.height(20.dp))
                LoginInputField("비밀번호", password, { password = it }, isPassword = true)
                Spacer(modifier = Modifier.height(40.dp))

                //로그인 버튼
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 180f, heightPx = 54f)
                        .clickable(enabled = isLoginEnabled) {
                            if (studentId == "1111" && password == "1111") {
                                onLoginSuccess() //성공 시 콜백 호출
                            } else {
                                showJoinWarning = true //실패 시 안내 이미지 표시
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(
                            id = if (isLoginEnabled) R.drawable.btn_login else R.drawable.btn_login_disabled
                        ),
                        contentDescription = "로그인 버튼",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                //회원가입 버튼
                Text(
                    text = "회원가입",
                    fontSize = figmaTextSizeSp(12f),
                    color = Color.Black,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }

        //회원가입 안내 이미지
        //3초 후 자동으로 사라지는 로직
        LaunchedEffect(showJoinWarning) {
            if (showJoinWarning) {
                delay(3000) // 3초
                showJoinWarning = false
            }
        }
        if (showJoinWarning) {
            Image(
                painter = painterResource(R.drawable.img_warning_join),
                contentDescription = "회원가입 안내",
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 340.dp) // 위치 조절
                    .figmaSize(widthPx = 258.5f, heightPx = 43.64f)
            )
        }
    }
}

@Composable
fun LoginInputField(
    label: String,
    text: String, //현재 입력 값
    onTextChange: (String) -> Unit, //텍스트 변경 콜백
    isPassword: Boolean = false //비밀번호 true일 경우 점으로 표시
) {
    //밑줄 색상 결정: 값이 있으면 검정, 없으면 회색
    val underlineColor = if (text.isNotBlank()) Color.Black else Color(0xFFBFBFBF)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = figmaTextSizeSp(14f),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(5.dp))

        //입력 필드, 밑줄
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            //텍스트 필드
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = figmaTextSizeSp(18f),
                    color = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp) //텍스트와 밑줄 사이 여백
            )

            //밑줄
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(underlineColor)
            )
        }
    }
}