package com.software.core.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeVideoData(
    val code: Int,
    val message: String,
    val ttl: Int
)
