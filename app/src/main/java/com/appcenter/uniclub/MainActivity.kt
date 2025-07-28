package com.appcenter.uniclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appcenter.uniclub.ui.theme.UniClubTheme
import com.appcenter.uniclub.ui.home.HomeScreen
import com.appcenter.uniclub.ui.components.bottombar.BottomNavigationBar
import com.appcenter.uniclub.ui.home.clublist.ClubListScreen
import com.appcenter.uniclub.ui.components.bottombar.Navigation
import com.appcenter.uniclub.ui.mypage.MypageScreen
import com.appcenter.uniclub.ui.promotion.UserPromotionScreen
import com.appcenter.uniclub.ui.qna.QnAScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UniClubTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.Transparent,
                    bottomBar = { BottomNavigationBar(navController=navController) } //하단바
                ) { innerPadding ->
                    NavHost(
                        navController  = navController,
                        startDestination = Navigation.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 📌 홈 화면
                        composable(Navigation.QnA.route)    { QnAScreen() }
                        composable(Navigation.Home.route)   { HomeScreen(modifier = Modifier,
                                                                        navController = navController) }
                        composable(Navigation.MyPage.route) { MypageScreen() }
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

                        composable("promotion") {
                            UserPromotionScreen(navController = navController)
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
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { innerPadding ->
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
                navController = navController    // 📌 넘겨주기
            )
        }
    }
}
