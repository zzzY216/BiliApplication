package com.software.core.model.pgc

import com.software.core.model.PlayUrlData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 动漫（番剧/国创）PGC 域模型。
 * 字段与 JSON 对应关系参考 PiliPlus lib/models_new/pgc/ 下的模型（真实响应验证过）。
 * 所有 JSON 下划线字段必须显式 @SerialName。
 */

// ---------- 列表：番剧索引 ----------

@Serializable
data class PgcIndexData(
    val list: List<PgcIndexItem>? = null,
)

/** /pgc/season/index/result 的 list 元素 */
@Serializable
data class PgcIndexItem(
    val badge: String? = null,                         // 角标（"独家"/"更新至XX话"）
    val cover: String? = null,                         // 封面
    @SerialName("index_show") val indexShow: String? = null,   // 副标题
    val order: String? = null,
    @SerialName("season_id") val seasonId: Long? = null,       // 详情页入口
    val title: String? = null,
)

// ---------- 列表：番剧排行 ----------

@Serializable
data class PgcRankData(
    val list: List<PgcRankItem>? = null,
)

/** /pgc/web/rank/list 的 list 元素 */
@Serializable
data class PgcRankItem(
    val cover: String? = null,
    val title: String? = null,
    val url: String? = null,                           // 详情页链接
    @SerialName("new_ep") val newEp: PgcNewEp? = null, // "更新至XX话"
    val stat: PgcRankStat? = null,
)

@Serializable
data class PgcRankStat(
    val follow: Long? = null,                          // 追番数
    val view: Long? = null,                            // 播放数
)

// ---------- 通用小结构 ----------

/** 最新一集信息（索引/排行/详情共用） */
@Serializable
data class PgcNewEp(
    val desc: String? = null,                          // "更新至XX话"
    val title: String? = null,
)

// ---------- 列表：新番时间线 ----------

@Serializable
data class PgcTimelineData(
    val result: List<TimelineResult>? = null,
)

/** /pgc/web/timeline 的 result 元素（按日分组） */
@Serializable
data class TimelineResult(
    val date: String? = null,                          // "2026-08-30"
    @SerialName("date_ts") val dateTs: Long? = null,
    @SerialName("day_of_week") val dayOfWeek: Int? = null,   // 1-7
    @SerialName("is_today") val isToday: Int? = null,         // 1 今天
    val episodes: List<TimelineEpisode>? = null,       // 当天更新的剧
)

@Serializable
data class TimelineEpisode(
    val cover: String? = null,
    @SerialName("episode_id") val episodeId: Long? = null,
    val follow: Int? = null,
    @SerialName("pub_index") val pubIndex: String? = null,    // "第1话"
    @SerialName("pub_time") val pubTime: String? = null,      // 日期字符串
    @SerialName("season_id") val seasonId: Long? = null,
    val title: String? = null,
)

// ---------- 详情：/pgc/view/web/season ----------

/** data.result 包装（详情接口的 data 是 {result: {...}}） */
@Serializable
data class SeasonDetailResponse(
    val result: SeasonDetail? = null,
)

@Serializable
data class SeasonDetail(
    @SerialName("season_id") val seasonId: Long? = null,
    @SerialName("media_id") val mediaId: Long? = null, // 评论/评分用
    val title: String? = null,
    @SerialName("season_title") val seasonTitle: String? = null, // "第一季"
    val cover: String? = null,
    val evaluate: String? = null,                      // 简介
    val subtitle: String? = null,
    val actors: String? = null,                        // 声优/演员
    val areas: List<PgcArea>? = null,                  // 地区
    val type: Int? = null,                             // 1 番剧 / 4 国创
    val rating: PgcRating? = null,                     // 评分
    val stat: PgcStat? = null,                         // 播放/追番等
    @SerialName("new_ep") val newEp: PgcNewEp? = null, // "更新至XX话"
    val publish: PgcPublish? = null,                   // 播出时间
    @SerialName("up_info") val upInfo: PgcUpInfo? = null,
    @SerialName("user_status") val userStatus: PgcUserStatus? = null, // 登录时追番状态
    val episodes: List<PgcEpisode>? = null,            // 全部分集
    val section: List<PgcSection>? = null,             // 分季（多季番）
)

@Serializable
data class PgcArea(
    val name: String? = null,
)

@Serializable
data class PgcRating(
    val score: Double? = null,                         // 如 9.8
)

@Serializable
data class PgcStat(
    val views: Long? = null,
    val danmakus: Long? = null,
    val reply: Long? = null,
    val favorite: Long? = null,
    val coin: Long? = null,                            // coins
    val share: Long? = null,
    val likes: Long? = null,
)

@Serializable
data class PgcPublish(
    @SerialName("pub_time_show") val pubTimeShow: String? = null, // "2026-07-05"
)

@Serializable
data class PgcUpInfo(
    val avatar: String? = null,
    val mid: Long? = null,
    val uname: String? = null,
)

@Serializable
data class PgcUserStatus(
    val follow: Int? = null,                           // 1 已追番 / 0 未追
    @SerialName("follow_status") val followStatus: Int? = null,
)

/** 分集（PiliPlus EpisodeItem，duration 单位为毫秒） */
@Serializable
data class PgcEpisode(
    @SerialName("ep_id") val epId: Long? = null,
    val aid: Long? = null,
    val bvid: String? = null,
    val cid: Long? = null,                             // 播放用
    val cover: String? = null,
    val title: String? = null,                         // "第1话"
    @SerialName("long_title") val longTitle: String? = null,   // "第1话 标题"
    @SerialName("show_title") val showTitle: String? = null,
    val badge: String? = null,
    val duration: Long? = null,                        // 毫秒（PGC），非秒
    @SerialName("pub_time") val pubTime: Long? = null,
    val from: String? = null,                          // "bangumi"/"pv"…
    val link: String? = null,
)

@Serializable
data class PgcSection(
    val episodes: List<PgcEpisode>? = null,
)

// ---------- 播放：/pgc/player/web/v2/playurl ----------

@Serializable
data class PgcPlayUrlResponse(
    val result: PgcPlayUrlResult? = null,
)

@Serializable
data class PgcPlayUrlResult(
    @SerialName("video_info") val videoInfo: PlayUrlData? = null, // 复用现有 PlayUrlData
    @SerialName("last_play_time") val lastPlayTime: Long? = null, // 续播进度
)

// ---------- 互动 ----------

/** /pgc/season/episode/community 的 data（点赞/投币/收藏状态） */
@Serializable
data class PgcCommunityData(
    val like: Int? = null,
    val coin: Int? = null,
    val favorite: Int? = null,
)

/** 追番/写操作响应（data 形状不稳定，仅取提示信息） */
@Serializable
data class PgcActionResult(
    val toast: String? = null,
)