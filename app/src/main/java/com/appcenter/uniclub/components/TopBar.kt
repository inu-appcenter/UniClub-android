package com.appcenter.uniclub.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun TopBar(
    onBackClick: () -> Unit = {},
    rightIcon: ImageVector = Icons.Default.Search,
    onRightIconClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "뒤로가기",
            modifier = Modifier.clickable { onBackClick() }
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = rightIcon,
            contentDescription = "오른쪽 아이콘",
            modifier = Modifier.clickable { onRightIconClick() }
        )
    }
}
