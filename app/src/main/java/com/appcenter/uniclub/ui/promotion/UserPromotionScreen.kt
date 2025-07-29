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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.appcenter.uniclub.ui.components.TopBar
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.zIndex

@Composable
fun UserPromotionScreen(navController: NavHostController,) {
    //좋아요 상태 저장용 변수
    var isLiked by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxWidth()) {
        //배너
        Image(
            painter = painterResource(R.drawable.banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(209.dp)
                .clip(RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp))
        )

        //프로필 이미지
        Image(
            painter = painterResource(R.drawable.profile_example), // 교체 필요
            contentDescription = "Profile Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(113.dp)
                .offset(x = 24.dp, y = 209.dp - 113.dp / 2) // 절반 걸치도록 오프셋
                .clip(RoundedCornerShape(40.dp))
        )

        //SNS 버튼 (수정 필요)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .offset(y = 209.dp + 17.dp)
                .fillMaxWidth()
                .padding(end = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5900))
                    .clickable { /* TODO: Instagram 이동 */ }
            )
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF5900))
                    .clickable { /* TODO: YouTube 이동 */ }
            )
        }

        //스크롤 가능한 본문 콘텐츠 (회의 후 수정)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 209.dp + 113.dp / 2 )
                .zIndex(0f) //상단바보다 뒤에 배치
        ) {
            //모집중 상태
            var isRecruiting by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 28.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //동아리명 박스
                Box(
                    modifier = Modifier
                        .width(143.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(Color(0xFFFF5900)),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "크레퍼스(CREPERS)",
                        color = Color.White,
                        modifier = Modifier.padding(start = 12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                //모집중 버튼
                Row(
                    modifier = Modifier
                        .offset(x = (-15).dp)
                        .width(54.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(Color(0xFF363636))
                        .clickable { isRecruiting = !isRecruiting },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "모집중",
                        color = Color.White,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.width(3.dp))

                    Icon(
                        imageVector = Icons.Filled.Notifications, // 종 아이콘
                        contentDescription = "알림",
                        tint = if (isRecruiting) Color(0xFFFF5900) else Color.White,
                        modifier = Modifier.size(10.dp)
                    )
                }
            }

            //동아리 정보 (위치, 회장, 연락처)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoItem(title = "동아리방", value = "17호관 414호")
                InfoItem(title = "회장", value = "이석준")
                InfoItem(title = "연락처", value = "010.1234.5678")
            }

            Spacer(modifier = Modifier.height(25.dp))

            //한줄소개
            TextShadowBanner(text = "동아리에서 함께 연주하고 추억을 쌓아봐요!")

            Spacer(modifier = Modifier.height(35.dp))

            //공지사항 (모집기간 등)
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

            //실선
            Divider(color = Color(0xFFDDDDDD), thickness = 1.dp)

            Spacer(modifier = Modifier.height(15.dp))

            //소개글
            ClubDescription(
                description = "이 글은 동아리 소개 예시글입니다. 저희 동아리는 전공과 학년을 넘어 다양한 사람들이 모여 공통의 관심사를 나누고, 함께 경험을 쌓아가는 공간입니다. 활동 하나하나에 진심을 담고, 소소한 일상도 특별하게 만드는 우리! 처음이라도 괜찮아요. 언제든 환영합니다. 당신의 자리를 만들어드릴게요."
            )

            //활동사진 캐러셀 & 버튼2개
            val sampleImages = listOf(
                R.drawable.club1, R.drawable.club2, R.drawable.club3,
                R.drawable.club1, R.drawable.club2
            )

            Column {
                ActivityImageCarousel(imageResIds = sampleImages)
                BottomActionButtons()
            }
        }
/*
        //상단바 (뒤로가기, 좋아요)
        TopBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .zIndex(10f),
            onBackClick = { navController.navigateUp() },
            rightIcon = if (isLiked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
            rightIconTint = if (isLiked) Color(0xFFF30000) else Color.White,
            onRightIconClick = { isLiked = !isLiked }
        )
 */
    }
}

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

@Composable
fun TextShadowBanner(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(39.dp)
            .graphicsLayer {
                // 그림자 효과 주기 위해 graphicsLayer 사용
                shadowElevation = 15.dp.toPx() // 높이 조절
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
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 21.dp, top = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(imageResIds.take(10)) { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 139.dp, height = 183.dp)
                    .clip(RoundedCornerShape(25.dp))
            )
        }
    }
}

@Composable
fun BottomActionButtons() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp), // 필요 시 조정
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally)
    ) {
        ButtonItem(text = "질문하기")
        ButtonItem(text = "지원하기")
    }
}

//하단 버튼 두개
@Composable
fun ButtonItem(text: String) {
    Box(
        modifier = Modifier
            .width(152.dp)
            .height(54.dp)
            .clip(RoundedCornerShape(45.dp))
            .background(Color(0xFF2C2C2C)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserPromotionScreenPreview() {
    val navController = rememberNavController()
    UserPromotionScreen(navController = navController)
}