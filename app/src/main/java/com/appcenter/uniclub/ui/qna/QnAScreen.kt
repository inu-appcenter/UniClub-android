package com.appcenter.uniclub.ui.qna

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.appcenter.uniclub.R
import com.appcenter.uniclub.ui.components.TopBar
import com.appcenter.uniclub.ui.theme.NotoSansKR
import com.appcenter.uniclub.util.figmaPadding
import com.appcenter.uniclub.util.figmaSize
import com.appcenter.uniclub.util.figmaTextSizeSp

@Composable
fun QnAScreen(navController: NavHostController) {
//    var query by remember { mutableStateOf("") }
//    var selectedClub by remember { mutableStateOf("") }
//    var checking by remember { mutableStateOf(false) }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Spacer(modifier = Modifier.height(27.dp))
//        TopBar( //상단바
//            onBackClick = { navController.popBackStack() },
//            title = "질의응답"
//        )
//        Spacer(modifier = Modifier.height(26.dp))
//
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .figmaPadding(startPx = 29f, endPx = 29f)
//        ) {
//            Box(modifier = Modifier
//                .figmaSize(widthPx = 303f, heightPx = 35f)
//                .clip(RoundedCornerShape(13.dp))
//            ){
//                Image(
//                    painter = painterResource(R.drawable.bg_qna_search),
//                    contentDescription = null,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier.fillMaxSize()
//                )
//
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    modifier = Modifier
//                        .figmaPadding(startPx = 36f, topPx = 8f, endPx = 13f, bottomPx = 8f)
//                        .fillMaxWidth()
//                ){
//                    BasicTextField(
//                        value = query,
//                        onValueChange = { query = it },
//                        singleLine = true,
//                        modifier = Modifier.weight(1f),
//                        decorationBox = { innerTextField ->
//                            if (query.isEmpty()) {
//                                Text(
//                                    text = "질문을 검색해보세요.",
//                                    fontSize = figmaTextSizeSp(12f),
//                                    fontFamily = NotoSansKR,
//                                    lineHeight = 12.sp * 1.5f,
//                                    letterSpacing = (-0.011).em,
//                                    color = Color(0xFF595959)
//                                )
//                            }
//                            innerTextField() //실제 입력 텍스트
//                        }
//                    )
//
//                    //입력값 있을 때만 지우기 버튼 표시
//                    if (query.isNotEmpty()) {
//                        IconButton(
//                            onClick = { query = "" },
//                            modifier = Modifier.size(18.dp)
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.ic_close),
//                                contentDescription = "검색어 지우기",
//                                tint = Color.Black,
//                                modifier = Modifier.figmaSize(widthPx = 12f, heightPx = 12f)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(26.dp))
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .figmaPadding(startPx = 30f, endPx = 30f),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            ClubDropdown(
//                items = dummyClubs.map { it.name },
//                selectedValue = selectedClub,
//                onItemSelected = { selectedClub = it },
//            )
//            Box(
//                modifier = Modifier
//                    .figmaSize(widthPx = 68f, heightPx = 17f)
//                    .clickable { checking = !checking }
//            ){
//                if(checking){
//                    Image(
//                        painter = painterResource(id = R.drawable.btn_check),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                } else {
//                    Image(
//                        painter = painterResource(id = R.drawable.btn_check_disabled),
//                        contentDescription = null,
//                        contentScale = ContentScale.Crop,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(18.dp))
//    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ClubDropdown(
//    items: List<String>,
//    selectedValue: String,
//    onItemSelected: (String) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Box(modifier = modifier
//            .figmaSize(widthPx = 129f, heightPx = 28f)
//            .background(Color(0xFFFF5900), RoundedCornerShape(13.dp))
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .clip(RoundedCornerShape(10.dp))
//                .fillMaxWidth()
//                .height(40.dp)
//                .clickable { expanded = true }
//                .figmaPadding(startPx = 12f, endPx = 12f)
//        ) {
//            Text(
//                text = if (selectedValue.isEmpty()) "동아리 선택" else selectedValue,
//                fontSize = figmaTextSizeSp(11f),
//                fontFamily = NotoSansKR,
//                lineHeight = 11.sp * 1.5f,
//                letterSpacing = (-0.011).em,
//                color = Color.White,
//                modifier = Modifier.weight(1f)
//            )
//            Icon(
//                imageVector = Icons.Filled.ArrowDropDown,
//                contentDescription = null,
//                tint = Color.White
//            )
//        }
//
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false }
//        ) {
//            items.forEach { club ->
//                DropdownMenuItem(
//                    text = { Text(club) },
//                    onClick = {
//                        onItemSelected(club)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}