package com.appcenter.uniclub.ui.mypage

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.NavGuard
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp
import kotlinx.coroutines.launch

//문의하기 화면
@Composable
fun InquiryScreen(navController: NavHostController) {
    val context = LocalContext.current

    //뒤로가기 연타/잔상 클릭 방지용
    val scope = rememberCoroutineScope()
    val navGuard = remember { NavGuard(lockMs = 800L) }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(Modifier.height(24.dp))
        TopBar( //상단바
            onBackClick = {
                scope.launch {
                    navGuard.run navGuardRun@{
                        val route = navController.currentBackStackEntry?.destination?.route.orEmpty()
                        if (route != "inquiry") return@navGuardRun

                        navController.popBackStack()
                    }
                }
            },
            title = "문의하기"
        )
        Spacer(Modifier.height(43.dp))

        //설명 텍스트
        Text(
            text = "UniClub 이용 중에 생긴 불편한 점이나 문의사항을 \n보내주세요 :-)",
            fontSize = figmaTextSizeSp(11f),
            fontFamily = NotoSansKR,
            lineHeight = 11.sp * 1.5f,
            letterSpacing = (-0.011).em,
            color = Color(0xFF7D7D7D),
            modifier = Modifier.padding(horizontal = 36.dp)
        )
        Spacer(Modifier.height(27.dp))

        //카카오톡 채널
        InquirySection(
            title = "Kakao Talk 채널",
            link = "pf.kakao.com/_xgxaSLd",
            buttonResId = R.drawable.btn_link,
            onButtonClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pf.kakao.com/_xgxaSLd"))
                context.startActivity(intent)
            }
        )

        //인스타그램
        InquirySection(
            title = "Instagram",
            link = "@inuappcenter",
            buttonResId = R.drawable.btn_link,
            onButtonClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/inuappcenter/"))
                context.startActivity(intent)
            }
        )

        //이메일
        InquirySection(
            title = "Email",
            link = "inuappcenter@gmail.com"
        )
    }
}

//문의하기 연락처 섹션
@Composable
fun InquirySection(
    modifier: Modifier = Modifier,
    title: String, //연락처 종류
    link: String, //연락처 텍스트
    buttonResId: Int? = null,
    onButtonClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .figmaPadding(startPx = 36f, bottomPx = 26f, endPx = 36f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = title,
                fontSize = figmaTextSizeSp(14f),
                fontFamily = NotoSansKR,
                fontWeight = FontWeight.Medium,
                lineHeight = 14.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color.Black
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = link,
                fontSize = figmaTextSizeSp(10f),
                fontFamily = NotoSansKR,
                lineHeight = 10.sp * 1.5f,
                letterSpacing = (-0.011).em,
                color = Color(0xFF7D7D7D)
            )
        }
        buttonResId?.let {
            Image(
                painter = painterResource(id = it),
                contentDescription = "링크 이동 버튼",
                modifier = Modifier
                    .figmaSize(widthPx = 34f, heightPx = 34f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onButtonClick() }
            )
        }
    }
}