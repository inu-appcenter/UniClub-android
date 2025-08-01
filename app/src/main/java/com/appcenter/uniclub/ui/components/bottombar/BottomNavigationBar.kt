package com.appcenter.uniclub.ui.components.bottombar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.R
import androidx.compose.ui.Alignment
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
            .wrapContentSize()
            .clipToBounds()
            .background(Color.Transparent)
            .navigationBarsPadding()
    ) {
        Image(
            painter = painterResource(id = barImage),
            contentDescription = "Bottom Navigation Bar",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomCenter) // 가운데 정렬
        )

        Row(
            modifier = Modifier
                .matchParentSize()
                .align(Alignment.BottomCenter) // 이미지 위에 딱 맞게
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

@Preview(showBackground = false)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BottomNavigationBar(navController = navController)
}
