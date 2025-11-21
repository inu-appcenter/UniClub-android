package com.appcenter.uniclub.ui.qna

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.TimeUtils
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp

//Q&A 페이지 질문 카드

data class QnaListItem(
    val id: Long,
    val authorName: String,
    val authorProfileUrl: String?,
    val clubName: String,
    val createdAt: String,
    val question: String,
    val hasAnswer: Boolean,
    val answerCount: Int,
    val isMine: Boolean
)

//공통 카드 컨테이너
@Composable
private fun QnaBaseCard(
    modifier: Modifier = Modifier,
    height: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .figmaSize(308f, height.toFloat())
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = Color(0x20000000),
                spotColor = Color(0x20000000)
            )
            .clip(RoundedCornerShape(31.dp))
            .background(Color.White)
            .padding(start = 20.dp, top = 15.dp, end = 20.dp)
    ) {
        Column(content = content)
    }
}

//카드 상단 헤더 영역
@Composable private fun QnaHeader(
    profile: String?,
    name: String, //작성자명
    date: String, //작성일시
    isMine: Boolean, //내 글 여부
    onMenuClick: (Boolean) -> Unit //더보기 버튼 클릭
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = if (!profile.isNullOrBlank())
                rememberAsyncImagePainter(profile)
            else painterResource(R.drawable.default_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(25.dp).clip(RoundedCornerShape(11.dp))
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text( //작성자명
                text = name, fontFamily = NotoSansKR, fontSize = figmaTextSizeSp(11f),
                fontWeight = FontWeight.Medium, lineHeight = 11.sp * 1.5f, letterSpacing = (-0.011).em
            )
            Text( //작성일시
                text = TimeUtils.toFormattedTime(date), fontFamily = NotoSansKR, fontSize = figmaTextSizeSp(9f),
                lineHeight = 9.sp * 1.5f, letterSpacing = (-0.011).em, color = Color(0xFF7D7D7D)
            )
        }

        //더보기 버튼
        val interaction = remember { MutableInteractionSource() }
        Box(
            modifier = Modifier
                .size(13.dp)
                .clickable(
                    interactionSource = interaction,
                    indication = null
                ) { onMenuClick(isMine) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = "더보기",
                tint = Color(0xFF7D7D7D)
            )
        }
    }
}

@Composable private fun ClubTag(club: String) {
    Text(
        text = "@$club", fontFamily = NotoSansKR, fontSize = figmaTextSizeSp(11f),
        fontWeight = FontWeight.Medium, lineHeight = 11.sp * 1.5f, letterSpacing = (-0.011).em, color = Color(0xFFFF5900)
    )
}

//질문 내용 영역
@Composable private fun QuestionText(q: String) {
    Text(
        text = q,
        maxLines = 1, //한 줄만 보이게
        overflow = TextOverflow.Ellipsis, //넘어가면 ... 표시
        fontFamily = NotoSansKR,
        fontSize = figmaTextSizeSp(11f),
        lineHeight = 11.sp * 1.5f,
        letterSpacing = (-0.011).em,
        color = Color(0xFF000000)
    )
}

//댓글 수 카운트
@Composable private fun AnswerCount(count: Int) {
    if (count > 0) {
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_comment),
                contentDescription = null, tint = Color(0xFFFF5900),
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = count.toString(), fontFamily = NotoSansKR, fontWeight = Bold,
                fontSize = figmaTextSizeSp(9f), lineHeight = 9.sp * 1.5f,
                letterSpacing = (-0.011).em, color = Color(0xFFFF5900)
            )
        }
    }
}

//댓글 없는 카드
@Composable
fun QnaCardNoAnswer(
    item: QnaListItem,
    onMenuClick: (Boolean, Long) -> Unit, //(isMine, questionId)
    navController: NavHostController
) {
    QnaBaseCard(
        height = 110,
        modifier = Modifier.clickable( //질문 페이지로 이동
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { navController.navigate("question/${item.id}") }
    ) {
        //헤더
        QnaHeader(item.authorProfileUrl, item.authorName, item.createdAt, item.isMine, onMenuClick = { isMine -> onMenuClick(isMine, item.id) })
        Spacer(modifier = Modifier.height(8.dp))
        Column( //본문 (동아리 이름, 질문 내용)
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.figmaPadding(topPx = 5f, startPx = 28f)
        ) {
            ClubTag(item.clubName)
            QuestionText(item.question)
        }
    }
}

//댓글 있는 카드
@Composable
fun QnaCardAnswered(
    item: QnaListItem,
    onMenuClick: (Boolean, Long) -> Unit,
    navController: NavHostController
) {
    QnaBaseCard(
        height = 140,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { navController.navigate("question/${item.id}") }
    ) {
        QnaHeader(item.authorProfileUrl, item.authorName, item.createdAt, item.isMine, onMenuClick = { isMine -> onMenuClick(isMine, item.id) })
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.figmaPadding(topPx = 5f, startPx = 28f)
        ) {
            ClubTag(item.clubName)
            QuestionText(item.question)
            AnswerCount(item.answerCount)
        }
    }
}