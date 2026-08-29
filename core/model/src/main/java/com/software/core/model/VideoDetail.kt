package com.software.core.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoDetail(
    val bvid: String,
    val owner: VideoOwner,
    val stat: VideoStat
)

@Serializable
data class VideoStat(
    val view: Int, // 播放量
    val like: Int, // 点赞数
    val coin: Int, // 投币数
    val favorite: Int, // 收藏数
    val share: Int, // 分享数
    val danmaku: Int, // 弹幕数
    val reply: Int // 评论数
)

@Serializable
data class VideoOwner(
    val mid: Long,
    val name: String,
    val face: String
)
