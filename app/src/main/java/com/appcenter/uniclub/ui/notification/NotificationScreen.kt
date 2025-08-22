package com.appcenter.uniclub.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

//알림 페이지
@Composable
fun NotificationScreen(navController: NavHostController, vm: NotificationViewModel) {
    //vm에서 알림 리스트 상태 가져오기
    val notifications by vm.notifications.collectAsState(initial = emptyList())

    //안읽은 알림, 읽은 알림 분리
    val unread = notifications.filter { !it.isRead }
    val read = notifications.filter { it.isRead }

    Column(modifier = Modifier.fillMaxSize()){
        Spacer(modifier = Modifier.height(27.dp))
        //상단바
        TopBar(
            title = "알림",
            onBackClick = { navController.navigateUp() }
        )

        //알림이 하나도 없을 때
        if (unread.isEmpty() && read.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "알림이 없어요.",
                    fontSize = figmaTextSizeSp(20f),
                    fontFamily = NotoSansKR,
                    lineHeight = 20.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color(0xFFD3D3D3)
                )
            }
        } else {
            //알림 목록
            LazyColumn(modifier = Modifier.padding(top = 60.dp)) {
                //안읽은 알림 영역
                if (unread.isNotEmpty()) {
                    item { SectionTitle("안읽은 알림") }
                    items(unread, key = {it.id}) { item ->
                        SwipeToDeleteNotification(
                            item = item,
                            onDelete = { vm.delete(item.id) },  //삭제 동작
                            onClick = { //클릭 시 읽음 처리 + 해당 화면으로 이동
                                vm.markAsRead(item.id)
                                navigateByNotification(navController, item, vm)
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }

                //읽은 알림 영역
                if (read.isNotEmpty()) {
                    item { SectionTitle("읽은 알림") }
                    items(read, key = {it.id}) { item ->
                        SwipeToDeleteNotification(
                            item = item,
                            onDelete = { vm.delete(item.id) },
                            //읽은 알림은 클릭해도 상태 변화 없이 화면만 이동
                            onClick = { navigateByNotification(navController, item, vm) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(30.dp)) }
            }
        }
    }
}

//섹션 제목 (안읽은 알림/ 읽은 알림)
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = figmaTextSizeSp(11f),
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Medium,
        lineHeight = 11.sp * 1.5f,
        letterSpacing = (-0.011).em,
        color = Color(0xFFA1A1A1),
        modifier = Modifier.figmaPadding(startPx = 20f, bottomPx = 15f)
    )
}

//알림 종류에 따라 다른 화면으로 이동
fun navigateByNotification(
    rootNavController: NavHostController,
    item: NotificationItem,
    vm: NotificationViewModel
) {
    when (item.type) {
        NotificationType.INTERESTED_CLUB -> { //홍보
            rootNavController.navigate("notification_promotion") { launchSingleTop = true }
        }
        NotificationType.ANSWER_RECEIVED -> { //Q&A
            rootNavController.navigate("qna") { launchSingleTop = true }
        }
        NotificationType.NOTICE -> {
            vm.markAsRead(item.id)
            rootNavController.navigate("main")
        }
    }
}

//스와이프하여 삭제 버튼 노출
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteNotification(
    item: NotificationItem,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    //스와이프 상태 기억
    val dismissState = rememberDismissState(
        confirmStateChange = { value ->
            if (value == DismissValue.DismissedToStart) {
                onDelete()
            }
            true
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart), //오른쪽->왼쪽만 허용
        background = {
            val direction = dismissState.dismissDirection

            if (direction == DismissDirection.EndToStart) {
                // 스와이프 중일 때만 표시
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.9f)
                            .width(80.dp)
                            .offset(y = (-3.5).dp)
                            .background(
                                color = Color(0xFFFF5900),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "삭제",
                            fontSize = figmaTextSizeSp(11f),
                            fontFamily = NotoSansKR,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 11.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color.White
                        )
                    }
                }
            }
        },
        dismissContent = {
            NotificationCard(item = item, onClick = onClick)
        }
    )
}