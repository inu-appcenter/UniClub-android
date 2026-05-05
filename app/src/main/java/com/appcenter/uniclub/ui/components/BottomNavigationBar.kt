package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
fun BottomNavigationBar(
    navController: NavHostController
) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = backStackEntry?.destination?.route

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
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (currentRoute != "qna") {
                            navController.navigate("qna") {
                                launchSingleTop = true
                            }
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (currentRoute != "home") {
                            navController.navigate("home") {
                                launchSingleTop = true
                                popUpTo("home") {
                                    inclusive = false
                                }
                            }
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (currentRoute != "mypage") {
                            navController.navigate("mypage") {
                                launchSingleTop = true
                            }
                        }
                    }
            )
        }
    }
}