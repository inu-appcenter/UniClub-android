package com.appcenter.uniclub.ui.promotion

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.appcenter.uniclub.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize

@Composable
fun PromotionTopBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    isLiked: Boolean,
    onLikeClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(63.dp)
            .zIndex(10f)
            .background(Color.Transparent)
    ) {
        Image(
            painter = painterResource(id = R.drawable.promotion_topbar_bg), // ← 배경 이미지
            contentDescription = "TopBar Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        // 왼쪽 뒤로가기 버튼 이미지
        Image(
            painter = painterResource(id = R.drawable.ic_back_white),
            contentDescription = "Back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .figmaPadding(startPx = 20f, topPx = 5f)
                .figmaSize(widthPx = 11f, heightPx = 20f)
                .clickable { onBackClick() }
        )

        // 오른쪽 좋아요 버튼 이미지
        Image(
            painter = painterResource(
                id = if (isLiked) R.drawable.promotion_favorite_filled else R.drawable.promotion_favorite
            ),
            contentDescription = if (isLiked) "즐겨찾기 취소" else "즐겨찾기",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .figmaPadding(endPx = 20f, topPx = 5f)
                .figmaSize(widthPx = 25f, heightPx = 22f)
                .clickable { onLikeClick() }
        )
    }
}

@Preview(showBackground = false)
@Composable
fun PromotionTopBarPreview() {
    var isLiked by remember { mutableStateOf(false) }
    PromotionTopBar(
        isLiked = isLiked,
        onBackClick = {},
        onLikeClick = { isLiked = !isLiked } // ← 여기서 상태 토글
    )
}