package com.software.core.network

import com.software.core.network.model.BiliResponse
import com.software.biliapp.data.remote.model.QrCodeData
import com.software.biliapp.data.remote.model.QrPollData
import retrofit2.http.GET
import retrofit2.http.Query

interface BiliLoginApiService {
    // 1. 生成扫码二维码
    @GET("/x/passport-login/web/qrcode/generate")
    suspend fun getQrCodeInfo(): BiliResponse<QrCodeData>

    // 2. 轮询扫码状态
    @GET("/x/passport-login/web/qrcode/poll")
    suspend fun pollQrCodeStatus(
        @Query("qrcode_key") qrcodeKey: String
    ): QrPollData
}