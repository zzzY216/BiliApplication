package com.software.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BiliColors.BiliPink,
    secondary = BiliColors.BiliPinkDark,
    tertiary = BiliColors.BiliPink,
    background = Color(0xFF181818),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E6E6),
    onSurfaceVariant = Color(0xFFB0B0B0),
)

private val LightColorScheme = lightColorScheme(
    primary = BiliColors.BiliPink,
    secondary = BiliColors.BiliPinkDark,
    tertiary = BiliColors.BiliPink,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF6B6B6B),
)

/**
 * 全局主题（从 app/ui/theme 收敛至设计系统）。
 */
@Composable
fun BiliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color 在 Android 12+ 可用
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BiliTypography,
        content = content
    )
}
