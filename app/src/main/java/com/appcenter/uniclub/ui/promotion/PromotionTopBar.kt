package com.appcenter.uniclub.ui.promotion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.appcenter.uniclub.R
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import kotlinx.coroutines.launch

@Composable
fun PromotionTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    showEdit: Boolean = true,            // 권한 등에 따라 노출 제어
    onEditClick: () -> Unit = {}         // 수정 버튼 클릭 콜백
) {
    //연타 방지 (TopBar와 동일 패턴)
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 300L) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .zIndex(10f)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFF5900).copy(alpha = 0.6f),
                        Color(0x00FF5900)
                    )
                )
            )
    ) {

        //왼쪽 뒤로가기 버튼 이미지
        Image(
            painter = painterResource(id = R.drawable.ic_back_white),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .figmaPadding(startPx = 23f)
                .figmaSize(widthPx = 11f, heightPx = 20f)
                .offset(y = (-1).dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    scope.launch {
                        navGuard.run { onBackClick() }
                    }
                }
        )

        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .figmaPadding(endPx = 20f),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showEdit) {
                //수정 버튼
                Box(
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        scope.launch {
                            navGuard.run { onEditClick() }
                        }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "수정하기",
                        modifier = Modifier.figmaSize(widthPx = 24f, heightPx = 24f)
                    )
                }
            }

            //즐겨찾기 버튼
            Box(
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    scope.launch {
                        navGuard.run { onLikeClick() }
                    }
                },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(
                        id = if (isLiked) R.drawable.promotion_favorite_filled
                        else R.drawable.promotion_favorite
                    ),
                    contentDescription = if (isLiked) "즐겨찾기 취소" else "즐겨찾기",
                    modifier = Modifier.figmaSize(widthPx = 25f, heightPx = 22f)
                )
            }
        }
    }
}