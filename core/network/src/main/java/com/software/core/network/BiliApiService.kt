package com.software.core.network

import com.software.core.model.BiliResponse
import com.software.core.model.BiliVideoUrlResponse
import com.software.core.model.HasLikeVideoData
import com.software.core.model.LikeVideoData
import com.software.core.model.PlayUrlData
import com.software.core.model.PopularData
import com.software.core.model.ReplyData
import com.software.core.model.UserInfo
import com.software.core.model.VideoDetail
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * api.bilibili.com 域接口。
 * 与 [BiliAppApiService]（app.bilibili.com）按 baseUrl 拆分，避免一个接口挂两个域名。
 */
interface BiliApiService {
    @GET("/x/web-interface/popular")
    suspend fun getPopularList(
        @Query("idx") idx: Long = 0,
        @Query("pull") pull: Boolean = true, // 翻页必须传 false
        @Query("login_event") loginEvent: Int = 1,
        @Query("flush") flush: Int = 0
    ): BiliResponse<PopularData>

    /**
     * 获取视频播放流地址 (HTML5 接口，最易于在播放器中使用)
     * @param aid 视频 avid
     * @param cid 视频内部分片 id
     * @param qn 想要获取的画质 (16: 360P, 64: 720P, 80: 1080P)
     * @param type mp4 格式在 Android 端兼容性最好
     */
    @GET("/x/player/playurl")
    suspend fun getVideoPlayUrl(
        @Query("avid") aid: String,
        @Query("cid") cid: String,
        @Query("qn") qn: Int = 64,
        @Query("type") type: String = "mp4",
        @Query("platform") platform: String = "html5",
    ): BiliVideoUrlResponse<PlayUrlData>

    @GET("/x/web-interface/view")
    suspend fun getVideoDetail(
        @Query("aid") aid: String? = null,
        @Query("bvid") bvid: String? = null
    ): BiliResponse<VideoDetail>

    @GET("/x/web-interface/nav")
    suspend fun getUserInfo(@Header("Cookie") cookie: String): BiliResponse<UserInfo>

    /**
     * 获取主评论列表
     * @param type 类型，视频为 1
     * @param oid 视频 aid (不是 bvid)
     * @param sort 排序方式：0-按时间，1-按热度
     * @param pn 页码
     * @param ps 每页数量
     */
    @GET("/x/v2/reply")
    suspend fun getReplyList(
        @Query("oid") oid: Long,
        @Query("type") type: Int = 1,
        @Query("sort") sort: Int = 2,
        @Query("pn") pn: Int = 1,
        @Query("ps") ps: Int = 20
    ): BiliResponse<ReplyData>

    // 点赞 Post
    // aid 稿件avid（与 bvid 二选一）；like 1点赞 2取消；csrf 取自 cookie
    @FormUrlEncoded
    @POST("/x/web-interface/archive/like")
    suspend fun likeVideo(
        @Field("aid") aid: String? = null,
        @Field("bvid") bvid: String? = null,
        @Field("like") like: Int,
        @Field("csrf") csrf: String,
        @Header("Referer") referer: String,
    ): LikeVideoData

    @GET("/x/web-interface/archive/has/like")
    suspend fun hasLikeVideo(
        @Query("aid") aid: String? = null,
        @Query("bvid") bvid: String? = null,
    ): HasLikeVideoData
}
