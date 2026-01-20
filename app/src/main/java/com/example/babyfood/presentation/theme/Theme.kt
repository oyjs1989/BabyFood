package com.example.babyfood.presentation.theme

import android.app.Activity
import android.os.Build
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

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    tertiary = Accent,
    onTertiary = OnPrimary,
    tertiaryContainer = SoftYellow,
    onTertiaryContainer = Color(0xFFFF6F00),
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SoftGreen,
    onSurfaceVariant = OnBackground,
    outline = Primary,
    outlineVariant = PrimaryLight,
    inverseSurface = PrimaryDark,
    inverseOnSurface = Color.White,
    inversePrimary = PrimaryLight,
    error = Error,
    onError = OnPrimary,
    errorContainer = SoftPink,
    onErrorContainer = PrimaryDark
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Primary,
    onPrimaryContainer = OnPrimary,
    secondary = SecondaryDark,
    onSecondary = Color.White,
    secondaryContainer = Secondary,
    onSecondaryContainer = OnSecondary,
    tertiary = Accent,
    onTertiary = OnPrimary,
    tertiaryContainer = SoftYellow,
    onTertiaryContainer = Color(0xFFFF6F00),
    background = Color(0xFF1E1E1E),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF2C2C2C),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF3D3D3D),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFF424242),
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF1E1E1E),
    inversePrimary = PrimaryDark,
    error = Error,
    onError = OnPrimary,
    errorContainer = PrimaryDark,
    onErrorContainer = Color.White
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}