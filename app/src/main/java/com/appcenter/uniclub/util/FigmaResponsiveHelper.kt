package com.appcenter.uniclub.ui.util

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit

// 기준 해상도: Figma 디자인 기준 (가로 360dp)
private const val BASE_WIDTH_DP = 360f

/**
 * Figma px 기준으로 Modifier.size 변환
 */
@Composable
fun Modifier.figmaSize(
    widthPx: Float,
    heightPx: Float
): Modifier {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val width = (widthPx / BASE_WIDTH_DP) * screenWidthDp
    val height = (heightPx / BASE_WIDTH_DP) * screenWidthDp
    return this.then(Modifier.size(width.dp, height.dp))
}

/**
 * Figma px 기준으로 Padding 변환 (start, top, end, bottom)
 */
@Composable
fun Modifier.figmaPadding(
    startPx: Float = 0f,
    topPx: Float = 0f,
    endPx: Float = 0f,
    bottomPx: Float = 0f
): Modifier {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return this.then(
        Modifier.padding(
            start = (startPx / BASE_WIDTH_DP * screenWidthDp).dp,
            top = (topPx / BASE_WIDTH_DP * screenWidthDp).dp,
            end = (endPx / BASE_WIDTH_DP * screenWidthDp).dp,
            bottom = (bottomPx / BASE_WIDTH_DP * screenWidthDp).dp
        )
    )
}

/**
 * Figma px 기준으로 텍스트 크기 변환 (dp)
 */
@Composable
fun figmaTextSize(px: Float): Dp {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return ((px / BASE_WIDTH_DP) * screenWidthDp).dp
}

@Composable
fun figmaTextSizeSp(px: Float): TextUnit {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    return ((px / BASE_WIDTH_DP) * screenWidthDp).sp
}

/**
 * Figma px 기준 offset 변환 (X, Y)
 */
@Composable
fun Modifier.figmaOffset(
    offsetXPx: Float = 0f,
    offsetYPx: Float = 0f
): Modifier {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val xDp = (offsetXPx / BASE_WIDTH_DP) * screenWidthDp
    val yDp = (offsetYPx / BASE_WIDTH_DP) * screenWidthDp
    return this.then(Modifier.offset(x = xDp.dp, y = yDp.dp))
}
