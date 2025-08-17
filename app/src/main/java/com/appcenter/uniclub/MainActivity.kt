package com.appcenter.uniclub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appcenter.uniclub.data.AuthRepository
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.ui.theme.UniClubTheme
import com.appcenter.uniclub.ui.home.HomeScreen
import com.appcenter.uniclub.ui.components.BottomNavigationBar
import com.appcenter.uniclub.ui.home.clublist.ClubListScreen
import com.appcenter.uniclub.ui.login.LoginScreen
import com.appcenter.uniclub.ui.login.LoginViewModel
import com.appcenter.uniclub.ui.mypage.MypageScreen
import com.appcenter.uniclub.ui.notification.NotificationScreen
import com.appcenter.uniclub.ui.notification.NotificationViewModel
import com.appcenter.uniclub.ui.notification.NotificationViewModelFactory
import com.appcenter.uniclub.ui.promotion.AdminPromotionScreen
import com.appcenter.uniclub.ui.promotion.UserPromotionScreen
import com.appcenter.uniclub.ui.qna.QnAScreen
import com.appcenter.uniclub.ui.search.SearchScreen
import com.appcenter.uniclub.ui.signup.SignUpScreen
import com.appcenter.uniclub.ui.signup.SignUpViewModel

class MainActivity : ComponentActivity() {
    private val notificationVm: NotificationViewModel by viewModels {
        NotificationViewModelFactory(
            ServiceLocator.authService(application as App).let { authService ->
                AuthRepository(authService, (application as App).tokenStore)
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            val app = application as App
            val authService = ServiceLocator.authService(app) // AuthService 생성
            val repo = AuthRepository(authService, app.tokenStore) // Repository 생성
            UniClubTheme {
                val navController = rememberNavController()
                NavHost(
                    navController,
                    startDestination = "login",
                ) {
                    composable("login") {
                        val vm = remember { LoginViewModel(repo) }
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onSignUpClick = { navController.navigate("signup") },
                            vm = vm
                        )
                    }

                    composable("signup") {
                        val vm = remember { SignUpViewModel(repo) }
                        SignUpScreen(
                            onFinished = { navController.popBackStack() },
                            vm = vm
                        )
                    }

                    composable("notification") { backStackEntry ->
                        NotificationScreen(
                            navController = navController,
                            vm = notificationVm
                        )
                    }

                    composable("notification_promotion") {
                        MainScaffold(
                            rootNavController = navController,
                            startDestination = "promotion",
                            onBackToNotification = {
                                navController.popBackStack("notification", false)
                            }
                        )
                    }
                    composable("notification_qna") {
                        MainScaffold(
                            rootNavController = navController,
                            startDestination = "qna",
                            onBackToNotification = {
                                navController.popBackStack("notification", false)
                            }
                        )
                    }

                    composable("main") {
                            MainScaffold(navController, startDestination = "home")
                    }
                }
            }
        }
    }
}

@Composable
fun MainScaffold(
    rootNavController: NavHostController,
    startDestination: String,
    onBackToNotification: (() -> Unit)? = null
) {
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
                        startDestination = startDestination
                    ) {
                        composable("qna")      { QnAScreen() }
                        composable("home")     { HomeScreen(navController = bottomNavController, rootNavController = rootNavController) }
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
                        composable("promotion") {
                            UserPromotionScreen(
                                navController = bottomNavController,
                                onBackClick = {
                                    if (onBackToNotification != null) {
                                        // 알림에서 들어왔을 때
                                        onBackToNotification()
                                    } else {
                                        // 홈/클럽리스트에서 들어왔을 때
                                        bottomNavController.popBackStack()
                                    }
                                }
                            )
                        }
                        composable("admin_promotion") { AdminPromotionScreen(navController = bottomNavController) }
                        composable("search") { SearchScreen(navController = bottomNavController) }
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


//
//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    val navController = rememberNavController()  // 📌 더미 NavController 생성
//
//    UniClubTheme {
//        Scaffold(
//            bottomBar = { BottomNavigationBar(navController = navController) }
//        ) { innerPadding ->
//            HomeScreen(
//                modifier = Modifier.padding(innerPadding),
//                navController = navController    // 📌 넘겨주기
//            )
//        }
//    }
//}
