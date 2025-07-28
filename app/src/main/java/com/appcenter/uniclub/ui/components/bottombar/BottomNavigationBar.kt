package com.appcenter.uniclub.ui.components.bottombar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color

//하단 내비게이션 바 (수정 필요)
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val barImage = remember(currentRoute) {
        when (currentRoute) {
            "qna" -> R.drawable.qna_navigation
            "mypage" -> R.drawable.my_navigation
            else -> R.drawable.home_navigation
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // 상단만 보이게 설정 (절반)
            .offset(y = 40.dp) // 전체 높이 중 하단을 화면 밖으로 내보냄
            .background(Color.Transparent)
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        // 전체 이미지 (450 x 80)
        Image(
            painter = painterResource(id = barImage),
            contentDescription = "Bottom Navigation Bar",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .width(screenWidth)
                .height(80.dp) // 원래 이미지 높이
                .scale(1.5f)
        )

        // 클릭 가능한 3등분 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .offset(y = (-40).dp)
        ) {
            val routes = listOf("qna", "home", "mypage")
            routes.forEach { route ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            if (currentRoute != route) {
                                navController.navigate(route) {
                                    launchSingleTop = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        inclusive = false
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BottomNavigationBar(navController = navController)
}
