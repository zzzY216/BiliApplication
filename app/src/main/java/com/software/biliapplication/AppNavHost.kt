package com.software.biliapplication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.software.biliapplication.ui.BottomBar
import com.software.biliapplication.ui.PlaceholderScreen
import com.software.biliapplication.ui.TopLevelDestination
import com.software.core.model.navigation.AnimeDetailRoute
import com.software.core.model.navigation.DiscoverRoute
import com.software.core.model.navigation.HomeRoute
import com.software.core.model.navigation.LibraryRoute
import com.software.core.model.navigation.LoginRoute
import com.software.core.model.navigation.ProfileRoute
import com.software.home.homeNavGraph
import com.software.login.login

/**
 * 应用导航骨架：
 * - 登录页（无底部导航）
 * - 主界面：底部导航（首页/推荐/收藏/我的）+ 各 Tab 目的地
 */
@Composable
fun AppNavHost(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = backStackEntry?.destination
            if (currentDestination != null &&
                TopLevelDestination.all.any { currentDestination.hasRoute(it.route::class) }
            ) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = LoginRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            login(
                onLoginSuccess = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            homeNavGraph(
                onSeasonClick = { seasonId ->
                    navController.navigate(AnimeDetailRoute(seasonId)) {
                        launchSingleTop = true
                    }
                }
            )
            composable<DiscoverRoute> { PlaceholderScreen(text = "发现（开发中）") }
            composable<LibraryRoute> { PlaceholderScreen(text = "收藏（开发中）") }
            composable<ProfileRoute> { PlaceholderScreen(text = "我的（开发中）") }
        }
    }
}
