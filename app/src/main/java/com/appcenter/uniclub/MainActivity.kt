package com.appcenter.uniclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appcenter.uniclub.ui.theme.UniClubTheme
import com.appcenter.uniclub.ui.home.HomeScreen
import com.appcenter.uniclub.ui.components.BottomNavigationBar
import com.appcenter.uniclub.ui.home.clublist.ClubListScreen
import com.appcenter.uniclub.ui.login.LoginScreen
import com.appcenter.uniclub.ui.mypage.MypageScreen
import com.appcenter.uniclub.ui.notification.NotificationScreen
import com.appcenter.uniclub.ui.promotion.UserPromotionScreen
import com.appcenter.uniclub.ui.qna.QnAScreen
import com.appcenter.uniclub.ui.search.SearchScreen
import com.appcenter.uniclub.ui.signup.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            UniClubTheme {
                val navController = rememberNavController()
                NavHost(
                    navController,
                    startDestination = "login",
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onSignUpClick = { navController.navigate("signup") }
                        )
                    }

                    composable("signup") { SignUpScreen()  }

                    composable("main") {
                            MainScaffold(navController)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScaffold(rootNavController: NavHostController) {
    val bottomNavController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            content = { innerPadding ->
                Box(modifier = Modifier
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars)) {
                    NavHost(
                        navController = bottomNavController,
                        startDestination = "home"
                    ) {
                        composable("qna")      { QnAScreen() }
                        composable("home")     { HomeScreen(navController = bottomNavController) }
                        composable("mypage")   { MypageScreen() }
                        composable("clublist/{categoryName}",
                            arguments = listOf(navArgument("categoryName") {
                                type = NavType.StringType
                                defaultValue = "전체"
                            })
                        ) { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("categoryName") ?: "전체"
                            ClubListScreen(navController = bottomNavController, categoryName = category)
                        }
                        composable("promotion") { UserPromotionScreen(navController = bottomNavController) }
                        composable("search") { SearchScreen(navController = bottomNavController) }
                        composable("notification") { NotificationScreen() }
                    }
                }
            }
        )

        // ✅ bottomBar를 content 외부에 위치시킴
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentSize()
        ) {
            BottomNavigationBar(navController = bottomNavController)
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
