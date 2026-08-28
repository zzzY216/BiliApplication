package com.software.core.network.model

import com.software.biliapp.domain.model.PlayUrlDataDomain
import com.software.biliapp.domain.model.VideoDUrlDomain
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
    @SerialName("accept_description") val acceptDescription: List<String>?= emptyList(),
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


fun PlayUrlData.toDomain(): PlayUrlDataDomain {
    return PlayUrlDataDomain(
        quality = quality,
        format = format,
        timelength = timelength,
        durl = durl.map { it.toDomain() },
        acceptDescription = acceptDescription,
        acceptQuality = acceptQuality
    )
}

fun VideoDUrl.toDomain(): VideoDUrlDomain {
    return VideoDUrlDomain(
        order = order,
        length = length,
        size = size,
        url = url,
        backupUrl = backupUrl
    )
}
