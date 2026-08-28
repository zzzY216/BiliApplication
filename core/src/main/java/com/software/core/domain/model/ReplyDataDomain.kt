package com.software.biliapp.domain.model

data class ReplyDataDomain(
    val replies: List<BiliReplyDomain>?,
    val page: ReplyPageDomain?
)


data class BiliReplyDomain(
    val rpid: Long,
    val mid: Long,
    val member: ReplyMemberDomain,
    val content: ReplyContentDomain,
    val like: Int,
    val replyControl: ReplyControlDomain?,
    val replies: List<BiliReplyDomain>?
)


data class ReplyContentDomain(
    val message: String,
)

data class ReplyControlDomain(
    val timeDesc: String,
    val location: String?
)


data class ReplyMemberDomain(
    val uname: String,
    val avatar: String,
    val sex: String,
    val vip: ReplyVipDomain,
)


data class ReplyVipDomain(
    val vipType: Int,
)

data class ReplyPageDomain(
    val num: Int,
    val size: Int,
    val count: Int,
)