package com.software.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.biliapp.domain.usecase.BiliPollQrCodeStatusUseCase
import com.software.biliapp.domain.usecase.BiliQrCodeDataUseCase
import com.software.core.mongo.bili.BiliSessionManager
import com.software.core.util.ZQRCodeUtils
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
    private val biliQrCodeDataUseCase: BiliQrCodeDataUseCase,
    private val biliPollQrCodeStatusUseCase: BiliPollQrCodeStatusUseCase,
    private val sessionManager: BiliSessionManager
) : ViewModel(){
    private val _uiState = MutableStateFlow(BiliLoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<BiliLoginUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    private var pollJob: Job? = null

    init {
        checkLoginStatus()
    }

    fun onEvent(event: BiliLoginUiEvent) {
        when (event) {
            BiliLoginUiEvent.BiliQrCodeData -> biliQrCodeData()
            is BiliLoginUiEvent.UpdateCurrentPassword -> updateCurrentPassword(event.password)
            is BiliLoginUiEvent.UpdateCurrentUsername -> updateCurrentUsername(event.username)
            is BiliLoginUiEvent.BiliPollQrCodeStatus -> {
                val key = _uiState.value.qrCodeData?.qrcode_key
                if (!key.isNullOrEmpty()) {
                    biliPollQrCodeStatus(key)
                }
            }
        }
    }

    private fun updateCurrentUsername(username: String) {
        _uiState.update {
            it.copy(currentUsername = username)
        }
    }

    private fun updateCurrentPassword(password: String) {
        _uiState.update {
            it.copy(currentPassword = password)
        }
    }

    private fun biliPollQrCodeStatus(qrcodeKey: String) {
        pollJob?.cancel()
        Log.d("QRCode", "biliPollQrCodeStatus() 函数被触发")
        pollJob = viewModelScope.launch {
            var lastCode: Int? = null
            while (isActive) {
                try {
                    val response =
                        biliPollQrCodeStatusUseCase.invoke(
                            uiState.value.qrCodeData?.qrcode_key ?: ""
                        )
                    response.fold(
                        onSuccess = { qrPollDataDomain ->
                            val currentCode = qrPollDataDomain.data.code
                            if (currentCode != lastCode) {
                                when (currentCode) {
                                    0 -> {
                                        Log.d("QRCode", "登录成功")
                                        Log.d(
                                            "QRCode",
                                            "准备存入 Token: ${qrPollDataDomain.data.refreshToken}"
                                        )
                                        sessionManager.saveLoginSession(
                                            url = qrPollDataDomain.data.url ?: "",
                                            refreshToken = qrPollDataDomain.data.refreshToken ?: ""
                                        )
                                        Log.d("QRCode", "saveRefreshToken 函数执行完毕")
                                        _uiEffect.send(BiliLoginUiEffect.ShowToast("登录成功"))
                                        _uiEffect.send(BiliLoginUiEffect.NavigateToRecommend)
                                        pollJob?.cancel()
                                    }

                                    86101 -> {}
                                    86090 -> {
                                        _uiEffect.send(BiliLoginUiEffect.ShowToast("已扫码，请在手机确认登录"))
                                    }

                                    86038 -> {
                                        Log.d("QRCode", "二维码状态已过期")
                                        _uiEffect.send(BiliLoginUiEffect.ShowToast("二维码状态已过期"))
                                        pollJob?.cancel()
                                    }

                                    else -> {
                                        Log.d(
                                            "QRCode",
                                            "等待扫码/确认中 ... code = ${qrPollDataDomain.code}"
                                        )
                                    }
                                }
                                lastCode = currentCode
                            }
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(
                                    qrPollData = null
                                )
                            }
                            Log.d("QRCode", "二维码状态查询失败: e ${it.message}")
                            _uiEffect.send(BiliLoginUiEffect.ShowToast("二维码状态查询失败"))
                        }
                    )
                } catch (e: Exception) {
                    Log.e("QRCode", "二维码状态查询失败: ${e.message}")
                }
                delay(3000.milliseconds)
            }
        }
    }

    private fun biliQrCodeData() {
        pollJob?.cancel()
        viewModelScope.launch {
            val response = biliQrCodeDataUseCase.invoke()
            response.fold(
                onSuccess = { qrCodeDataDomain ->
                    _uiState.update {
                        it.copy(
                            qrCodeData = qrCodeDataDomain
                        )
                    }
                    val bitmap = ZQRCodeUtils.generateQRCode(qrCodeDataDomain.url)
                    _uiState.update {
                        it.copy(
                            qrBitmap = bitmap
                        )
                    }
                    _uiEffect.send(BiliLoginUiEffect.ShowToast("二维码生成成功"))
                    biliPollQrCodeStatus(qrCodeDataDomain.qrcode_key)
                },
                onFailure = { errorMessage ->
                    _uiState.update {
                        it.copy(
                            qrCodeData = null
                        )
                    }
                    Log.d("QRCode", "二维码生成失败: $errorMessage")
                    _uiEffect.send(BiliLoginUiEffect.ShowToast("二维码生成失败ViewModel"))
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollJob?.cancel()
    }
    private fun checkLoginStatus() {
        viewModelScope.launch {
            val cookie = sessionManager.cookieFlow.first()
            if (cookie.isNotEmpty()) {
                _uiEffect.send(BiliLoginUiEffect.NavigateToRecommend)
            }
        }
    }
}