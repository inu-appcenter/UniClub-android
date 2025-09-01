package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaTextSizeSp

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
        modifier = Modifier.figmaPadding(bottomPx = 10f)
    )
}

//밑줄 형태 입력 필드
@Composable
fun UnderlineInputField(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit, //텍스트 변경 콜백
    enabled: Boolean, //입력 가능 여부
    isPassword: Boolean = false
) {
    //밑줄 색상: 활성/비활성 구분
    val underlineColor = if (enabled) Color.Black else Color(0xFFBFBFBF) //비활성화 시 회색

    Column(modifier = modifier.fillMaxWidth()) {
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