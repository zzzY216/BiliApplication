package com.software.designsystem

import androidx.compose.ui.graphics.Color

/**
 * 设计系统色彩 Token。
 * 品牌色与常用功能色统一收敛于此，UI 层禁止硬编码颜色值。
 */
object BiliColors {
    // 品牌色
    val BiliPink = Color(0xFFFB7299)
    val BiliPinkDark = Color(0xFFE75C86)

    // 功能色（MusicTopBar 等组件使用）
    val AccentRed = Color(0xFFFF4D4D)
    val TextPrimary = Color(0xFF333333)
    val TextSecondary = Color(0xFF999999)
    val Background = Color(0xFFFFFFFF)
    val SurfaceVariantLight = Color(0xFFF5F5F5)
}
