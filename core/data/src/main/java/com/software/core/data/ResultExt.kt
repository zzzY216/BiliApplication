package com.software.core.data

import com.software.core.model.BiliResponse

/** 业务码非 0 或 data 为空时抛出的统一异常，保留后端 message。 */
class BiliApiException(
    val code: Int,
    override val message: String,
) : Exception("code=$code, message=$message")

/**
 * 统一包裹 BiliResponse<T> 形式的接口调用：code==0 且 data 非空 → success，否则 failure。
 */
suspend fun <T> biliApiCall(block: suspend () -> BiliResponse<T>): Result<T> {
    return try {
        val response = block()
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
