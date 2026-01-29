package com.example.babyfood.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    inverseSurface = DarkOnBackground,
    inverseOnSurface = DarkBackground,
    inversePrimary = DarkPrimary,
    error = Error,
    onError = Color.White,
    errorContainer = DarkBackground,
    onErrorContainer = DarkOnBackground
)

// ===== 动画配置 - Apple 风格 =====

// 标准动画持续时间（毫秒）- Apple 风格更流畅
const val AnimationDurationFast = 200      // 快速动画（按钮点击等）
const val AnimationDurationNormal = 300    // 标准动画（卡片展开等）
const val AnimationDurationSlow = 500      // 慢速动画（页面切换等）
const val AnimationDurationVerySlow = 600  // 非常慢速动画（复杂过渡）

// 缓动函数 - Apple 风格
// 使用更自然的缓动曲线，提升用户体验
val AnimationEasing = androidx.compose.animation.core.CubicBezierEasing(0.25f, 0.1f, 0.25f, 1.0f)  // ease-in-out
val AnimationEasingIn = androidx.compose.animation.core.CubicBezierEasing(0.42f, 0.0f, 1.0f, 1.0f)  // ease-in
val AnimationEasingOut = androidx.compose.animation.core.CubicBezierEasing(0.0f, 0.0f, 0.58f, 1.0f)  // ease-out
val AnimationEasingSpring = androidx.compose.animation.core.FastOutSlowInEasing  // 弹簧缓动（用于弹性效果）

// 预定义动画规格 - Soft UI Evolution 风格
@Composable
fun standardAnimationSpec() = tween<Float>(
    durationMillis = AnimationDurationNormal,
    easing = AnimationEasing
)

@Composable
fun fastAnimationSpec() = tween<Float>(
    durationMillis = AnimationDurationFast,
    easing = AnimationEasingOut
)

@Composable
fun slowAnimationSpec() = tween<Float>(
    durationMillis = AnimationDurationSlow,
    easing = AnimationEasing
)

@Composable
fun springAnimationSpec() = spring<Float>(
    dampingRatio = 0.8f,  // 阻尼比（0.0-1.0，越小越弹）
    stiffness = 300f      // 刚度（越小越软）
)

// ===== 新增：动画扩展函数 =====

/**
 * 创建平滑的缩放动画
 * @param targetValue 目标值
 * @param animationSpec 动画规格
 */
@Composable
fun animateScale(
    targetValue: Float,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Float> = springAnimationSpec()
): Float {
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "scale_animation"
    ).value
}

/**
 * 创建平滑的透明度动画
 * @param targetValue 目标值
 * @param animationSpec 动画规格
 */
@Composable
fun animateAlpha(
    targetValue: Float,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Float> = standardAnimationSpec()
): Float {
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "alpha_animation"
    ).value
}

/**
 * 创建脉冲动画（用于强调元素）
 * @param targetValue 目标值
 */
@Composable
fun animatePulse(
    targetValue: Float = 1f
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_animation")
    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AnimationDurationSlow,
                easing = AnimationEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    ).value
}

/**
 * 创建淡入淡出动画
 * @param isVisible 是否可见
 */
@Composable
fun animateFadeInOut(
    isVisible: Boolean
): Float {
    val targetValue = if (isVisible) 1f else 0f
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = standardAnimationSpec(),
        label = "fade_animation"
    ).value
}

/**
 * 创建滑动动画（用于页面切换）
 * @param targetValue 目标值
 */
@Composable
fun animateSlide(
    targetValue: Float,
    animationSpec: androidx.compose.animation.core.AnimationSpec<Float> = slowAnimationSpec()
): Float {
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "slide_animation"
    ).value
}

/**
 * 创建弹性缩放动画（用于强调效果）
 * @param targetValue 目标值
 */
@Composable
fun animateBouncyScale(
    targetValue: Float
): Float {
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = spring(
            dampingRatio = 0.6f,  // 更弹性
            stiffness = 400f     // 更硬朗
        ),
        label = "bouncy_scale_animation"
    ).value
}

// ===== 新增：动画状态管理 =====

/**
 * 动画状态枚举
 */
enum class AnimationState {
    ENTERING,    // 进入中
    VISIBLE,     // 可见
    EXITING,     // 退出中
    HIDDEN       // 隐藏
}

/**
 * 创建可管理的动画状态
 * @param initialState 初始状态
 */
@Composable
fun rememberAnimationState(
    initialState: AnimationState = AnimationState.HIDDEN
): AnimationState {
    var state by remember { mutableStateOf(initialState) }
    return state
}

/**
 * 根据动画状态获取透明度
 * @param state 动画状态
 */
@Composable
fun getAlphaByState(state: AnimationState): Float {
    return when (state) {
        AnimationState.ENTERING -> animateAlpha(1f)
        AnimationState.VISIBLE -> 1f
        AnimationState.EXITING -> animateAlpha(0f)
        AnimationState.HIDDEN -> 0f
    }
}

/**
 * 根据动画状态获取缩放
 * @param state 动画状态
 */
@Composable
fun getScaleByState(state: AnimationState): Float {
    return when (state) {
        AnimationState.ENTERING -> animateScale(1f)
        AnimationState.VISIBLE -> 1f
        AnimationState.EXITING -> animateScale(0.9f)
        AnimationState.HIDDEN -> 0.9f
    }
}

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