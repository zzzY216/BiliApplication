package com.software.biliapp.data.remote.model

import com.software.biliapp.domain.model.VideoDetailDomain
import com.software.biliapp.domain.model.VideoOwnerDomain
import com.software.biliapp.domain.model.VideoStatDomain
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

fun VideoDetail.toDomain(): VideoDetailDomain {
    return VideoDetailDomain(
        bvid = bvid,
        owner = owner.toDomain(),
        stat = stat.toDomain()
    )
}

fun VideoStat.toDomain(): VideoStatDomain {
    return VideoStatDomain(
        view = view,
        like = like,
        coin = coin,
        favorite = favorite,
        share = share,
        danmaku = danmaku,
        reply = reply
    )
}

fun VideoOwner.toDomain(): VideoOwnerDomain {
    return VideoOwnerDomain(
        mid = mid,
        name = name,
        face = face
    )
}