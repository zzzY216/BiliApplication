package com.software.core.network.model

import com.software.biliapp.domain.model.BiliReplyDomain
import com.software.biliapp.domain.model.ReplyContentDomain
import com.software.biliapp.domain.model.ReplyControlDomain
import com.software.biliapp.domain.model.ReplyDataDomain
import com.software.biliapp.domain.model.ReplyMemberDomain
import com.software.biliapp.domain.model.ReplyPageDomain
import com.software.biliapp.domain.model.ReplyVipDomain
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

fun ReplyData.toDomain(): ReplyDataDomain {
    return ReplyDataDomain(
        replies = replies?.map { it.toDomain() },
        page = page?.toDomain()
    )
}

fun BiliReply.toDomain(): BiliReplyDomain {
    return BiliReplyDomain(
        rpid = rpid,
        mid = mid,
        member = member.toDomain(),
        content = content.toDomain(),
        like = like,
        replyControl = replyControl?.toDomain(),
        replies = replies?.map { it.toDomain() }
    )
}

fun ReplyContent.toDomain(): ReplyContentDomain {
    return ReplyContentDomain(
        message = message
    )
}

fun ReplyControl.toDomain(): ReplyControlDomain {
    return ReplyControlDomain(
        timeDesc = timeDesc,
        location = location
    )
}

fun ReplyMember.toDomain(): ReplyMemberDomain {
    return ReplyMemberDomain(
        uname = uname,
        avatar = avatar,
        sex = sex,
        vip = vip.toDomain(),
    )
}

fun ReplyVip.toDomain(): ReplyVipDomain {
    return ReplyVipDomain(
        vipType = vipType,
    )
}

fun ReplyPage.toDomain(): ReplyPageDomain {
    return ReplyPageDomain(
        num = num,
        size = size,
        count = count
    )
}