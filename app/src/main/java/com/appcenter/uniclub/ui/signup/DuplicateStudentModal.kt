package com.appcenter.uniclub.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaTextSizeSp

@Composable
fun DuplicateStudentModal(
    onRetry: () -> Unit,
    onLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x66000000))
            .padding(horizontal = 0.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Color.White, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 30.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "이미 가입된 회원입니다.",
                fontSize = figmaTextSizeSp(18f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp * 1.5f,
                letterSpacing = (-0.011).em
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = "로그인하거나 학번 또는 비밀번호를 \n다시 확인해 주세요.",
                fontSize = figmaTextSizeSp(14f),
                fontFamily = NotoSansKR,
                color = Color(0xFF2B2B2B),
                lineHeight = 14.sp * 1.5f,
                letterSpacing = (-0.011).em,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp)
                        .background(Color.Black, RoundedCornerShape(45.dp))
                        .clickable { onRetry() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "다시 입력",
                        color = Color.White,
                        fontSize = figmaTextSizeSp(14f),
                        fontFamily = NotoSansKR,
                        lineHeight = 14.sp * 1.5f,
                        letterSpacing = (-0.011).em
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp)
                        .background(Color(0xFFFF5900), RoundedCornerShape(45.dp))
                        .clickable { onLogin() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "로그인",
                        color = Color.White,
                        fontSize = figmaTextSizeSp(14f),
                        fontFamily = NotoSansKR,
                        lineHeight = 14.sp * 1.5f,
                        letterSpacing = (-0.011).em
                    )
                }
            }
        }
    }
}
