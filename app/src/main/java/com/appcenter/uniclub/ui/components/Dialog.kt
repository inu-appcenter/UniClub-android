package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Divider
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
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

@Composable
fun Dialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) //반투명 배경
            .clickable(enabled = true, onClick = { onDismiss() }), //바깥 터치 시 닫기
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .figmaSize(widthPx = 225f, heightPx = 90f)
                .background(Color.White, shape = RoundedCornerShape(18.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //메시지
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(13f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 13.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)

            //버튼 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "취소",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(13f),
                        lineHeight = 13.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color.Black
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(0.5.dp),
                    color = Color(0xFFDDDDDD)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "확인",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(13f),
                        lineHeight = 13.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color.Black
                    )
                }
            }
        }
    }
}