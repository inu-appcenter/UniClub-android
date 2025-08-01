package com.appcenter.uniclub.ui.promotion

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.R
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Divider
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.zIndex
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

//사용자용 홍보 페이지
@Composable
fun UserPromotionScreen(navController: NavHostController) {
    var isLiked by remember { mutableStateOf(false) } //즐겨찾기 상태 저장
    var isRecruiting by remember { mutableStateOf(true) } //모집중, 모집예정 상태 저장
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) //스크롤
                .zIndex(0f)
        ) {
            //배너 + TopBar + 프로필 사진 겹치는 구조
            Box(modifier = Modifier.height(209.dp)) {
                Image( //배너
                    painter = painterResource(R.drawable.banner),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
                )

                PromotionTopBar( // 상단바
                    isLiked = isLiked,
                    onBackClick = { navController.popBackStack() },
                    onLikeClick = { isLiked = !isLiked },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(63.dp)
                        .zIndex(1f) //배너 위에 배치
                )

                //프로필 이미지
                Image(
                    painter = painterResource(R.drawable.profile_example),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(113.dp)
                        .offset(x = 24.dp, y = 209.dp - 113.dp / 2) //배너 반쯤 걸쳐서
                        .clip(RoundedCornerShape(40.dp))
                        .zIndex(1f)
                )
            }

            //SNS 버튼 (유튜브, 인스타)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 17.dp, end = 15.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { /* TODO */ }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { /* TODO */ }
                )
            }

            //동아리명 + 모집상태
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 143f, heightPx = 30f)
                        .clip(RoundedCornerShape(45.dp))
                        .background(Color(0xFFFF5900)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text( //동아리명
                        text = "크레퍼스(CREPERS)",
                        color = Color.White,
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = figmaTextSizeSp(14f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Image( //모집상태
                    painter = painterResource(
                        id = if (isRecruiting) R.drawable.ic_recruiting else R.drawable.ic_upcoming
                    ),
                    contentDescription = if (isRecruiting) "모집중" else "모집예정",
                    modifier = Modifier
                        .offset(x = (-18).dp)
                        .clickable { isRecruiting = !isRecruiting }
                )
            }

            //동아리 정보 3개
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 15.dp),
                horizontalArrangement = Arrangement.spacedBy(35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoItem(title = "동아리방", value = "17호관 414호")
                InfoItem(title = "회장", value = "이석준")
                InfoItem(title = "연락처", value = "010.1234.5678")
            }

            Spacer(modifier = Modifier.height(25.dp))

            //한줄 소개
            TextShadowBanner(text = "동아리에서 함께 연주하고 추억을 쌓아봐요!")

            Spacer(modifier = Modifier.height(35.dp))

            //모집 안내, 공지
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                AnnouncementItem(label = "모집기간", content = "7월 16일 ~ 24일 오후 6시")
                AnnouncementItem(label = "공지", content = "25일 동아리실 출입금지")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color(0xFFDDDDDD), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(15.dp))

            ClubDescription(
                description = "이 글은 동아리 소개 예시글입니다. 저희 동아리는 전공과 학년을 넘어 다양한 사람들이 모여 공통의 관심사를 나누고, 함께 경험을 쌓아가는 공간입니다. 활동 하나하나에 진심을 담고, 소소한 일상도 특별하게 만드는 우리! 처음이라도 괜찮아요. 언제든 환영합니다. 당신의 자리를 만들어드릴게요."
            )

            //활동 사진 캐러셀, 질문지원 버튼
            val sampleImages = listOf(
                R.drawable.club1, R.drawable.club2, R.drawable.club3,
                R.drawable.club1, R.drawable.club2
            )
            Column {
                ActivityImageCarousel(imageResIds = sampleImages)
                BottomActionButtons()
            }

            Spacer(modifier = Modifier.height(75.dp))
        }
    }
}

//동아리 정보 컴포넌트
@Composable
fun InfoItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            color = Color.Black
        )
    }
}

//한줄 소개 컴포넌트
@Composable
fun TextShadowBanner(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
            .graphicsLayer {
                shadowElevation = 10.dp.toPx() //높이 조절
                shape = RoundedCornerShape(0.dp)
                clip = false
            }
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF9F9F9),
                        Color(0xFFFFFFFF)
                    )
                ),
                shape = RoundedCornerShape(0.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFFFF5900),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 30.dp)
        )
    }
}

//공지 컴포넌트
@Composable
fun AnnouncementItem(label: String, content: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.width(70.dp)
        )
        Text(
            text = content,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

//동아리 설명 컴포넌트
@Composable
fun ClubDescription(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 20.dp)
    ) {
        Text(
            text = description,
            fontSize = 13.sp,
            color = Color.Black,
            lineHeight = 20.sp
        )
    }
}

//활동사진 캐러셀
@Composable
fun ActivityImageCarousel(imageResIds: List<Int>) {
    val limitedImages = imageResIds.take(10) //최대 10장 제한

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 21.dp, top = 15.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        itemsIndexed(limitedImages) { index, resId ->
            val isLast = index == limitedImages.lastIndex
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .figmaSize(widthPx = 139f, heightPx = 183f)
                    .clip(RoundedCornerShape(25.dp))
                    .then(
                        if (isLast) Modifier.padding(end = 20.dp) else Modifier
                    )
            )
        }
    }
}

//하단 버튼 정렬 컴포넌트
@Composable
fun BottomActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally)
    ) {
        ImageButtonItem(
            imageRes = R.drawable.btn_question,
            contentDescription = "질문하기",
            onClick = { /* TODO: 질문하기 기능 */ }
        )
        ImageButtonItem(
            imageRes = R.drawable.btn_apply,
            contentDescription = "지원하기",
            onClick = { /* TODO: 지원하기 기능 */ }
        )
    }
}

@Composable
fun ImageButtonItem(
    imageRes: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = contentDescription,
        modifier = Modifier
            .clickable(onClick = onClick)
    )
}

@Preview(showBackground = true)
@Composable
fun UserPromotionScreenPreview() {
    val navController = rememberNavController()
    UserPromotionScreen(navController = navController)
}
