package com.software.core.model.navigation

import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object DiscoverRoute

@Serializable
object HomeRoute

@Serializable
object LibraryRoute

@Serializable
object ProfileRoute

/** 动漫详情页（番剧/国创） */
@Serializable
data class AnimeDetailRoute(val seasonId: Long)
