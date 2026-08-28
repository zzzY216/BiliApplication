package com.software.login

sealed class BiliLoginUiEvent {
    data class UpdateCurrentUsername(val username: String) : BiliLoginUiEvent()
    data class UpdateCurrentPassword(val password: String) : BiliLoginUiEvent()
    object BiliPollQrCodeStatus : BiliLoginUiEvent()
    object BiliQrCodeData : BiliLoginUiEvent()
}

sealed class BiliLoginUiEffect {
    data class ShowToast(val message: String) : BiliLoginUiEffect()
    object NavigateToRecommend : BiliLoginUiEffect()
}