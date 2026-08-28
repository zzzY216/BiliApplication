package com.software.biliapp.domain.model

data class PopularDataDomain(
    val list: List<PopularItemDomain>,
    val noMore: String
)


data class PopularItemDomain(
    val aid: String,
    val cid: String,
    val videos: Int,
    val tname: String,
    val pic: String,
    val title: String,
    val owner: PopularDataOwnerDomain,
    val stat: PopularDataStatDomain
)


data class PopularDataOwnerDomain(
    val mid: Long,
    val name: String,
    val face: String
)

data class PopularDataStatDomain(
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
