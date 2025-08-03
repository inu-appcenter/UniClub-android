package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

//상단바
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    title: String? = null,
    rightIconResId: Int? = null,
    onRightIconClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 23f, endPx = 23f, topPx = 15f)
    ) {
        //가운데 타이틀
        title?.let {
            Text(
                text = it,
                fontSize = figmaTextSizeSp(15f),
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        //왼쪽 뒤로가기 아이콘
        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .figmaSize(widthPx = 11f, heightPx = 20f)
                .align(Alignment.CenterStart)
                .clickable { onBackClick() }
        )

        //오른쪽 아이콘 (옵션)
        rightIconResId?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "오른쪽 아이콘",
                modifier = Modifier
                    .figmaSize(widthPx = 24f, heightPx = 23f)
                    .align(Alignment.CenterEnd)
                    .clickable { onRightIconClick() }
            )
        }
    }
}