package com.appcenter.uniclub

import android.os.Bundle
import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
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
import com.appcenter.uniclub.ui.SplashScreen
import com.appcenter.uniclub.ui.qna.ClubSelectScreen
import com.appcenter.uniclub.ui.qna.QuestionEditScreen
import com.appcenter.uniclub.ui.qna.QuestionScreen
import com.appcenter.uniclub.ui.signup.NicknameScreen
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        Log.d("FCM", "MainActivity onCreate reached")
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            val app = application as App
            val repo = ServiceLocator.userRepository(app)
            val fcmRepo = ServiceLocator.fcmRepository(app)

            val rootNavController = rememberNavController()
            LogNavChanges("ROOT_NAV", rootNavController)

            // ✅ 핵심: bottomNavController를 여기서 1번만 생성해서 재사용
            val bottomNavController = rememberNavController()
            LogNavChanges("BOTTOM_NAV", bottomNavController)

            val entry by rootNavController.currentBackStackEntryAsState()
            LaunchedEffect(entry) {
                Log.d("NAV_TRACE", "route=${entry?.destination?.route}")
            }

            LaunchedEffect(Unit) {
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener { token ->
                        Log.d("FCM", "Current token=$token")
                        CoroutineScope(Dispatchers.IO).launch {
                            fcmRepo.registerIfLoggedIn(token)
                                .onFailure { e -> Log.w("FCM", "registerIfLoggedIn failed", e) }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FCM", "Token fetch failed", e)
                    }
            }

            LaunchedEffect(Unit) {
                app.logoutEvent.collect {
                    rootNavController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            UniClubTheme {
                NavHost(
                    navController = rootNavController,
                    startDestination = "splash",
                ) {
                    composable("splash") { SplashScreen(rootNavController) }

                    composable("login") {
                        val vm = remember { LoginViewModel(repo, fcmRepo) }
                        LoginScreen(
                            onLoginSuccess = {
                                rootNavController.navigate("main") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onSignUpClick = { rootNavController.navigate("signup") },
                            vm = vm
                        )
                    }

                    composable("signup") { backStackEntry ->
                        val vm: SignUpViewModel = viewModel(
                            factory = SignUpViewModelFactory(repo),
                            viewModelStoreOwner = backStackEntry
                        )
                        SignUpScreen(
                            navController = rootNavController,
                            onNext = { rootNavController.navigate("nickname") },
                            vm = vm
                        )
                    }

                    composable("nickname") {
                        val parentEntry = remember(rootNavController) {
                            try { rootNavController.getBackStackEntry("signup") }
                            catch (e: IllegalArgumentException) { null }
                        }

                        if (parentEntry != null) {
                            val vm: SignUpViewModel = viewModel(
                                factory = SignUpViewModelFactory(repo),
                                viewModelStoreOwner = parentEntry
                            )
                            NicknameScreen(
                                navController = rootNavController,
                                onNext = { rootNavController.navigate("agreement") },
                                vm = vm
                            )
                        } else {
                            rootNavController.navigate("signup") {
                                popUpTo("nickname") { inclusive = true }
                            }
                        }
                    }

                    composable("agreement") {
                        val parentEntry = remember(rootNavController) {
                            try { rootNavController.getBackStackEntry("signup") }
                            catch (e: IllegalArgumentException) { null }
                        }

                        if (parentEntry != null) {
                            val vm: SignUpViewModel = viewModel(
                                factory = SignUpViewModelFactory(repo),
                                viewModelStoreOwner = parentEntry
                            )
                            AgreementScreen(
                                navController = rootNavController,
                                onFinished = {
                                    rootNavController.navigate("login") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                },
                                vm = vm
                            )
                        } else {
                            rootNavController.navigate("signup") {
                                popUpTo("agreement") { inclusive = true }
                            }
                        }
                    }

                    composable("notification") {
                        NotificationScreen(
                            navController = rootNavController,
                            vm = notificationVm
                        )
                    }

                    composable(
                        "notification_promotion/{clubId}",
                        arguments = listOf(navArgument("clubId") { type = NavType.LongType })
                    ) { entry ->
                        val clubId = entry.arguments?.getLong("clubId")!!

                        MainScaffold(
                            rootNavController = rootNavController,
                            bottomNavController = bottomNavController, // ✅ 주입
                            startDestination = "home",
                            startClubId = clubId,
                            notificationVm = notificationVm,
                            onBackToNotification = { rootNavController.popBackStack("notification", false) }
                        )
                    }

                    // (root 그래프에 있던 qna/question 등은 그대로 둬도 됨)
                    composable("qna") { QnAScreen(navController = rootNavController) }
                    composable(
                        route = "question/{questionId}",
                        arguments = listOf(navArgument("questionId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val questionId = backStackEntry.arguments?.getLong("questionId")!!
                        QuestionScreen(navController = rootNavController, questionId = questionId)
                    }
                    composable("questionEdit") {
                        QuestionEditScreen(
                            navController = rootNavController,
                            questionId = 0L,
                            initialClubId = null,
                            initialClubName = "",
                            initialContent = ""
                        )
                    }
                    composable(
                        route = "questionEditFull?id={id}&clubId={clubId}&clubName={clubName}&content={content}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.LongType },
                            navArgument("clubId") { type = NavType.LongType },
                            navArgument("clubName") { type = NavType.StringType; defaultValue = "" },
                            navArgument("content") { type = NavType.StringType; defaultValue = "" }
                        )
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getLong("id") ?: 0L
                        val clubId = backStackEntry.arguments?.getLong("clubId") ?: 0L

                        val rawClub = backStackEntry.arguments?.getString("clubName") ?: ""
                        val rawContent = backStackEntry.arguments?.getString("content") ?: ""

                        val decodedClub = URLDecoder.decode(rawClub, StandardCharsets.UTF_8.toString())
                        val decodedContent = URLDecoder.decode(rawContent, StandardCharsets.UTF_8.toString())

                        QuestionEditScreen(
                            navController = rootNavController,
                            questionId = id,
                            initialClubId = clubId,
                            initialClubName = decodedClub,
                            initialContent = decodedContent
                        )
                    }

                    composable("clubSelect") { ClubSelectScreen(navController = rootNavController) }

                    // ✅ profileEdit은 root에 그대로 둠(사용자 요구)
                    composable("alarmSetting") {
                        val app2 = rootNavController.context.applicationContext as App
                        val notificationRepo = ServiceLocator.notificationRepository(app2)
                        AlarmSettingScreen(navController = rootNavController, repository = notificationRepo)
                    }
                    composable("profileEdit") { ProfileEditScreen(navController = rootNavController) }
                    composable("inquiry") { InquiryScreen(navController = rootNavController) }
                    composable("terms") { TermsScreen(navController = rootNavController) }
                    composable("delete") { DeleteAccountScreen(navController = rootNavController) }

                    composable("main") {
                        MainScaffold(
                            rootNavController = rootNavController,
                            bottomNavController = bottomNavController, // ✅ 주입
                            startDestination = "home",
                            notificationVm = notificationVm
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainScaffold(
    rootNavController: NavHostController,
    bottomNavController: NavHostController, // ✅ 주입받기
    startDestination: String,
    startClubId: Long? = null,
    onBackToNotification: (() -> Unit)? = null,
    notificationVm: NotificationViewModel
) {
    // ✅ 여기서 rememberNavController() 만들지 않습니다.

    val bottomEntry = bottomNavController.currentBackStackEntryAsState().value
    LaunchedEffect(bottomEntry) {
        Log.d("NAV", "bottom route=${bottomEntry?.destination?.route}")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
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
                        composable("home") {
                            HomeScreen(
                                bottomNavController = bottomNavController,
                                rootNavController = rootNavController,
                                notificationViewModel = notificationVm
                            )
                        }
                        composable("mypage") {
                            MypageScreen(
                                navController = bottomNavController,
                                rootNavController = rootNavController
                            )
                        }
                        composable(
                            "clublist/{categoryName}",
                            arguments = listOf(navArgument("categoryName") {
                                type = NavType.StringType
                                defaultValue = "전체"
                            })
                        ) { backStackEntry ->
                            val category = backStackEntry.arguments?.getString("categoryName") ?: "전체"
                            ClubListScreen(navController = bottomNavController, categoryName = category)
                        }
                        composable(
                            "promotion/{clubId}",
                            arguments = listOf(navArgument("clubId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val clubId = backStackEntry.arguments?.getLong("clubId")!!
                            val app = (LocalContext.current.applicationContext as App)

                            UserPromotionScreen(
                                navController = bottomNavController,
                                rootNavController = rootNavController,
                                onBackClick = {
                                    if (onBackToNotification != null) onBackToNotification()
                                    else bottomNavController.popBackStack()
                                },
                                clubId = clubId,
                                app = app
                            )
                        }
                        composable(
                            "admin_promotion/{clubId}",
                            arguments = listOf(navArgument("clubId") { type = NavType.LongType })
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

@Composable
private fun LogNavChanges(tag: String, navController: NavController) {
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, dest, args ->
            Log.d(tag, "dest=${dest.route} args=$args")
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose { navController.removeOnDestinationChangedListener(listener) }
    }
}
