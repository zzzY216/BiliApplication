package com.software.core.network

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BiliLoginNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BiliAppNetwork

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BiliApiNetwork // 专门给 api.bilibili.com 使用