package com.software.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.core.navigation.LoginRoute

fun NavGraphBuilder.login(
    onLoginSuccess:()-> Unit
) {
    composable<LoginRoute> {
        BiliLoginScreen(
            onNavigateToMain = onLoginSuccess
        )
    }
}