package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp

@Composable
fun ReportDialog(
    title: String,
    reason: String,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(999f)
            .background(Black.copy(alpha = 0.5f))
            .imePadding()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .figmaSize(widthPx = 240f, heightPx = 150f)
                .background(Color.White, RoundedCornerShape(18.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 15.dp)
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(13f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 13.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .background(
                            color = Color(0xFFF7F7F7),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    BasicTextField(
                        value = reason,
                        onValueChange = { onReasonChange(it) },
                        modifier = Modifier.fillMaxSize(),
                        textStyle = TextStyle(
                            color = Black,
                            fontSize = figmaTextSizeSp(11f),
                            fontFamily = NotoSansKR,
                            lineHeight = 11.sp * 1.5f,
                            letterSpacing = (-0.011).em
                        ),
                        decorationBox = { innerTextField ->
                            if (reason.isBlank()) {
                                Text(
                                    text = "신고 사유를 입력해주세요.",
                                    fontSize = figmaTextSizeSp(11f),
                                    fontFamily = NotoSansKR,
                                    lineHeight = 11.sp * 1.5f,
                                    letterSpacing = (-0.011).em,
                                    color = Color(0xFF767676)
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)

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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material.Text(
                        text = "취소",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(13f),
                        lineHeight = 13.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Black
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
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onConfirm(reason.trim()) },
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material.Text(
                        text = "확인",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(13f),
                        lineHeight = 13.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Black
                    )
                }
            }
        }
    }
}