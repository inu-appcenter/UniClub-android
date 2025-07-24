package com.appcenter.uniclub.ui.home.clublist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//동아리 리스트 페이지에서 정렬 옵션을 선택할 수 있는 드롭다운 메뉴
@Composable
fun SortDropdown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) } //드롭다운 메뉴의 펼쳐짐 상태 저장
    val options = listOf("이름순", "모집중", "좋아요순") //정렬 옵션 목록

    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        Text(
            text = "$selectedOption ▾", //선택된 옵션 텍스트로 변경
            modifier = Modifier
                .clickable { expanded = true } //클릭 시 메뉴 펼치기
                .padding(8.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
