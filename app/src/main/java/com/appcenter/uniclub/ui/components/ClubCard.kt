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
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.Club
import com.appcenter.uniclub.model.ClubCategory
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaTextSizeSp

//동아리 리스트 화면의 동아리 카드
@Composable
fun ClubCard(
    club: Club,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
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
                .fillMaxSize()
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
            if (club.profileUrl.isNullOrBlank()) {
                // 기본 이미지
                Image(
                    painter = painterResource(id = R.drawable.default_image),
                    contentDescription = "기본 프로필",
                    modifier = Modifier
                        .figmaSize(widthPx = 54f, heightPx = 59f)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // 서버에서 불러온 이미지
                AsyncImage(
                    model = club.profileUrl,
                    contentDescription = "동아리 프로필",
                    modifier = Modifier
                        .figmaSize(widthPx = 54f, heightPx = 53f)
                        .clip(RoundedCornerShape(20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            //동아리 이름, 추가정보 영역
            Column(
                modifier = Modifier
                    .weight(1f)
                    .offset(y = (-3).dp),
                verticalArrangement = Arrangement.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text( //동아리 이름
                            text = club.name,
                            fontSize = figmaTextSizeSp(14f),
                            fontFamily = NotoSansKR,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp * 1.5f, //행간
                            letterSpacing = (-0.011).em, //자간
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(7.dp))

                        //카테고리 이미지
                        val categoryRes = when (club.category) {
                            ClubCategory.ACADEMIC -> R.drawable.academic
                            ClubCategory.HOBBY -> R.drawable.hobby
                            ClubCategory.SPORTS -> R.drawable.sports
                            ClubCategory.RELIGION -> R.drawable.religion
                            ClubCategory.VOLUNTEER -> R.drawable.volunteer
                            ClubCategory.CULTURE -> R.drawable.culture
                        }
                        Image(
                            painter = painterResource(id = categoryRes),
                            contentDescription = club.category.displayName,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.scale(1.1f)
                        )
                    }
                    Text( //추가정보
                        text = club.info,
                        fontSize = figmaTextSizeSp(9f),
                        fontFamily = NotoSansKR,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 9.sp * 1.39f, //행간
                        letterSpacing = (-0.011).em, //자간
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
                        .clickable { onToggleFavorite() },
                    contentAlignment = Alignment.Center
                ){
                    Image(
                        painter = painterResource(
                            id = if (club.favorite) R.drawable.ic_favorite_filled else R.drawable.ic_favorite
                        ),
                        contentDescription = if (club.favorite) "즐겨찾기 취소" else "즐겨찾기",
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(if (club.favorite) 1.6f else 1f) //빨간 하트 확대
                            .offset( //빨간 하트 위치조정
                                x = if (club.favorite) (-0.5).dp else 0.dp,
                                y = if (club.favorite) (-0.5).dp else 0.dp
                            )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                //모집 상태에 따른 이미지 설정
                val statusImageRes = when (club.status) {
                    "ACTIVE" -> R.drawable.label_recruiting
                    "SCHEDULED" -> R.drawable.label_upcoming
                    "CLOSED" -> R.drawable.label_closed
                    else -> R.drawable.label_closed
                }
                //모집 상태에 따라 사이즈 조정
                val statusModifier = if (club.status == "ACTIVE") {
                    Modifier
                        .figmaSize(widthPx = 46f, heightPx = 31f) //모집중일 때 더 큼
                        .padding(bottom = 6.dp) //아래 위치 고정
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