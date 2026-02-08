package com.example.babyfood.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutBack
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
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ===== 亮色主题配色方案 - BabyFood 设计系统 =====
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = Primary,
    secondary = Green,
    onSecondary = Color.White,
    tertiary = Blue,
    onTertiary = Color.White,
    error = Error,
    onError = Color.White,
    background = PageBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    surfaceVariant = PageBackground,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    outlineVariant = Outline,
    inverseSurface = Primary,
    inverseOnSurface = Color.White,
    inversePrimary = PrimaryLight,
    scrim = Scrim
)

// ===== 暗色主题配色方案 - BabyFood 设计系统 =====
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.White,
    primaryContainer = Primary,
    onPrimaryContainer = DarkPrimary,
    secondary = DarkSuccess,
    onSecondary = Color.White,
    tertiary = DarkError,
    onTertiary = Color.White,
    error = DarkError,
    onError = Color.White,
    background = DarkPageBackground,
    onBackground = DarkTextPrimary,
    surface = DarkCardBackground,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardBackground,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    inverseSurface = DarkTextPrimary,
    inverseOnSurface = DarkPageBackground,
    inversePrimary = Primary,
    scrim = DarkScrim
)

// ===== 动画配置 - BabyFood 设计系统 =====
// 基于动效量化标准

// 动画持续时间（毫秒）
const val AnimationDurationPageFadeIn = 300        // 页面切换淡入
const val AnimationDurationPageSlide = 300         // 页面切换滑动
const val AnimationDurationButtonClick = 150       // 按钮点击反馈
const val AnimationDurationCardExpand = 350        // 卡片展开/收起
const val AnimationDurationLoading = 1000          // 加载指示器旋转周期
const val AnimationDurationRefresh = 200           // 下拉刷新回弹

// 缓动函数 - BabyFood 设计系统
val EaseInOutCubic = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)  // ease-in-out
val EaseOutBack = EaseOutBack                                             // ease-out-back
val LinearEasing = LinearEasing                                           // linear
val EaseOutEasing = EaseOut                                               // ease-out
val SpringEasing = spring<Float>(                                         // 弹簧动画
    dampingRatio = 0.8f,
    stiffness = 300f
)

// ===== 动画规格 =====

// 页面切换动画：淡入 300ms + 横向滑动 300ms，ease-in-out
val PageTransitionAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = AnimationDurationPageFadeIn,
    easing = EaseInOutCubic
)

// 按钮点击反馈：缩放比例 0.95→1.0，150ms，弹簧动画
val ButtonClickAnimationSpec: AnimationSpec<Float> = spring(
    dampingRatio = 0.8f,
    stiffness = 300f
)

// 卡片展开/收起：高度变化 0→目标高度，350ms，ease-out-back
val CardExpandAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = AnimationDurationCardExpand,
    easing = EaseOutBack
)

// 加载指示器：旋转周期 1000ms/圈，linear，infinite 无限循环
val LoadingAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = AnimationDurationLoading,
    easing = LinearEasing
)

// 下拉刷新：回弹动画 200ms，ease-out
val RefreshAnimationSpec: AnimationSpec<Float> = tween(
    durationMillis = AnimationDurationRefresh,
    easing = EaseOutEasing
)

// ===== 动画扩展函数 =====

/**
 * 创建平滑的缩放动画（用于按钮点击反馈）
 * @param targetValue 目标值
 */
@Composable
fun animateScale(
    targetValue: Float
): Float {
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = ButtonClickAnimationSpec,
        label = "scale_animation"
    ).value
}

/**
 * 创建脉冲动画（用于强调元素）
 * @param targetValue 目标缩放值
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
                durationMillis = AnimationDurationCardExpand,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    ).value
}

/**
 * 创建淡入淡出动画（用于页面切换）
 * @param isVisible 是否可见
 */
@Composable
fun animateFadeInOut(
    isVisible: Boolean
): Float {
    val targetValue = if (isVisible) 1f else 0f
    return animateFloatAsState(
        targetValue = targetValue,
        animationSpec = PageTransitionAnimationSpec,
        label = "fade_animation"
    ).value
}

/**
 * 创建加载旋转动画
 * @param rotation 旋转角度（0-360）
 */
@Composable
fun animateRotation(
    rotation: Float
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation_animation")
    return infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = rotation,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween(
                durationMillis = ANIMATION_DURATION_LOADING_CYCLE,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    ).value
}

// ===== 动画状态管理 =====

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
        AnimationState.ENTERING -> animateFadeInOut(true)
        AnimationState.VISIBLE -> 1f
        AnimationState.EXITING -> animateFadeInOut(false)
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
        AnimationState.EXITING -> animateScale(0.95f)
        AnimationState.HIDDEN -> 0.95f
    }
}

@Composable
fun BabyFoodTheme(
    themePreference: String? = null,  // null 表示跟随系统，"light" 表示浅色，"dark" 表示深色
    content: @Composable () -> Unit
) {
    // 根据主题偏好决定使用哪个主题
    val darkTheme = when (themePreference) {
        "light" -> false
        "dark" -> true
        "auto", null -> isSystemInDarkTheme()
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // 页面背景渐变：从橙色到白色的垂直渐变（三色渐变确保底部纯白）
    val pageBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            PageGradientStart,
            PageGradientMiddle,
            PageGradientEnd
        )
    )

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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 应用背景渐变
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(pageBackgroundBrush)
        ) {
            MaterialTheme(
                colorScheme = colorScheme,
                typography = Typography,
                content = content
            )
        }
    }
}