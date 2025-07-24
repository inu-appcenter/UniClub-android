package com.appcenter.uniclub.ui.components.bottombar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

//하단 내비게이션 바
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    //BottomBar에 보여줄 화면 목록
    val items = listOf(
        Navigation.QnA, Navigation.Home, Navigation.MyPage
    )
    //현재 NavHostController의 back stack entry 를 관찰해서
    //어떤 route가 선택되어 있는지 실시간으로 가져옴
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        //정의한 items 리스트 순회
        items.forEach { screen ->
            NavigationBarItem(
                //아이콘 영역
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.label,
                        modifier = Modifier.size(30.dp)
                    )
                },
                //텍스트 영역
                label = { Text(screen.label, fontSize = 10.sp) },
                selected = (currentRoute == screen.route),
                //클릭 시 해당 route로 네비게이션
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            //스택 쌓임 방지
                            launchSingleTop = true
                            // 홈으로 돌아갈 때 startDestination까지만 popUp
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF0167FF), //선택된 아이콘 색: 파란색
                    selectedTextColor = Color(0xFF0167FF), //선택된 텍스트 색: 파란색
                    unselectedIconColor = Color.Black, //선택 안 됐을 때 아이콘 색
                    unselectedTextColor = Color.Black, //선택 안 됐을 때 텍스트 색
                    indicatorColor = Color.Transparent //하이라이트 색 제거
                )
            )
        }
    }
}