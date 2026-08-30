package com.software.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 视频宽高（瀑布流卡片用真实比例做参差高度） */
@Serializable
data class VideoDimension(
    val width: Int? = null,
    val height: Int? = null,
)

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
    @SerialName("cover_left_2_content_description") val coverLeft2ContentDescription: String? = "", // 弹幕数据
    @SerialName("cover_right_content_description") val coverRightContentDescription: String? = "", // 时长
    @SerialName("player_args") val playerArgs: PlayerArgs? = null,
    val dimension: VideoDimension? = null   // 瀑布流宽高比
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
