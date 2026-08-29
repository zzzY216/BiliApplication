package com.software.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QrPollData(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: QrPollDataData
)

@Serializable
data class QrPollDataData(
    val url: String?,
    @SerialName("refresh_token") val refreshToken: String?,
    val timestamp: Long,
    val code: Int,
    val message: String
)
