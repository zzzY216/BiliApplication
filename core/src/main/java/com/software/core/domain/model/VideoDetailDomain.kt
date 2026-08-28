package com.software.biliapp.domain.model

data class VideoDetailDomain(
    val bvid: String,
    val owner: VideoOwnerDomain,
    val stat: VideoStatDomain
)

data class VideoStatDomain(
    val view: Int, // 播放量
    val like: Int, // 点赞数
    val coin: Int, // 投币数
    val favorite: Int, // 收藏数
    val share: Int, // 分享数
    val danmaku: Int, // 弹幕数
    val reply: Int // 评论数
)

data class VideoOwnerDomain(
    val mid: Long,
    val name: String,
    val face: String
)