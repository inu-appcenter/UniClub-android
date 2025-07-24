package com.appcenter.uniclub.home.clublist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.appcenter.uniclub.components.TopBar

@Composable
fun ClubListScreen(categoryName: String = "전체") {
    var selectedSort by remember { mutableStateOf("이름순") }

    Column(modifier = Modifier.fillMaxSize()) {
        TopBar()

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ 카테고리 텍스트 반영
            Text(
                text = categoryName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            SortDropdown(
                selectedOption = selectedSort,
                onOptionSelected = { selectedSort = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(dummyClubs.size) { index ->
                ClubCardList(club = dummyClubs[index])
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClubListScreenPreview() {
    ClubListScreen()
}