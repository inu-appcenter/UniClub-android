package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    title: String? = null,
    rightIconResId: Int? = null,
    onRightIconClick: () -> Unit = {}
) {
    //화면 단위가 아니라 "이 TopBar 클릭"에서 네비게이션 중복을 막는 가드
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 300L) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 23f, endPx = 23f, topPx = 15f)
    ) {
        title?.let {
            androidx.compose.material3.Text(
                text = it,
                fontSize = figmaTextSizeSp(15f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 15.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_back_arrow),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .figmaSize(widthPx = 11f, heightPx = 20f)
                .align(Alignment.CenterStart)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    //여기서 연타/레이스 방지: pop/navigate가 짧은 시간에 2번 못 들어가게
                    scope.launch {
                        navGuard.run { onBackClick() }
                    }
                }
        )

        rightIconResId?.let { id ->
            Image(
                painter = painterResource(id = id),
                contentDescription = "오른쪽 아이콘",
                modifier = Modifier
                    .figmaSize(widthPx = 24f, heightPx = 23f)
                    .align(Alignment.CenterEnd)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        scope.launch {
                            navGuard.run { onRightIconClick() }
                        }
                    }
            )
        }
    }
}