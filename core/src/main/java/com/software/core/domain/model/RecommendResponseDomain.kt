package com.software.biliapp.domain.model

data class RecommendResponseDomain(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: RecommendDataDomain
)

data class RecommendDataDomain(
    val config: RecommendConfigDomain? = null,
    val items: List<RecommendItemDomain>
)

data class RecommendItemDomain(
    val idx: Long,
    val cover: String,
    val title: String,
    val uri: String,
    val coverLeft1ContentDescription: String?, // 观看次数
    val coverLeft2ContentDescription: String?,// 弹幕数据
    val coverRightContentDescription: String?, // 时长
    val playerArgs: PlayerArgsDomain?
)
data class PlayerArgsDomain(
    val aid: Long?,
    val cid: Long?
)

data class RecommendConfigDomain(
    val column: Int?,
    val autoplayCard: Int?,
    val feedCleanAbtest: Int?,
    val homeTransferAbtest: Int?,
    val autoRefreshTime: Int?,
    val showInlineDanmaku: Int?,
)
