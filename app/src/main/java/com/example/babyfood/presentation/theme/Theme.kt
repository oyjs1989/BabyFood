package com.example.babyfood.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ===== 亮色主题配色方案 - BabyFood 暖阳辅食橙 (Plan A) =====
private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = Color.White,
    secondary = SecondaryYellow,
    onSecondary = TextMain,
    tertiary = StatusSuccess,
    onTertiary = Color.White,
    error = StatusError,
    onError = Color.White,
    background = BackgroundWarm,
    onBackground = TextMain,
    surface = SurfaceWhite,
    onSurface = TextMain,
    surfaceVariant = SurfaceCream,
    onSurfaceVariant = TextSub,
    outline = OutlineWarm
)

// ===== 暗色主题配色方案 =====
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrangeLight,
    onPrimary = Color(0xFF3D2B1F),
    secondary = SecondaryYellow,
    onSecondary = Color.Black,
    tertiary = StatusSuccess,
    onTertiary = Color.Black,
    error = StatusError,
    onError = Color.Black,
    background = Color(0xFF1F1A17),
    onBackground = Color(0xFFF2EAE0),
    surface = Color(0xFF2D2621),
    onSurface = Color(0xFFF2EAE0),
    surfaceVariant = Color(0xFF3D332D),
    onSurfaceVariant = Color(0xFFC7BDB3),
    outline = Color(0xFF4D433D)
)

@Composable
fun BabyFoodTheme(
    themePreference: String? = null,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themePreference) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
