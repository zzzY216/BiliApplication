package com.software.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReplyData(
    val replies: List<BiliReply>?,
    val page: ReplyPage?
)

@Serializable
data class BiliReply(
    val rpid: Long,
    val mid: Long,
    val member: ReplyMember,
    val content: ReplyContent,
    val like: Int,
    @SerialName("reply_control") val replyControl: ReplyControl? = null,
    val replies: List<BiliReply>?
)

@Serializable
data class ReplyContent(
    val message: String,
)

@Serializable
data class ReplyControl(
    @SerialName("time_desc") val timeDesc: String,
    val location: String?
)

@Serializable
data class ReplyMember(
    val uname: String,
    val avatar: String,
    val sex: String,
    val vip: ReplyVip
)

@Serializable
data class ReplyVip(
    val vipType: Int,
)

@Serializable
data class ReplyPage(
    val num: Int,
    val size: Int,
    val count: Int,
)
