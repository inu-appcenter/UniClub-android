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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.appcenter.uniclub.di.ServiceLocator
import com.appcenter.uniclub.ui.theme.UniClubTheme
import com.appcenter.uniclub.ui.home.HomeScreen
import com.appcenter.uniclub.ui.components.BottomNavigationBar
import com.appcenter.uniclub.ui.home.clublist.ClubListScreen
import com.appcenter.uniclub.ui.login.LoginScreen
import com.appcenter.uniclub.ui.login.LoginViewModel
import com.appcenter.uniclub.ui.mypage.AlarmSettingScreen
import com.appcenter.uniclub.ui.mypage.DeleteAccountScreen
import com.appcenter.uniclub.ui.mypage.InquiryScreen
import com.appcenter.uniclub.ui.mypage.MypageScreen
import com.appcenter.uniclub.ui.mypage.ProfileEditScreen
import com.appcenter.uniclub.ui.mypage.TermsScreen
import com.appcenter.uniclub.ui.notification.NotificationScreen
import com.appcenter.uniclub.ui.notification.NotificationViewModel
import com.appcenter.uniclub.ui.notification.NotificationViewModelFactory
import com.appcenter.uniclub.ui.promotion.AdminPromotionScreen
import com.appcenter.uniclub.ui.promotion.UserPromotionScreen
import com.appcenter.uniclub.ui.qna.QnAScreen
import com.appcenter.uniclub.ui.search.SearchScreen
import com.appcenter.uniclub.ui.signup.AgreementScreen
import com.appcenter.uniclub.ui.signup.SignUpScreen
import com.appcenter.uniclub.ui.signup.SignUpViewModel
import com.appcenter.uniclub.ui.signup.SignUpViewModelFactory
import androidx.compose.ui.platform.LocalContext
import com.appcenter.uniclub.ui.qna.ClubSelectScreen
import com.appcenter.uniclub.ui.qna.QuestionEditScreen
import com.appcenter.uniclub.ui.qna.QuestionScreen
import com.appcenter.uniclub.ui.signup.NicknameScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val notificationVm: NotificationViewModel by viewModels {
        NotificationViewModelFactory(
            ServiceLocator.notificationRepository(application as App)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        setContent {
            val app = application as App
            val repo = ServiceLocator.userRepository(app)
            val navController = rememberNavController()

            //logoutEvent 구독 → 로그인 화면으로 이동
            LaunchedEffect(Unit) {
                app.logoutEvent.collect {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            UniClubTheme {
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

                    composable("signup") { backStackEntry ->
                        val vm: SignUpViewModel = viewModel(
                            factory = SignUpViewModelFactory(repo),
                            viewModelStoreOwner = backStackEntry
                        )
                        SignUpScreen(
                            onBack = { navController.popBackStack() },
                            onNext = { navController.navigate("nickname") },
                            vm = vm
                        )
                    }

                    composable("nickname") {
                        // signup 에서 만든 ViewModel 재사용
                        val parentEntry = remember(navController) {
                            try { navController.getBackStackEntry("signup") }
                            catch (e: IllegalArgumentException) { null }
                        }

                        if (parentEntry != null) {
                            val vm: SignUpViewModel = viewModel(
                                factory = SignUpViewModelFactory(repo),
                                viewModelStoreOwner = parentEntry
                            )
                            NicknameScreen(
                                onBack = { navController.popBackStack() },
                                onNext = { navController.navigate("agreement") },
                                vm = vm
                            )
                        } else {
                            // signup 이 없으면 fallback 처리
                            navController.navigate("signup") {
                                popUpTo("nickname") { inclusive = true }
                            }
                        }
                    }

                    composable("agreement") {
                        // signup 화면에서 만든 ViewModel 재사용
                        val parentEntry = remember(navController) {
                            try { navController.getBackStackEntry("signup") }
                            catch (e: IllegalArgumentException) { null }
                        }

                        if (parentEntry != null) {
                            val vm: SignUpViewModel = viewModel(
                                factory = SignUpViewModelFactory(repo),
                                viewModelStoreOwner = parentEntry
                            )
                            AgreementScreen(
                                onBack = { navController.popBackStack() },
                                onFinished = {
                                    navController.navigate("login") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                },
                                vm = vm
                            )
                        } else {
                            // signup이 없으면 fallback 처리 (예: 다시 signup으로 보내기)
                            navController.navigate("signup") {
                                popUpTo("agreement") { inclusive = true }
                            }
                        }
                    }

                    composable("notification") { backStackEntry ->
                        NotificationScreen(
                            navController = navController,
                            vm = notificationVm
                        )
                    }
                    composable(
                        "notification_promotion/{clubId}",
                        arguments = listOf(navArgument("clubId") { type = NavType.LongType })
                    ) { entry ->
                        val clubId = entry.arguments?.getLong("clubId")!!

                        MainScaffold(
                            rootNavController = navController,
                            startDestination = "home",
                            startClubId = clubId,
                            notificationVm = notificationVm,
                            onBackToNotification = { navController.popBackStack("notification", false) }
                        )
                    }

                    composable("qna") { QnAScreen(navController = navController) }
                    composable(
                        route = "question/{questionId}",
                        arguments = listOf(navArgument("questionId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val questionId = backStackEntry.arguments?.getLong("questionId")!!
                        QuestionScreen(navController = navController, questionId = questionId)
                    }
                    composable("questionEdit") {
                        QuestionEditScreen(
                            navController = navController,
                            questionId = 0L,
                            initialClubId = null,
                            initialClubName = "",
                            initialContent = ""
                        )
                    }
                    composable(
                        route = "questionEdit?id={id}&clubName={clubName}&content={content}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.LongType },
                            navArgument("clubName") { type = NavType.StringType; defaultValue = "" },
                            navArgument("content") { type = NavType.StringType; defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("id") ?: 0L
                        val rawClub = backStackEntry.arguments?.getString("clubName") ?: ""
                        val rawContent = backStackEntry.arguments?.getString("content") ?: ""
                        val decodedClub = URLDecoder.decode(rawClub, StandardCharsets.UTF_8.toString())
                        val decodedContent = URLDecoder.decode(rawContent, StandardCharsets.UTF_8.toString())

                        QuestionEditScreen(
                            navController = navController,
                            questionId = id,
                            initialClubId = null,
                            initialClubName = decodedClub,
                            initialContent = decodedContent
                        )
                    }
                    composable("clubSelect") { ClubSelectScreen(navController = navController) }

                    composable("main") {
                            MainScaffold(navController, startDestination = "home", notificationVm = notificationVm)
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
    startClubId: Long? = null,
    onBackToNotification: (() -> Unit)? = null,
    notificationVm: NotificationViewModel
) {
    val bottomNavController = rememberNavController()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            content = { innerPadding ->
                Box(modifier = Modifier
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.navigationBars)) {

                    // 알림에서 들어온 경우 → 홈 그래프 로딩 후 첫 진입 시 이동
                    LaunchedEffect(startClubId) {
                        if (startClubId != null) {
                            bottomNavController.navigate("promotion/$startClubId") {
                                launchSingleTop = true
                            }
                        }
                    }

                    NavHost(
                        navController = bottomNavController,
                        startDestination = startDestination
                    ) {
                        composable("home")     { HomeScreen(bottomNavController = bottomNavController, rootNavController = rootNavController, notificationViewModel = notificationVm) }
                        composable("mypage")   { MypageScreen(navController = bottomNavController, rootNavController = rootNavController) }
                        composable("alarmSetting") {
                            val app = rootNavController.context.applicationContext as App
                            val notificationRepo = ServiceLocator.notificationRepository(app)
                            AlarmSettingScreen(navController = bottomNavController, repository = notificationRepo)
                        }
                        composable("profileEdit") { ProfileEditScreen(navController = bottomNavController) }
                        composable("inquiry") { InquiryScreen(navController = bottomNavController) }
                        composable("terms") { TermsScreen(navController = bottomNavController) }
                        composable("delete") { DeleteAccountScreen(navController = bottomNavController) }
                        composable("clublist/{categoryName}",
                            arguments = listOf(navArgument("categoryName") {
                                type = NavType.StringType
                                defaultValue = "전체"
                            })
                        ) { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("categoryName") ?: "전체"
                            ClubListScreen(navController = bottomNavController, categoryName = category)
                        }
                        composable("promotion/{clubId}", arguments = listOf(navArgument("clubId") { type = NavType.LongType })
                        ) {backStackEntry ->
                            val clubId = backStackEntry.arguments?.getLong("clubId")!!
                            val app = (LocalContext.current.applicationContext as App)

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
                                },
                                clubId = clubId,
                                app = app
                            )
                        }
                        composable(
                            "admin_promotion/{clubId}", arguments = listOf(navArgument("clubId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val clubId = backStackEntry.arguments?.getLong("clubId")
                            if (clubId != null) {
                                AdminPromotionScreen(
                                    navController = bottomNavController,
                                    clubId = clubId
                                )
                            }
                        }
                        composable("search") { SearchScreen(navController = bottomNavController) }
                    }
                }
            }
        )

        //bottomBar를 content 외부에 위치시킴
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentSize()
        ) {
            BottomNavigationBar(
                rootNavController = rootNavController,
                bottomNavController = bottomNavController
            )
        }
    }
}