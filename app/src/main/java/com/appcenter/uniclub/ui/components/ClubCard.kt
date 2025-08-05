package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.appcenter.uniclub.model.Club
import com.appcenter.uniclub.model.RecruitStatus
import com.appcenter.uniclub.ui.util.figmaPadding
import com.appcenter.uniclub.ui.util.figmaSize
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.ClubCategory
import com.appcenter.uniclub.ui.util.figmaTextSizeSp

//동아리 리스트 화면의 동아리 카드
@Composable
fun ClubCard(club: Club, onClick: () -> Unit) {
    //즐겨찾기 상태 기억
    var isLiked by remember { mutableStateOf(false) }

    //카드 컨테이너
    Box(
        modifier = Modifier
            .figmaSize(widthPx = 350f, heightPx = 85f)
            .clip(RoundedCornerShape(28.dp))
            .clickable { onClick() },
    ) {
        //배경 이미지
        Image(
            painter = painterResource(id = R.drawable.club_card_bg), //배경 이미지 리소스
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize() //Box 전체 채우기
                .clip(RoundedCornerShape(28.dp))
        )

        //카드 내부 콘텐츠
        Row(
            verticalAlignment = Alignment.CenterVertically, //수직 중앙
            modifier = Modifier
                .fillMaxSize()
                .figmaPadding(startPx = 20f, endPx = 30f)
        ) {
            //동아리 프로필 이미지 영역
            //기본 이미지 여부 판단
            val isDefaultImage = club.imageResId == null || club.imageResId == R.drawable.default_club_image

            Image(
                painter = painterResource(id = club.imageResId ?: R.drawable.default_club_image),
                contentDescription = null,
                modifier = if (isDefaultImage) {
                    Modifier
                        .figmaSize(widthPx = 54f, heightPx = 59f) //기본 이미지일 때 더 큰 사이즈
                        .offset(y = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                } else {
                    Modifier
                        .figmaSize(widthPx = 54f, heightPx = 53f) //일반 동아리 이미지
                        .clip(RoundedCornerShape(20.dp))
                },
                contentScale = ContentScale.Crop,
                alignment = Alignment.BottomCenter
            )

            Spacer(modifier = Modifier.width(20.dp))

            //동아리 이름, 추가정보 영역
            Column(
                modifier = Modifier
                    .weight(1f)
                    .offset(y = (-3).dp),
                verticalArrangement = Arrangement.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text( //동아리 이름
                            text = club.name,
                            fontSize = figmaTextSizeSp(14f),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(7.dp))

                        Image( //카테고리 이미지
                            painter = painterResource(id = club.category.getIconRes()),
                            contentDescription = club.category.toDisplayName(),
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .scale(1.1f)
                        )
                    }
                    Text( //추가정보
                        text = club.description,
                        fontSize = figmaTextSizeSp(9f),
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            //즐겨찾기, 모집상태 영역
            Column(
                horizontalAlignment = Alignment.End, //우측 정렬
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(25.dp))
                // 즐겨찾기 아이콘
                Box(
                    modifier = Modifier
                        .figmaSize(widthPx = 14f, heightPx = 12f) //고정된 박스 크기
                        .clickable { isLiked = !isLiked },
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(
                            id = if (isLiked) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
                        ),
                        contentDescription = if (isLiked) "즐겨찾기 취소" else "즐겨찾기",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(if (isLiked) 1.6f else 1f) //빨간 하트 확대
                            .offset( //빨간 하트 위치조정
                                x = if (isLiked) (-0.5).dp else 0.dp,
                                y = if (isLiked) (-0.5).dp else 0.dp
                            )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                //모집 상태에 따른 이미지 설정
                val statusImageRes = when (club.isRecruiting) {
                    RecruitStatus.RECRUITING -> R.drawable.label_recruiting
                    RecruitStatus.UPCOMING -> R.drawable.label_upcoming
                    RecruitStatus.CLOSED -> R.drawable.label_closed
                }
                //모집 상태에 따라 사이즈 조정
                val statusModifier = if (club.isRecruiting == RecruitStatus.RECRUITING) {
                    Modifier
                        .figmaSize(widthPx = 46f, heightPx = 31f) // 모집중일 때 더 큼
                        .padding(bottom = 6.dp) // 아래 위치 고정
                } else {
                    Modifier
                        .figmaSize(widthPx = 43f, heightPx = 28f)
                        .padding(bottom = 6.dp)
                }
                //모집 상태 이미지 출력 (오른쪽 하단)
                Image(
                    painter = painterResource(id = statusImageRes),
                    contentDescription = "모집 상태",
                    modifier = statusModifier
                )
            }
        }
    }
}

fun ClubCategory.toDisplayName(): String = when (this) {
    ClubCategory.ACADEMIC -> "교양학술"
    ClubCategory.HOBBY -> "취미전시"
    ClubCategory.SPORTS -> "체육"
    ClubCategory.RELIGION -> "종교"
    ClubCategory.VOLUNTEER -> "봉사"
    ClubCategory.CULTURE -> "문화"
}

fun ClubCategory.getIconRes(): Int = when (this) {
    ClubCategory.ACADEMIC -> R.drawable.academic
    ClubCategory.HOBBY -> R.drawable.hobby
    ClubCategory.SPORTS -> R.drawable.sports
    ClubCategory.RELIGION -> R.drawable.religion
    ClubCategory.VOLUNTEER -> R.drawable.volunteer
    ClubCategory.CULTURE -> R.drawable.culture
}

@Preview(showBackground = true)
@Composable
fun ClubCardPreview() {
    val sampleClub = Club(
        name = "appcenter",
        description = "앱 개발 동아리",
        isRecruiting = RecruitStatus.RECRUITING,
        isLiked = true,
        category = ClubCategory.ACADEMIC,
        imageResId = R.drawable.club1
    )

    ClubCard(club = sampleClub, onClick = {})
}