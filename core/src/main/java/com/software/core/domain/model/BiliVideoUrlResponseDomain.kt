package com.software.biliapp.domain.model

data class BiliVideoUrlResponseDomain<T>(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T?
)

data class PlayUrlDataDomain(
    val quality: Int,
    val format: String,
    val timelength: Long,
    val durl: List<VideoDUrlDomain>,
    val acceptDescription: List<String>?,
    val acceptQuality: List<Int>?
)

data class VideoDUrlDomain(
    val order: Int,
    val length: Long,
    val size: Long,
    val url: String,
    val backupUrl: List<String>?
)