package com.software.core.network

import com.software.core.model.BiliResponse
import com.software.core.model.pgc.PgcActionResult
import com.software.core.model.pgc.PgcCommunityData
import com.software.core.model.pgc.PgcIndexData
import com.software.core.model.pgc.PgcPlayUrlResponse
import com.software.core.model.pgc.PgcRankData
import com.software.core.model.pgc.PgcTimelineData
import com.software.core.model.pgc.SeasonDetailResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * 动漫（番剧/国创）PGC 域接口，全部位于 api.bilibili.com，复用 @BiliApiNetwork Retrofit。
 * 注意：路径不带前导斜杠（拼在 baseUrl 之后）。
 */
interface PgcApiService {

    /** 番剧索引-结果（分页） */
    @GET("pgc/season/index/result")
    suspend fun getIndexResult(
        @Query("page") page: Int,
        @Query("pagesize") pageSize: Int,
        @Query("season_type") seasonType: Int,
        @Query("order") order: Int = 3,     // 3 更新时间
        @Query("sort") sort: Int = 0,
        @Query("year") year: Int? = null,
        @Query("area") area: Int? = null,
        @Query("is_finish") isFinish: Int? = null,
    ): BiliResponse<PgcIndexData>

    /** 番剧排行（需 WBI 签名，签名后的参数整体传入） */
    @GET("pgc/web/rank/list")
    suspend fun getRank(
        @QueryMap params: Map<String, String>,
    ): BiliResponse<PgcRankData>

    /** 新番时间线（types: 1 番剧 / 4 国创） */
    @GET("pgc/web/timeline")
    suspend fun getTimeline(
        @Query("types") types: Int,
        @Query("before") before: Int = 7,
        @Query("after") after: Int = 7,
    ): BiliResponse<PgcTimelineData>

    /** 剧集明细 */
    @GET("pgc/view/web/season")
    suspend fun getSeasonDetail(
        @Query("season_id") seasonId: Long,
    ): BiliResponse<SeasonDetailResponse>

    /** PGC 播放地址 */
    @GET("pgc/player/web/v2/playurl")
    suspend fun getPlayUrl(
        @Query("ep_id") epId: Long,
        @Query("cid") cid: Long,
        @Query("qn") qn: Int = 64,
        @Query("fnval") fnval: Int = 4048,
    ): BiliResponse<PgcPlayUrlResponse>

    /** 追番 */
    @FormUrlEncoded
    @POST("pgc/web/follow/add")
    suspend fun followSeason(
        @Field("season_id") seasonId: Long,
        @Field("csrf") csrf: String,
    ): BiliResponse<PgcActionResult>

    /** 取消追番 */
    @FormUrlEncoded
    @POST("pgc/web/follow/del")
    suspend fun unfollowSeason(
        @Field("season_id") seasonId: Long,
        @Field("csrf") csrf: String,
    ): BiliResponse<PgcActionResult>

    /** 分集互动状态（点赞/投币/收藏） */
    @GET("pgc/season/episode/community")
    suspend fun getEpisodeCommunity(
        @Query("ep_id") epId: Long,
    ): BiliResponse<PgcCommunityData>
}