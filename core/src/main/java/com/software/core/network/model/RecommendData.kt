package com.software.biliapp.data.remote.model

import com.software.biliapp.domain.model.PlayerArgsDomain
import com.software.biliapp.domain.model.RecommendConfigDomain
import com.software.biliapp.domain.model.RecommendDataDomain
import com.software.biliapp.domain.model.RecommendItemDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RecommendData(
    val config: RecommendConfig? = null,
    val items: List<RecommendItem>
)

@Serializable
data class RecommendItem(
    val idx: Long,
    val cover: String = "",
    val title: String = "",
    val uri: String = "",
    @SerialName("cover_left_1_content_description") val coverLeft1ContentDescription: String? = "", // 观看次数
    @SerialName("cover_left_2_content_description") val coverLeft2ContentDescription: String? = "",// 弹幕数据
    @SerialName("cover_right_content_description") val coverRightContentDescription: String? = "", // 时长
    @SerialName("player_args") val playerArgs: PlayerArgs? = null
)

@Serializable
data class PlayerArgs(
    val cid: Long? = null,
    val aid: Long? = null
)

@Serializable
data class RecommendConfig(
    val column: Int? = null,
    @SerialName("autoplay_card") val autoplayCard: Int? = null,
    @SerialName("feed_clean_abtest") val feedCleanAbtest: Int? = null,
    @SerialName("home_transfer_abtest") val homeTransferAbtest: Int? = null,
    @SerialName("auto_refresh_time") val autoRefreshTime: Int? = null,
    @SerialName("show_inline_danmaku") val showInlineDanmaku: Int? = null,
)

fun RecommendData.toDomain(): RecommendDataDomain {
    return RecommendDataDomain(
        config = config?.toDomain(),
        items = items.map { it.toDomain() }
    )
}

fun RecommendItem.toDomain(): RecommendItemDomain {
    return RecommendItemDomain(
        idx = idx,
        cover = cover,
        title = title,
        uri = uri,
        coverLeft1ContentDescription = coverLeft1ContentDescription ?: "未知观看",
        coverLeft2ContentDescription = coverLeft2ContentDescription ?: "",
        coverRightContentDescription = coverRightContentDescription ?: "00:00",
        playerArgs = playerArgs?.toDomain()
    )
}

fun PlayerArgs.toDomain(): PlayerArgsDomain {
    return PlayerArgsDomain(
        cid = cid,
        aid = aid
    )
}

fun RecommendConfig.toDomain(): RecommendConfigDomain {
    return RecommendConfigDomain(
        column = column,
        autoplayCard = autoplayCard,
        feedCleanAbtest = feedCleanAbtest,
        homeTransferAbtest = homeTransferAbtest,
        autoRefreshTime = autoRefreshTime,
        showInlineDanmaku = showInlineDanmaku
    )
}