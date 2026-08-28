package com.software.biliapp.data.remote.model

import com.software.core.domain.model.UserInfoDomain
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val isLogin: Boolean,
    val face: String,
    val uname: String,
    val money: Int,
    val moral: Int
)

fun UserInfo.toDomain(): UserInfoDomain {
    return UserInfoDomain(
        isLogin = isLogin,
        face = face,
        uname = uname,
        money = money,
        moral = moral
    )
}
