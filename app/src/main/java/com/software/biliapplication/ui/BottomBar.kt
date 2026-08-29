package com.software.biliapplication.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.software.core.model.navigation.DiscoverRoute
import com.software.core.model.navigation.HomeRoute
import com.software.core.model.navigation.LibraryRoute
import com.software.core.model.navigation.ProfileRoute

/**
 * 一级 Tab 目的地（底部导航的契约数据）。
 * 路由使用 core:model 的类型安全路由对象。
 */
data class TopLevelDestination(
    val route: Any,
    val label: String,
    val icon: ImageVector,
) {
    companion object {
        val all = listOf(
            TopLevelDestination(HomeRoute, "首页", Icons.Default.Home),
            TopLevelDestination(DiscoverRoute, "推荐", Icons.Default.PlayArrow),
            TopLevelDestination(LibraryRoute, "收藏", Icons.Default.List),
            TopLevelDestination(ProfileRoute, "我的", Icons.Default.Person),
        )
    }
}

/**
 * 底部导航栏（原 BottomBar 修复版）：
 * 1. 修正了 4 个 Tab 中 3 个都指向 DiscoverRoute 的路由错误；
 * 2. currentBackStackEntryAsState 提到循环外，避免每项重复订阅；
 * 3. popUpTo 使用 findStartDestination()，配合 saveState/restoreState 保留各 Tab 状态。
 */
@Composable
fun BottomBar(
    navController: NavHostController,
    destinations: List<TopLevelDestination> = TopLevelDestination.all,
    modifier: Modifier = Modifier,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    NavigationBar(modifier = modifier) {
        destinations.forEach { destination ->
            val isSelected = currentDestination?.hasRoute(destination.route::class) == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label,
                    )
                },
                label = {
                    Text(text = destination.label)
                },
                alwaysShowLabel = false
            )
        }
    }
}
