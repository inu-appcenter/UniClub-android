package com.appcenter.uniclub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    items: List<String>,
    selectedValue: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        // ✅ BasicTextField로 직접 구현
        Box(
            modifier = Modifier
                .menuAnchor()
                .border(
                    width = 0.dp,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(13.dp)
                )
                .background(Color(0xFFF3F3F3), RoundedCornerShape(13.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp), // 글자 잘리지 않도록 여유
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 14.sp, // 글씨 크기 자유 설정
                    lineHeight = 18.sp,
                    letterSpacing = 0.sp
                ),
                decorationBox = { innerTextField ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // ✅ 텍스트는 가변 영역
                        Box(modifier = Modifier.weight(1f)) {
                            innerTextField()
                        }
                        // ✅ 아이콘은 Row 맨 끝
                        Box(modifier = Modifier.wrapContentSize()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        }
                    }
                }
            )
        }

        // ✅ Dropdown 메뉴
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
                            color = Color.Black, // ✅ 텍스트 색 강제
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        onItemSelected(selectionOption)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color.Black,                // 일반 텍스트 색
                        leadingIconColor = Color.Black,         // 아이콘 색
                        trailingIconColor = Color.Black,
                        disabledTextColor = Color.Gray,         // 비활성화 텍스트 색
                        disabledLeadingIconColor = Color.Gray,
                        disabledTrailingIconColor = Color.Gray
                    )
                )
            }
        }
    }
}
