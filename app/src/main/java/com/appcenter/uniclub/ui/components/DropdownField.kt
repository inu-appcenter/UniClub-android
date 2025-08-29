package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaTextSizeSp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    items: List<String>, //드롭다운에 표시할 항목 리스트
    selectedValue: String, //현재 선택된 값
    onItemSelected: (String) -> Unit, //아이템 클릭 시 선택된 값 콜백
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } //드롭다운 열림/닫힘 상태 기억

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        //입력 박스
        Box(
            modifier = Modifier
                .menuAnchor()
                .background(Color(0xFFF3F3F3), RoundedCornerShape(13.dp))
                .border(
                    width = 1.dp,
                    color = if (expanded) Color(0xFFFF5900) else Color.Transparent,
                    shape = RoundedCornerShape(13.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp), //글자 잘리지 않도록 좌우상하 패딩
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = selectedValue, //현재 선택된 값 출력
                onValueChange = {},
                readOnly = true, //직접 입력 막고 선택만 허용
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = figmaTextSizeSp(12f),
                    fontFamily = NotoSansKR,
                    lineHeight = 12.sp * 1.5f,
                    letterSpacing = (-0.011).em,
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        //텍스트 영역
                        Box(modifier = Modifier.weight(1f)) {
                            innerTextField()
                        }
                        //드롭다운 아이콘 Row 끝에 고정
                        Box(modifier = Modifier.wrapContentSize()) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null,
                                tint = if (expanded) Color(0xFFFF5900) else Color.Gray
                            )
                        }
                    }
                }
            )
        }

        //드롭다운 메뉴
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFFF3F3F3))
        ) {
            items.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Text(
                            selectionOption,
                            fontSize = figmaTextSizeSp(12f),
                            fontFamily = NotoSansKR,
                            lineHeight = 12.sp * 1.5f,
                            letterSpacing = (-0.011).em,
                            color = Color.Black
                        )
                    },
                    onClick = {
                        onItemSelected(selectionOption) //아이템 선택 시 콜백 실행
                        expanded = false
                    }
                )
            }
        }
    }
}
