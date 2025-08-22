package com.appcenter.uniclub.ui.mypage

import android.view.MenuItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign

//마이페이지 화면
@Composable
fun MypageScreen(navController: NavHostController, rootNavController: NavHostController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
        .fillMaxSize()
        .verticalScroll(scrollState)
        .figmaPadding(startPx = 27f, endPx = 27f, topPx = 15f))
    {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "마이페이지",
                fontSize = figmaTextSizeSp(15f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Bold,
                lineHeight = 15.sp * 1.5f, // 행간
                letterSpacing = (-0.011).em, // 자간
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(40.dp))

        Row(verticalAlignment = Alignment.CenterVertically){
            Image( //프로필
                painter = painterResource(R.drawable.default_image),
                contentDescription = null,
                modifier = Modifier.figmaSize(widthPx = 70f, heightPx = 75f)
            )
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)){
                Row {
                    Text( //이름
                        text = "홍길동",
                        fontSize = figmaTextSizeSp(16f),
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 16.sp * 1.5f, // 행간
                        letterSpacing = (-0.011).em, // 자간
                        color = Color.Black,
                        modifier = Modifier.alignByBaseline()
                    )
                    Spacer(Modifier.width(6.dp))
                    Text( //닉네임
                        text = "닉네임",
                        fontSize = figmaTextSizeSp(11f),
                        fontFamily = NotoSansKR,
                        lineHeight = 11.sp * 1.5f, // 행간
                        letterSpacing = (-0.011).em, // 자간
                        color = Color.Black,
                        modifier = Modifier
                            .alignByBaseline()
                            .offset(y = 1.dp)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text( //학과
                    text = "디자인학부",
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 11.sp * 1.5f, // 행간
                    letterSpacing = (-0.011).em, // 자간
                    color = Color(0xFF818181)
                )
                Spacer(Modifier.height(5.dp))
                Text( //학번
                    text = "23학번",
                    fontSize = figmaTextSizeSp(11f),
                    fontFamily = NotoSansKR,
                    lineHeight = 11.sp * 1.5f, // 행간
                    letterSpacing = (-0.011).em, // 자간
                    color = Color(0xFF818181)
                )
            }
        }

        Spacer(modifier = Modifier.height(25.dp))
        Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(20.dp))

        //계정 섹션
        Section(title = "계정")
        Spacer(modifier = Modifier.height(10.dp))

        MenuItem(
            text = "알림 설정",
            onClick = { navController.navigate("alarmSetting") }
        )
        MenuItem(
            text = "프로필 수정",
            onClick = { navController.navigate("profileEdit") }
        )
        MenuItem(
            text = "로그아웃",
            onClick = { showLogoutDialog = true }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(20.dp))

        //이용안내 섹션
        Section(title = "이용안내")
        Spacer(modifier = Modifier.height(17.dp))

        MenuItem(
            text = "문의하기",
            onClick = { navController.navigate("inquiry") }
        )
        MenuItem(
            text = "이용약관",
            onClick = { navController.navigate("terms") }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(20.dp))

        //기타 섹션
        Section(title = "기타")
        Spacer(modifier = Modifier.height(17.dp))

        MenuItem(
            text = "계정 삭제",
            onClick = { navController.navigate("delete") }
        )

        Spacer(Modifier.height(20.dp))
    }

    //로그아웃 다이얼로그
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                rootNavController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun Section(title: String) {
    Text(
        text = title,
        fontSize = figmaTextSizeSp(17f),
        fontFamily = NotoSansKR,
        fontWeight = FontWeight.Bold,
        lineHeight = 17.sp * 1.5f, // 행간
        letterSpacing = (-0.011).em, // 자간
        color = Color.Black
    )
}

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp), //터치 영역 확보
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = figmaTextSizeSp(14f),
            fontFamily = NotoSansKR,
            lineHeight = 14.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color.Black
        )
    }
}

//로그아웃 다이얼로그
@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)) //반투명 배경
            .clickable(enabled = true, onClick = { onDismiss() }), //바깥 터치 시 닫기
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .figmaSize(widthPx = 225f, heightPx = 90f)
                .background(Color.White, shape = RoundedCornerShape(18.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //메시지
            Box(
                modifier = Modifier
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그아웃하시겠습니까?",
                    fontFamily = NotoSansKR,
                    fontSize = figmaTextSizeSp(13f),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 13.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
            Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)

            //버튼 영역
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "닫기",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(13f),
                        lineHeight = 13.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color.Black
                    )
                }
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(0.5.dp),
                    color = Color(0xFFDDDDDD)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "확인",
                        fontFamily = NotoSansKR,
                        fontSize = figmaTextSizeSp(13f),
                        lineHeight = 13.sp * 1.5f,
                        letterSpacing = (-0.011).em,
                        color = Color.Black
                    )
                }
            }
        }
    }
}