package com.software.core.network

import com.software.core.network.model.BiliResponse
import com.software.core.network.model.BiliVideoUrlResponse
import com.software.core.network.model.HasLikeVideoData
import com.software.core.network.model.LikeVideoData
import com.software.core.network.model.PlayUrlData
import com.software.biliapp.data.remote.model.PopularData
import com.software.biliapp.data.remote.model.RecommendData
import com.software.core.network.model.ReplyData
import com.software.biliapp.data.remote.model.UserInfo
import com.software.biliapp.data.remote.model.VideoDetail
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BiliApiService {
    /**
     * @param idx 上一次返回列表中最后一条视频的 idx
     * @param pull true 为下拉刷新(获取最新), false 为上拉加载更多(获取下一页)
     * @param login_event 登录事件，翻页自增或固定传 2
     */
    @GET("/x/v2/feed/index")
    suspend fun getRecommendList(
        @Query("idx") idx: Long = 0,
        @Query("pull") pull: Boolean = true, // 翻页必须传 false
        @Query("login_event") loginEvent: Int = 1,
        @Query("flush") flush: Int = 0
    ): BiliResponse<RecommendData>

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
    /// aid	num	稿件avid	必要（可选）	avid与bvid任选一个
    /// bvid	str	稿件bvid	必要（可选）	avid与bvid任选一个
    /// like	num	操作方式	必要	1：点赞 2：取消赞
    // csrf	str	CSRF Token（位于cookie）	必要
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
//        @Query("access_key") access_key: String? = null
    ): HasLikeVideoData
}