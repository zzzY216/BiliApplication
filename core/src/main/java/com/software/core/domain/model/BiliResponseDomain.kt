package com.software.biliapp.domain.model

data class BiliResponseDomain<T>(
    val code: Int,
    val message: String,
    val data: T?
)