package com.software.login

import com.software.core.model.QrPollStatus

data class BiliLoginUiState(
    /** 二维码内容 URL（Bitmap 由 UI 层按需生成，不放进状态） */
    val qrUrl: String? = null,
    /** 轮询状态（状态机已下沉到 core:model） */
    val qrStatus: QrPollStatus = QrPollStatus.WAITING_SCAN,
    /** 是否正在获取二维码 */
    val isQrLoading: Boolean = false,
)
