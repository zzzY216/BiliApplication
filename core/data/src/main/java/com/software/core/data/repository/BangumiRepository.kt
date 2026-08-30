package com.software.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.software.core.data.BiliApiException
import com.software.core.data.biliApiCall
import com.software.core.data.net.WbiSign
import com.software.core.data.paging.PgcIndexPagingSource
import com.software.core.data.session.BiliSessionManager
import com.software.core.model.PlayUrlData
import com.software.core.model.pgc.PgcCommunityData
import com.software.core.model.pgc.PgcIndexItem
import com.software.core.model.pgc.PgcRankItem
import com.software.core.model.pgc.SeasonDetail
import com.software.core.model.pgc.TimelineResult
import com.software.core.network.BiliApiNetwork
import com.software.core.network.PgcApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 动漫（番剧/国创）域仓储。
 * 域名：api.bilibili.com 公开接口，无 appSign 门槛；排行走 WBI 签名，写操作走 bili_jct csrf。
 */
interface BangumiRepository {
    /** 番剧索引分页流（首页动漫 Tab 主内容） */
    fun getIndexPagingFlow(seasonType: Int): Flow<PagingData<PgcIndexItem>>

    /** 番剧排行（WBI 签名，一次性） */
    suspend fun getRank(seasonType: Int, day: Int = 3): Result<List<PgcRankItem>>

    /** 新番时间线（一次性，按日分组） */
    suspend fun getTimeline(types: Int, before: Int = 7, after: Int = 7): Result<List<TimelineResult>>

    /** 剧集明细 */
    suspend fun getSeasonDetail(seasonId: Long): Result<SeasonDetail>

    /** PGC 播放地址 */
    suspend fun getPlayUrl(epId: Long, cid: Long, qn: Int = 64): Result<PlayUrlData>

    /** 追番 / 取消追番 */
    suspend fun setFollow(seasonId: Long, follow: Boolean): Result<Unit>

    /** 分集互动状态（点赞/投币/收藏） */
    suspend fun getEpisodeCommunity(epId: Long): Result<PgcCommunityData>
}

@Singleton
class BangumiRepositoryImpl @Inject constructor(
    @BiliApiNetwork private val pgcApiService: PgcApiService,
    private val okHttpClient: OkHttpClient,
    private val sessionManager: BiliSessionManager,
) : BangumiRepository {

    override fun getIndexPagingFlow(seasonType: Int): Flow<PagingData<PgcIndexItem>> =
        Pager(
            config = PagingConfig(
                pageSize = 21,
                enablePlaceholders = false,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = { PgcIndexPagingSource(pgcApiService, seasonType) },
        ).flow

    override suspend fun getRank(seasonType: Int, day: Int): Result<List<PgcRankItem>> {
        return try {
            val signed = WbiSign.sign(
                client = okHttpClient,
                params = mapOf("day" to day, "season_type" to seasonType),
            )
            val response = pgcApiService.getRank(signed)
            if (response.code == 0) {
                Result.success(response.data?.list.orEmpty())
            } else {
                Result.failure(BiliApiException(response.code, response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTimeline(
        types: Int,
        before: Int,
        after: Int,
    ): Result<List<TimelineResult>> =
        biliApiCall { pgcApiService.getTimeline(types, before, after) }
            .map { it.result.orEmpty() }

    override suspend fun getSeasonDetail(seasonId: Long): Result<SeasonDetail> =
        biliApiCall { pgcApiService.getSeasonDetail(seasonId) }
            .map { it.result ?: error("season detail result is null") }

    override suspend fun getPlayUrl(epId: Long, cid: Long, qn: Int): Result<PlayUrlData> =
        biliApiCall { pgcApiService.getPlayUrl(epId, cid, qn) }
            .map { it.result?.videoInfo ?: error("playurl result is null") }

    override suspend fun setFollow(seasonId: Long, follow: Boolean): Result<Unit> {
        return try {
            val csrf = sessionManager.jctFlow.first()
            val response = if (follow) {
                pgcApiService.followSeason(seasonId, csrf)
            } else {
                pgcApiService.unfollowSeason(seasonId, csrf)
            }
            if (response.code == 0) {
                Result.success(Unit)
            } else {
                Result.failure(BiliApiException(response.code, response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEpisodeCommunity(epId: Long): Result<PgcCommunityData> =
        biliApiCall { pgcApiService.getEpisodeCommunity(epId) }
}