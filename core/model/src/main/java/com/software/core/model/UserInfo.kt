package com.software.core.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val isLogin: Boolean,
    val face: String,
    val uname: String,
    val money: Int,
    val moral: Int
)
