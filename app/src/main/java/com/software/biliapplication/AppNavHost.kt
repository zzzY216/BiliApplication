package com.software.biliapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.software.core.navigation.LoginRoute
import com.software.login.login

@Composable
fun AppNavHost(navController: NavHostController) {
    Box() {
        NavHost(
            navController = navController,
            startDestination = LoginRoute // 初始页面
        ) {
            login(
                onLoginSuccess = {}
            )
        }
    }
}