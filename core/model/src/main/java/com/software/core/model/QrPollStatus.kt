package com.software.core.model

/**
 * 扫码登录轮询状态机（B 站 passport 接口语义）。
 * 由 code 映射为 UI 可直接消费的状态，避免 ViewModel 里散落魔法数字。
 */
enum class QrPollStatus {
    /** 等待扫码 */
    WAITING_SCAN,

    /** 已扫码，等待手机端确认 */
    WAITING_CONFIRM,

    /** 二维码过期 */
    EXPIRED,

    /** 登录成功 */
    SUCCESS;

    companion object {
        /** 86101 未扫码 / 86090 已扫码 / 86038 过期 / 0 成功 */
        fun fromCode(code: Int): QrPollStatus = when (code) {
            0 -> SUCCESS
            86090 -> WAITING_CONFIRM
            86038 -> EXPIRED
            else -> WAITING_SCAN
        }
    }
}
