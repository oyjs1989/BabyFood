package com.example.babyfood.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ===== 亮色主题配色方案 =====
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    inverseSurface = PrimaryDark,
    inverseOnSurface = Color.White,
    inversePrimary = PrimaryLight,
    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer
)

// ===== 暗色主题配色方案 =====
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimary,
    secondary = SecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = Secondary,
    onSecondaryContainer = OnSecondary,
    tertiary = TertiaryDark,
    onTertiary = Color.White,
    tertiaryContainer = Tertiary,
    onTertiaryContainer = OnTertiary,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFF424242),
    inverseSurface = Color(0xFFE1E1E1),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = PrimaryDark,
    error = Error,
    onError = Color.White,
    errorContainer = PrimaryDark,
    onErrorContainer = Color.White
)

// ===== 动画配置 =====

// 标准动画持续时间（毫秒）
const val AnimationDurationFast = 150
const val AnimationDurationNormal = 300
const val AnimationDurationSlow = 500

// 缓动函数
val AnimationEasing = androidx.compose.animation.core.FastOutSlowInEasing

// 预定义动画规格
@Composable
fun standardAnimationSpec() = tween<Float>(
    durationMillis = AnimationDurationNormal,
    easing = AnimationEasing
)

@Composable
fun fastAnimationSpec() = tween<Float>(
    durationMillis = AnimationDurationFast,
    easing = AnimationEasing
)

@Composable
fun slowAnimationSpec() = tween<Float>(
    durationMillis = AnimationDurationSlow,
    easing = AnimationEasing
)

@Composable
fun BabyFoodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 设置状态栏颜色
            window.statusBarColor = colorScheme.primary.toArgb()

            // 设置导航栏颜色
            window.navigationBarColor = colorScheme.surface.toArgb()

            // 启用边缘到边缘显示（Android 11+）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.decorView.setFitsSystemWindows(false)
            }

            // 设置状态栏图标颜色
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme

            // 设置导航栏图标颜色
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}