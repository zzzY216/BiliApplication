package com.software.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.software.core.model.navigation.HomeRoute

fun NavGraphBuilder.homeNavGraph() {
    composable<HomeRoute> {
        BiliHomeRoute()
    }
}
