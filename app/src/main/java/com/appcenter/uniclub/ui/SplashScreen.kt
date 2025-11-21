package com.appcenter.uniclub.ui

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.appcenter.uniclub.App

@Composable
fun SplashScreen(navController: NavController) {
    val app = LocalContext.current.applicationContext as App
    var isLoading by remember { mutableStateOf(true) }
    var hasToken by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val token = app.tokenStore.getAuthHeader()
        hasToken = !token.isNullOrBlank()
        isLoading = false
    }

    if (!isLoading) {
        if (hasToken) {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}