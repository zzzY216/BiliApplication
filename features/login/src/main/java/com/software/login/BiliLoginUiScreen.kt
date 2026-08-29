package com.software.login

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.software.core.common.ZQRCodeUtils
import com.software.core.model.QrPollStatus
import com.software.designsystem.BiliColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun BiliLoginScreen(
    viewModel: BiliLoginViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 二维码 Bitmap 在 UI 层按需生成（IO 线程），不再放进 UiState（P2-5）
    var qrBitmap by remember(uiState.qrUrl) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(uiState.qrUrl) {
        val url = uiState.qrUrl ?: return@LaunchedEffect
        qrBitmap = withContext(Dispatchers.Default) { ZQRCodeUtils.generateQRCode(url) }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is BiliLoginUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }

                is BiliLoginUiEffect.NavigateToHome -> {
                    onNavigateToMain()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        if (uiState.qrUrl == null) {
            viewModel.onEvent(BiliLoginUiEvent.RefreshQrCode)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_bilibili_logo),
                contentDescription = "Bilibili",
                tint = BiliColors.BiliPink,
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = "欢迎登录哔哩哔哩",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(48.dp))

            QrCodeSection(
                qrBitmap = qrBitmap,
                qrStatus = uiState.qrStatus,
                isQrLoading = uiState.isQrLoading,
                onRefresh = { viewModel.onEvent(BiliLoginUiEvent.RefreshQrCode) }
            )
        }
    }
}

@Composable
fun QrCodeSection(
    qrBitmap: Bitmap?,
    qrStatus: QrPollStatus,
    isQrLoading: Boolean,
    onRefresh: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            when {
                isQrLoading -> CircularProgressIndicator(color = BiliColors.BiliPink)
                qrBitmap != null -> Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentScale = ContentScale.Fit
                )

                else -> CircularProgressIndicator(color = BiliColors.BiliPink)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = statusText(qrStatus),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onRefresh,
            colors = ButtonDefaults.textButtonColors(contentColor = BiliColors.BiliPink)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("刷新二维码")
        }
    }
}

private fun statusText(status: QrPollStatus): String = when (status) {
    QrPollStatus.WAITING_SCAN -> "请使用 哔哩哔哩手机端 扫码登录"
    QrPollStatus.WAITING_CONFIRM -> "已扫码，请在手机确认登录"
    QrPollStatus.EXPIRED -> "二维码已过期，请刷新"
    QrPollStatus.SUCCESS -> "登录成功"
}
