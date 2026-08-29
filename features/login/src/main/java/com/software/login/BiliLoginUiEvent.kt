package com.software.login

sealed interface BiliLoginUiEvent {
    /** 刷新二维码并重新开始轮询 */
    data object RefreshQrCode : BiliLoginUiEvent
}

sealed interface BiliLoginUiEffect {
    data class ShowToast(val message: String) : BiliLoginUiEffect
    data object NavigateToHome : BiliLoginUiEffect
}
