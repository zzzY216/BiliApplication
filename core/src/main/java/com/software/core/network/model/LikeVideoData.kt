package com.software.core.network.model

import com.software.biliapp.domain.model.LikeVideoDataDomain
import kotlinx.serialization.Serializable

@Serializable
data class LikeVideoData(
    val code: Int,
    val message: String,
    val ttl: Int
)

fun LikeVideoData.toDomain(): LikeVideoDataDomain {
    return LikeVideoDataDomain(
        code = code,
        message = message,
        ttl = ttl
    )
}
