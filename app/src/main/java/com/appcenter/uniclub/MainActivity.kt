package com.appcenter.uniclub

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
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
import com.appcenter.uniclub.ui.SplashScreen
import com.appcenter.uniclub.ui.components.BottomNavigationBar
import com.appcenter.uniclub.ui.home.HomeScreen
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
import com.appcenter.uniclub.ui.qna.ClubSelectScreen
import com.appcenter.uniclub.ui.qna.QnAScreen
import com.appcenter.uniclub.ui.qna.QuestionEditScreen
import com.appcenter.uniclub.ui.qna.QuestionScreen
import com.appcenter.uniclub.ui.search.SearchScreen
import com.appcenter.uniclub.ui.signup.AgreementScreen
import com.appcenter.uniclub.ui.signup.NicknameScreen
import com.appcenter.uniclub.ui.signup.SignUpScreen
import com.appcenter.uniclub.ui.signup.SignUpViewModel
import com.appcenter.uniclub.ui.signup.SignUpViewModelFactory
import com.appcenter.uniclub.ui.theme.UniClubTheme
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

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            val app = application as App
            val repo = ServiceLocator.userRepository(app)
            val fcmRepo = ServiceLocator.fcmRepository(app)

            val rootNavController = rememberNavController()
            LogNavChanges("ROOT_NAV", rootNavController)

            val entry by rootNavController.currentBackStackEntryAsState()
            LaunchedEffect(entry) {
                Log.d("NAV_TRACE", "route=${entry?.destination?.route}")
            }

            //FCM 토큰 등록(로그인된 경우만)
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

            //서버 401 등으로 logoutEvent 발생 시 강제 로그인 화면 이동
            LaunchedEffect(Unit) {
                app.logoutEvent.collect {
                    rootNavController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            val view = LocalView.current
            val isDark = isSystemInDarkTheme()

            SideEffect {
                val window = (view.context as Activity).window

                WindowCompat.getInsetsController(window, view)
                    .isAppearanceLightStatusBars = !isDark
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
                                rootNavController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                    launchSingleTop = true
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

                        LaunchedEffect(clubId) {
                            rootNavController.navigate("promotion/$clubId") {
                                popUpTo("notification_promotion/{clubId}") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }

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

                    composable("alarmSetting") {
                        val app2 = rootNavController.context.applicationContext as App
                        val notificationRepo = ServiceLocator.notificationRepository(app2)
                        AlarmSettingScreen(navController = rootNavController, repository = notificationRepo)
                    }
                    composable("profileEdit") { ProfileEditScreen(navController = rootNavController) }
                    composable("inquiry") { InquiryScreen(navController = rootNavController) }
                    composable("terms") { TermsScreen(navController = rootNavController) }
                    composable("delete") { DeleteAccountScreen(navController = rootNavController) }

                    composable(
                        "clublist/{categoryName}",
                        arguments = listOf(navArgument("categoryName") {
                            type = NavType.StringType
                            defaultValue = "전체"
                        })
                    ) { backStackEntry ->
                        val category = backStackEntry.arguments?.getString("categoryName") ?: "전체"
                        val decodedCategory = Uri.decode(category)

                        ClubListScreen(
                            navController = rootNavController,
                            categoryName = decodedCategory
                        )
                    }

                    composable(
                        "promotion/{clubId}",
                        arguments = listOf(navArgument("clubId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val clubId = backStackEntry.arguments?.getLong("clubId")!!
                        val app = (LocalContext.current.applicationContext as App)

                        UserPromotionScreen(
                            navController = rootNavController,
                            rootNavController = rootNavController,
                            onBackClick = { rootNavController.popBackStack() },
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
                                navController = rootNavController,
                                clubId = clubId
                            )
                        }
                    }

                    composable("search") { SearchScreen(navController = rootNavController) }

                    composable("home") {
                        ScreenWithBottomBar(rootNavController) {
                            HomeScreen(
                                bottomNavController = rootNavController,
                                rootNavController = rootNavController
                            )
                        }
                    }

                    composable("mypage") {
                        ScreenWithBottomBar(rootNavController) {
                            MypageScreen(
                                navController = rootNavController,
                                rootNavController = rootNavController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenWithBottomBar(
    rootNavController: NavHostController,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            content()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .wrapContentSize()
        ) {
            BottomNavigationBar(
                navController = rootNavController
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
