package com.software.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.software.core.model.navigation.AnimeDetailRoute
import com.software.core.model.navigation.HomeRoute

fun NavGraphBuilder.homeNavGraph(
    onSeasonClick: (Long) -> Unit = {},
) {
    composable<HomeRoute> {
        BiliHomeRoute(onSeasonClick = onSeasonClick)
    }
    composable<AnimeDetailRoute> { backStackEntry ->
        val route = backStackEntry.toRoute<AnimeDetailRoute>()
        AnimeDetailRoute(seasonId = route.seasonId)
    }
}
