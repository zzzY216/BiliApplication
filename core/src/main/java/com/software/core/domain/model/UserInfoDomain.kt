package com.software.core.domain.model

data class UserInfoDomain(
    val isLogin: Boolean,
    val face: String,
    val uname: String,
    val money: Int,
    val moral: Int
)
