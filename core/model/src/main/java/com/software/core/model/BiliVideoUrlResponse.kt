package com.software.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiliVideoUrlResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T?
)

@Serializable
data class PlayUrlData(
    val quality: Int,
    val format: String,
    val timelength: Long,
    val durl: List<VideoDUrl>,
    @SerialName("accept_description") val acceptDescription: List<String>? = emptyList(),
    @SerialName("accept_quality") val acceptQuality: List<Int> = emptyList()
)

@Serializable
data class VideoDUrl(
    val order: Int,
    val length: Long,
    val size: Long,
    val url: String,
    @SerialName("backup_url") val backupUrl: List<String>? = emptyList()
)
