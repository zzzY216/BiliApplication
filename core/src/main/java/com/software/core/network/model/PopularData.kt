package com.software.biliapp.data.remote.model

import com.software.biliapp.domain.model.PopularDataDomain
import com.software.biliapp.domain.model.PopularDataOwnerDomain
import com.software.biliapp.domain.model.PopularDataStatDomain
import com.software.biliapp.domain.model.PopularItemDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularData(
    val list: List<PopularItem>,
    @SerialName("no_more") val noMore: String,
)

@Serializable
data class PopularItem(
    val aid: String,
    val cid: String,
    val videos: Int,
    val tname: String,
    val pic: String,
    val title: String,
    val owner: PopularDataOwner,
    val stat: PopularDataStat
)

@Serializable
data class PopularDataOwner(
    val mid: Long,
    val name: String,
    val face: String
)

@Serializable
data class PopularDataStat(
    val view: Int, // 播放量
    val danmaku: Int, // 弹幕数
    val reply: Int, // 评论数
    val favorite: Int, // 收藏数
    val coin: Int, // 投币数
    val share: Int, // 分享数
    val like: Int, // 点赞数
    val dislike: Int, //点踩数
    val vv: Int // 有效播放量
)

fun PopularData.toDomain(): PopularDataDomain {
    return PopularDataDomain(
        list = list.map { it.toDomain() },
        noMore = noMore
    )
}

fun PopularItem.toDomain(): PopularItemDomain {
    return PopularItemDomain(
        aid = aid,
        cid = cid,
        videos = videos,
        tname = tname,
        pic = pic,
        title = title,
        owner = owner.toDomain(),
        stat = stat.toDomain()
    )
}

fun PopularDataOwner.toDomain(): PopularDataOwnerDomain {
    return PopularDataOwnerDomain(
        mid = mid,
        name = name,
        face = face
    )
}

fun PopularDataStat.toDomain(): PopularDataStatDomain {
    return PopularDataStatDomain(
        view = view,
        danmaku = danmaku,
        reply = reply,
        favorite = favorite,
        coin = coin,
        share = share,
        like = like,
        dislike = dislike,
        vv = vv
    )
}