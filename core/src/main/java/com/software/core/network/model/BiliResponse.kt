package com.software.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class BiliResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int = 0,
    val data: T?
)