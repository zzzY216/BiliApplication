package com.software.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.core.data.repository.AuthRepository
import com.software.core.model.QrPollStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class BiliLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BiliLoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<BiliLoginUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private var pollJob: Job? = null
    private var lastToastStatus: QrPollStatus? = null

    init {
        checkLoginStatus()
    }

    fun onEvent(event: BiliLoginUiEvent) {
        when (event) {
            BiliLoginUiEvent.RefreshQrCode -> refreshQrCode()
        }
    }

    private fun refreshQrCode() {
        pollJob?.cancel()
        lastToastStatus = null
        viewModelScope.launch {
            _uiState.update {
                it.copy(isQrLoading = true, qrStatus = QrPollStatus.WAITING_SCAN)
            }
            authRepository.getQrCode()
                .onSuccess { qrCodeData ->
                    _uiState.update { it.copy(qrUrl = qrCodeData.url, isQrLoading = false) }
                    startPolling(qrCodeData.qrcode_key)
                }
                .onFailure {
                    _uiState.update { it.copy(isQrLoading = false) }
                    _uiEffect.send(BiliLoginUiEffect.ShowToast("二维码生成失败"))
                }
        }
    }

    private fun startPolling(qrcodeKey: String) {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (isActive) {
                authRepository.pollQrCodeStatus(qrcodeKey)
                    .onSuccess { pollData ->
                        val status = QrPollStatus.fromCode(pollData.code)
                        _uiState.update { it.copy(qrStatus = status) }
                        when (status) {
                            QrPollStatus.SUCCESS -> {
                                authRepository.saveSession(
                                    url = pollData.data.url ?: "",
                                    refreshToken = pollData.data.refreshToken ?: ""
                                )
                                _uiEffect.send(BiliLoginUiEffect.ShowToast("登录成功"))
                                _uiEffect.send(BiliLoginUiEffect.NavigateToHome)
                                pollJob?.cancel()
                            }

                            QrPollStatus.WAITING_CONFIRM -> {
                                if (lastToastStatus != QrPollStatus.WAITING_CONFIRM) {
                                    _uiEffect.send(BiliLoginUiEffect.ShowToast("已扫码，请在手机确认登录"))
                                    lastToastStatus = QrPollStatus.WAITING_CONFIRM
                                }
                            }

                            QrPollStatus.EXPIRED -> {
                                if (lastToastStatus != QrPollStatus.EXPIRED) {
                                    _uiEffect.send(BiliLoginUiEffect.ShowToast("二维码已过期，请刷新"))
                                    lastToastStatus = QrPollStatus.EXPIRED
                                }
                                pollJob?.cancel()
                            }

                            QrPollStatus.WAITING_SCAN -> Unit
                        }
                    }
                    .onFailure {
                        _uiEffect.send(BiliLoginUiEffect.ShowToast("二维码状态查询失败"))
                    }
                delay(3000.milliseconds)
            }
        }
    }

    override fun onCleared() {
        pollJob?.cancel()
        super.onCleared()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val cookie = authRepository.cookieFlow().first()
            if (cookie.isNotEmpty()) {
                _uiEffect.send(BiliLoginUiEffect.NavigateToHome)
            }
        }
    }
}
