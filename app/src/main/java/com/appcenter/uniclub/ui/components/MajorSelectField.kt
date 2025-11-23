package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.R
import com.appcenter.uniclub.model.Major
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaTextSizeSp

//학과 선택 버튼 (회원가입, 프로필 수정)
@Composable
fun MajorSelectButton(
    selectedMajor: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isFocused: Boolean = false
) {
    Box(
        modifier = modifier
            .background(Color(0xFFF3F3F3), RoundedCornerShape(13.dp))
            .border( //포커스 시 주황 테두리
                width = 1.dp,
                color = if (isFocused) Color(0xFFFF5900) else Color.Transparent,
                shape = RoundedCornerShape(13.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text( //선택된 학과 이름
                text = selectedMajor,
                fontSize = figmaTextSizeSp(12f),
                fontFamily = NotoSansKR,
                lineHeight = 12.sp * 1.5f,
                letterSpacing = (-0.011).em,
                modifier = Modifier.weight(1f)
            )
            Icon( //아래 화살표 아이콘
                painter = painterResource(R.drawable.ic_arrow_down),
                contentDescription = null,
                tint = if (isFocused) Color(0xFFFF5900) else Color.Gray
            )
        }
    }
}

//학과 선택 BottomSheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MajorSelectBottomSheet(
    onDismiss: () -> Unit,
    onSelect: (Major) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    //탭 상태: true = 학부생, false = 대학원생
    var isUndergraduate by remember { mutableStateOf(true) }

    //학부, 대학원 Major 분류
    val filteredMajors = if (isUndergraduate) {
        Major.values().filter { !it.college.name.startsWith("GRAD_") }
    } else { //대학원은 GRAD_로 시작하는 College
        Major.values().filter { it.college.name.startsWith("GRAD_") }
    }

    val grouped = filteredMajors.groupBy { it.college }.toList() //단과대 기준으로 그룹핑
    val chunks = grouped.chunked(2) //단과대 2개를 묶어 한줄에

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {

            //상단 탭 (학부생, 대학원생)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 35.dp)
                        .padding(top = 20.dp, bottom = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //학부생 탭
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { isUndergraduate = true }
                    ) {
                        MajorSegmentTab(
                            label = "학부생",
                            selected = isUndergraduate,
                        )
                    }

                    Spacer(modifier = Modifier.width(45.dp)) //탭 사이 간격

                    Box( //대학원생 탭
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { isUndergraduate = false }
                    ) {
                        MajorSegmentTab(
                            label = "대학원생",
                            selected = !isUndergraduate,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp)) //탭 아래 간격
            }

            //학과 리스트 순서대로 출력
            chunks.forEachIndexed { chunkIndex, pair ->

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 35.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        //왼쪽 단과대
                        Column(modifier = Modifier.weight(1f)) {
                            val (leftCollege, leftMajors) = pair[0]

                            Text( //단과대
                                text = leftCollege.displayName,
                                color = Color(0xFFFF5900),
                                fontWeight = FontWeight.Medium,
                                fontSize = figmaTextSizeSp(14f),
                                fontFamily = NotoSansKR,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            //전공 목록 하나씩 출력
                            leftMajors.forEach { major ->
                                Text(
                                    text = major.displayName,
                                    fontSize = figmaTextSizeSp(14f),
                                    fontFamily = NotoSansKR,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            onSelect(major)
                                            onDismiss()
                                        }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(45.dp))

                        //오른쪽 단과대
                        Column(modifier = Modifier.weight(1f)) {
                            if (pair.size > 1) {
                                val (rightCollege, rightMajors) = pair[1]

                                Text(
                                    text = rightCollege.displayName,
                                    color = Color(0xFFFF5900),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = figmaTextSizeSp(14f),
                                    fontFamily = NotoSansKR,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                rightMajors.forEach { major ->
                                    Text(
                                        text = major.displayName,
                                        fontSize = figmaTextSizeSp(14f),
                                        fontFamily = NotoSansKR,
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = null
                                            ) {
                                                onSelect(major)
                                                onDismiss()
                                            }
                                    )
                                }
                            }
                        }
                    }
                }

                //단과대 구분선 (마지막 제외)
                if (chunkIndex != chunks.lastIndex) {
                    item {
                        Divider(
                            color = Color(0xFFD9D9D9),
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp, vertical = 20.dp)
                        )
                    }
                }
            }
        }
    }
}

//상단 탭
@Composable
private fun MajorSegmentTab(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    //선택 시 주황색, 비선택 시 회색
    val highlightColor = if (selected) Color(0xFFFF5900) else Color(0xFFD9D9D9)
    val textColor = if (selected) Color(0xFFFF5900) else Color(0xFF7E7E7E)
    val fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text( //탭 라벨
            text = label,
            fontSize = 14.sp,
            fontFamily = NotoSansKR,
            fontWeight = fontWeight,
            letterSpacing = (-0.011).em,
            color = textColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box( //탭 바
            modifier = Modifier
                .size(120.dp, 4.dp)
                .background(highlightColor, RoundedCornerShape(50))
        )
    }
}