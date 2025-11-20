package com.appcenter.uniclub.ui.notification

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.model.NotificationItem
import com.appcenter.uniclub.model.NotificationType
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaTextSizeSp

//알림 페이지
//알림 화면 탭 종류
private enum class NotificationTab { UNREAD, READ }

@Suppress("UNUSED_PARAMETER")
@Composable
fun NotificationScreen(
    navController: NavHostController,
    vm: NotificationViewModel
) {
    val ui by vm.uiState.collectAsState()
    val unread by remember(ui.items) { derivedStateOf { ui.items.filter { !it.isRead } } } //안읽은 알림 필터링
    val read   by remember(ui.items) { derivedStateOf { ui.items.filter {  it.isRead } } } //읽은 알림 필터링
    var currentTab by rememberSaveable { mutableStateOf(NotificationTab.UNREAD) } //탭 상태

    Column(Modifier.fillMaxSize()) {
        Spacer(Modifier.height(27.dp))
        TopBar(title = "알림", onBackClick = { navController.navigateUp() }) //상단바

        //탭 세그먼트 (안읽은/읽은)
        HeaderSegments(current = currentTab, unreadCount = unread.size) { currentTab = it }

        Row( //전체 읽음 or 전체 삭제
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isUnreadTab = currentTab == NotificationTab.UNREAD
            //현재 탭에 따라 액션 가능 여부
            val actionEnabled = if (isUnreadTab) unread.isNotEmpty() else read.isNotEmpty()
            //라벨은 탭에 따라 변경
            val actionLabel = if (isUnreadTab) "전체 읽음" else "전체 삭제"
            //활성 상태에 때에 따라 색 변경
            val actionColor = if (actionEnabled) Color(0xFFFF5900) else Color.Transparent

            Text(
                text = actionLabel,
                fontSize = figmaTextSizeSp(11f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.011).em,
                color = actionColor,
                modifier = if (actionEnabled) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isUnreadTab) vm.markAllAsRead() else vm.deleteAllRead()
                    }
                } else {
                    Modifier
                }
            )
        }
        Spacer(Modifier.height(10.dp))

        //현재 탭에 대응하는 리스트
        val list = if (currentTab == NotificationTab.UNREAD) unread else read

        if (list.isEmpty()) { //리스트 없을 때
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 12.dp),
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
        } else { //리스트 있을 때
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 13.dp, end = 13.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp) //카드 간격
            ) {
                items(list, key = { it.id }) { item ->
                    SwipeToDeleteNotification( //스와이프 삭제
                        item = item,
                        modifier = Modifier.fillMaxWidth(),
                        onDelete = { vm.delete(item.id) },
                        onClick = {
                            if (!item.isRead) vm.markAsRead(item.id)
                            navigateByNotification(navController, item) }
                    )
                }
                item { Spacer(Modifier.height(30.dp)) }

                if(ui.hasNext) {
                    item {
                        LaunchedEffect(ui.currentPage, ui.hasNext) {
                            vm.loadPage(ui.currentPage + 1)
                        }
                    }
                }
            }
        }
    }
}

//세그먼트 탭 (안읽은/읽은)
@Composable
private fun HeaderSegments(
    current: NotificationTab, //현재 선택된 탭
    unreadCount: Int, //안읽은 알림 개수
    onChange: (NotificationTab) -> Unit //탭 변경 콜백
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        SegmentWithLabel( //왼쪽 탭
            label = "안읽은 알림",
            selected = current == NotificationTab.UNREAD,
            alignRight = true,
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onChange(NotificationTab.UNREAD) }
        )
        Spacer(Modifier.width(30.dp))
        SegmentWithLabel( //오른쪽 탭
            label = "읽은 알림",
            selected = current == NotificationTab.READ,
            alignRight = false,
            modifier = Modifier
                .weight(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onChange(NotificationTab.READ) }
        )
    }
}

//세그먼트
@Composable
private fun SegmentWithLabel(
    label: String,
    selected: Boolean, //선택 여부
    alignRight: Boolean,
    modifier: Modifier = Modifier
) {
    val barHeight = 4.dp //바 높이
    val barWidth = 150.dp //바 너비
    val segmentHeight = 70.dp //세그먼트 전체 높이
    //선택 시 더 위쪽으로 이동
    val gapFromBottom by animateDpAsState(
        targetValue = if (selected) 30.dp else 15.dp,
        label = "segGap"
    )
    //선택/비선택 색상
    val highlightColor by animateColorAsState(
        targetValue = if (selected) Color(0xFFFF5900) else Color(0xFFD9D9D9),
        label = "segColor"
    )

    Column(
        modifier = modifier.height(segmentHeight),
        horizontalAlignment = Alignment.Start
    ) {
        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column( //바+라벨 묶음
                modifier = Modifier
                    .align(if (alignRight) Alignment.BottomEnd else Alignment.BottomStart)
                    .width(barWidth)
                    .padding(bottom = gapFromBottom)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .background(highlightColor, RoundedCornerShape(50))
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = label,
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    letterSpacing = (-0.011).em,
                    color = if (selected) Color(0xFFFF5900) else Color(0xFFD9D9D9)
                )
            }
        }
    }
}

//알림 탭했을 때 화면 이동
private fun navigateByNotification(
    rootNavController: NavHostController,
    item: NotificationItem
) {
    when (item.type) {
        NotificationType.CLUB ->
            rootNavController.navigate("notification_promotion/${item.targetId}") { launchSingleTop = true }
        NotificationType.QNA ->
            rootNavController.navigate("question/${item.targetId}") { launchSingleTop = true }
        NotificationType.FEDERATION ->
            rootNavController.navigate("main")
        NotificationType.SYSTEM, NotificationType.PERSONAL -> null
    }
}

//스와이프 삭제 액션
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteNotification(
    item: NotificationItem, //알림 아이템
    modifier: Modifier = Modifier,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    //스와이프 상태. true 시점에 삭제 실행
    val dismissState = rememberDismissState(
        confirmStateChange = { value ->
            if (value == DismissValue.DismissedToStart) onDelete()
            true
        }
    )

    SwipeToDismiss(
        modifier = modifier,
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart), //오른쪽 -> 왼쪽으로만 스와이프 허용
        background = { //배경: 스와이프 중일 때 삭제 버튼 노출
            if (dismissState.dismissDirection == DismissDirection.EndToStart) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 15.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box( //삭제 버튼
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(87.dp)
                            .background(Color(0xFFFF5900), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
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
        //알림 카드 콘텐츠
        dismissContent = { NotificationCard(item = item, onClick = onClick) }
    )
}