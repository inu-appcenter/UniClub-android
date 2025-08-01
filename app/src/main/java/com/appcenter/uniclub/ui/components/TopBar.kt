package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    title: String? = null,
    rightIconResId: Int? = null,
    onRightIconClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 23f, endPx = 23f, topPx = 18f),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            //뒤로가기 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "뒤로가기",
                modifier = Modifier
                    .figmaSize(widthPx = 11f, heightPx = 20f)
                    .clickable { onBackClick() }
            )

            // ← 텍스트 (옵션)
            title?.let {
                Spacer(modifier = Modifier.width(11.dp)) // 아이콘과 텍스트 간격
                Text(
                    text = it,
                    fontSize = figmaTextSizeSp(15f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.011).em,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        // → 오른쪽 아이콘 이미지 (옵션)
        rightIconResId?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "오른쪽 아이콘",
                modifier = Modifier
                    .figmaSize(widthPx = 24f, heightPx = 23f)
                    .clickable { onRightIconClick() }
            )
        }
    }
}
