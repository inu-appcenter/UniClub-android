package com.appcenter.uniclub.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Help
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun BottomNavigationBar() {

    //현재 선택된 아이템 인덱스 (0: Q&A, 1: 홈, 2: MY)
    var selectedIndex by remember { mutableStateOf(1) } //초기값: 홈

    //기본 아이콘 설정, 추후 변경
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 4.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Help,
                    contentDescription = "Q&A",
                    modifier = Modifier.size(30.dp) //아이콘 크기 추후 변경
                )
            },
            label = { Text("Q&A", fontSize = 10.sp) },
            selected = selectedIndex == 0, //선택 여부
            onClick = { selectedIndex = 0 }, //클릭 시 상태 업데이트
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0167FF), //선택된 아이콘 색: 파란색
                selectedTextColor = Color(0xFF0167FF), //선택된 텍스트 색: 파란색
                unselectedIconColor = Color.Black, //선택 안 됐을 때 아이콘 색
                unselectedTextColor = Color.Black, //선택 안 됐을 때 텍스트 색
                indicatorColor = Color.Transparent //하이라이트 색 제거
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "홈",
                    modifier = Modifier.size(30.dp)
                )
            },
            label = { Text("홈", fontSize = 10.sp) },
            selected = selectedIndex == 1,
            onClick = { selectedIndex = 1 },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0167FF),
                selectedTextColor = Color(0xFF0167FF),
                unselectedIconColor = Color.Black,
                unselectedTextColor = Color.Black,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "MY",
                    modifier = Modifier.size(30.dp)
                )
            },
            label = { Text("MY", fontSize = 10.sp) },
            selected = selectedIndex == 2,
            onClick = { selectedIndex = 2 },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF0167FF),
                selectedTextColor = Color(0xFF0167FF),
                unselectedIconColor = Color.Black,
                unselectedTextColor = Color.Black,
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    BottomNavigationBar()
}