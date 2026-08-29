package com.software.core.model

import kotlinx.serialization.Serializable

@Serializable
data class HasLikeVideoData(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: Int
)
