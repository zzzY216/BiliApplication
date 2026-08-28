package com.software.core.network.model

import com.software.biliapp.domain.model.HasLikeVideoDataDomain
import kotlinx.serialization.Serializable

@Serializable
data class HasLikeVideoData(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: Int
)

fun HasLikeVideoData.toDomain(): HasLikeVideoDataDomain {
    return HasLikeVideoDataDomain(
        code = code,
        message = message,
        ttl = ttl,
        data = data
    )
}

