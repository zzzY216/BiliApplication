package com.software.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.software.core.data.BiliApiException
import com.software.core.data.biliApiCall
import com.software.core.data.paging.BiliRecommendPagingSource
import com.software.core.data.paging.GetPopularPagingSource
import com.software.core.data.session.BiliSessionManager
import com.software.core.model.HasLikeVideoData
import com.software.core.model.LikeVideoData
import com.software.core.model.PlayUrlData
import com.software.core.model.PopularData
import com.software.core.model.PopularItem
import com.software.core.model.RecommendData
import com.software.core.model.RecommendItem
import com.software.core.model.ReplyData
import com.software.core.model.VideoDetail
import com.software.core.network.BiliApiNetwork
import com.software.core.network.BiliApiService
import com.software.core.network.BiliAppApiService
import com.software.core.network.BiliAppNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 视频域仓储：推荐流 / 热门 / 播放地址 / 详情 / 评论 / 点赞。
 */
interface VideoRepository {
    suspend fun getRecommendVideo(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<RecommendData>

    fun getRecommendVideoPagingFlow(): Flow<PagingData<RecommendItem>>

    suspend fun getPopularList(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<PopularData>

    fun getPopularListPagingFlow(): Flow<PagingData<PopularItem>>

    suspend fun getVideoPlayUrl(
        avid: String,
        cid: String,
        qn: Int,
        type: String,
        platform: String
    ): Result<PlayUrlData>

    suspend fun getVideoDetail(aid: String?, bvid: String?): Result<VideoDetail>

    suspend fun getReplyList(
        oid: Long,
        type: Int,
        sort: Int,
        pn: Int,
        ps: Int
    ): Result<ReplyData>

    suspend fun likeVideo(aid: String?, bvid: String?, like: Int): Result<LikeVideoData>

    suspend fun hasLikeVideo(aid: String?, bvid: String?): Result<HasLikeVideoData>
}

@Singleton
class VideoRepositoryImpl @Inject constructor(
    @BiliAppNetwork private val appApiService: BiliAppApiService,
    @BiliApiNetwork private val apiService: BiliApiService,
    private val recommendPagingSource: BiliRecommendPagingSource,
    private val popularPagingSource: GetPopularPagingSource,
    private val sessionManager: BiliSessionManager,
) : VideoRepository {

    override suspend fun getRecommendVideo(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<RecommendData> =
        biliApiCall { appApiService.getRecommendList(idx, pull, loginEvent, flush) }

    override fun getRecommendVideoPagingFlow(): Flow<PagingData<RecommendItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            pagingSourceFactory = { recommendPagingSource }
        ).flow
    }

    override suspend fun getPopularList(
        idx: Long,
        pull: Boolean,
        loginEvent: Int,
        flush: Int
    ): Result<PopularData> =
        biliApiCall { apiService.getPopularList(idx, pull, loginEvent, flush) }

    override fun getPopularListPagingFlow(): Flow<PagingData<PopularItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                prefetchDistance = 3
            ),
            initialKey = 0,
            pagingSourceFactory = { popularPagingSource }
        ).flow
    }

    override suspend fun getVideoPlayUrl(
        avid: String,
        cid: String,
        qn: Int,
        type: String,
        platform: String
    ): Result<PlayUrlData> {
        return try {
            val response = apiService.getVideoPlayUrl(avid, cid, qn, type, platform)
            if (response.code == 0) {
                response.data?.let { Result.success(it) }
                    ?: Result.failure(BiliApiException(response.code, "data is null"))
            } else {
                Result.failure(BiliApiException(response.code, response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVideoDetail(aid: String?, bvid: String?): Result<VideoDetail> =
        biliApiCall { apiService.getVideoDetail(aid, bvid) }

    override suspend fun getReplyList(
        oid: Long,
        type: Int,
        sort: Int,
        pn: Int,
        ps: Int
    ): Result<ReplyData> =
        biliApiCall { apiService.getReplyList(oid, type, sort, pn, ps) }

    override suspend fun likeVideo(aid: String?, bvid: String?, like: Int): Result<LikeVideoData> {
        return try {
            val csrf = sessionManager.jctFlow.first()
            val response = apiService.likeVideo(
                aid, bvid, like, csrf,
                "https://www.bilibili.com/video/$bvid"
            )
            if (response.code == 0) {
                Result.success(response)
            } else {
                Result.failure(BiliApiException(response.code, response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasLikeVideo(aid: String?, bvid: String?): Result<HasLikeVideoData> {
        return try {
            val response = apiService.hasLikeVideo(aid, bvid)
            if (response.code == 0) {
                Result.success(response)
            } else {
                Result.failure(BiliApiException(response.code, response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
