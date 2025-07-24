package com.appcenter.uniclub

import android.R.attr.defaultValue
import android.R.attr.type
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appcenter.uniclub.ui.theme.UniClubTheme
import com.appcenter.uniclub.home.HomeScreen
import com.appcenter.uniclub.components.BottomNavigationBar
import com.appcenter.uniclub.home.clublist.ClubListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniClubTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BottomNavigationBar() } //하단바
                ) { innerPadding ->
                    NavHost(
                        navController  = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 📌 홈 화면
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        // 📌 카테고리 클릭 → 동아리 리스트 화면
                        composable(
                            route = "clublist/{categoryName}",
                            arguments = listOf(
                                navArgument("categoryName") {
                                    type = NavType.StringType
                                    defaultValue = "전체"
                                }
                            )
                        ) { backStackEntry ->
                            val category = backStackEntry.arguments
                                ?.getString("categoryName") ?: "전체"
                            ClubListScreen(
                                navController = navController,
                                categoryName = category
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()  // 📌 더미 NavController 생성

    UniClubTheme {
        Scaffold(
            bottomBar = { BottomNavigationBar() }
        ) { innerPadding ->
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
                navController = navController    // 📌 넘겨주기
            )
        }
    }
}