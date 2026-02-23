package com.example.babyfood.presentation.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

// ===== 动画量化标准 - BabyFood 设计系统 =====

// 动画类型和参数细则

// ===== 通用动画常量 =====
const val AnimationDurationNormal = 300      // 标准动画时长
val AnimationEasing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)  // 标准缓动函数

// ===== 页面切换 =====
// 淡入 300ms + 横向滑动 300ms，缓动函数：ease-in-out
const val ANIMATION_DURATION_PAGE_FADE_IN = 300        // 页面切换淡入
const val ANIMATION_DURATION_PAGE_SLIDE = 300         // 页面切换横向滑动

// ===== 按钮点击反馈 =====
// 缩放比例：0.95→1.0，时长 150ms，动画类型：spring 弹性动画
const val ANIMATION_DURATION_BUTTON_CLICK = 150       // 按钮点击反馈
const val ANIMATION_SCALE_BUTTON_START = 0.95f        // 按钮点击起始缩放
const val ANIMATION_SCALE_BUTTON_END = 1.0f           // 按钮点击结束缩放

// ===== 卡片展开/收起 =====
// 高度变化：0→目标高度，时长 350ms，缓动函数：ease-out-back
const val ANIMATION_DURATION_CARD_EXPAND = 350        // 卡片展开/收起

// ===== 加载指示器 =====
// 旋转周期 1000ms / 圈，缓动函数：linear，循环模式：infinite 无限循环
const val ANIMATION_DURATION_LOADING_CYCLE = 1000     // 加载指示器旋转周期
const val ANIMATION_ROTATION_FULL = 360f              // 完整旋转角度

// ===== 下拉刷新 =====
// 回弹动画 200ms，缓动函数：ease-out
const val ANIMATION_DURATION_REFRESH = 200           // 下拉刷新回弹

// ===== 缓动函数 =====

// ease-in-out（用于页面切换）
val EasingEaseInOut = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f)

// ease-out-back（用于卡片展开）
val EasingEaseOutBack = EaseOutBack

// ease-out（用于下拉刷新）
val EasingEaseOut = EaseOut

// linear（用于加载指示器）
val EasingLinear = LinearEasing

// spring（用于按钮点击）
val EasingSpring = spring<Float>(
    dampingRatio = 0.8f,  // 阻尼比（0.0-1.0，越小越弹）
    stiffness = 300f      // 刚度（越小越软）
)

// ===== 动画规格 =====

// 页面切换动画规格
val AnimationSpecPageTransition: AnimationSpec<Float> = tween(
    durationMillis = ANIMATION_DURATION_PAGE_FADE_IN,
    easing = EasingEaseInOut
)

// 按钮点击动画规格
val AnimationSpecButtonClick: AnimationSpec<Float> = spring(
    dampingRatio = 0.8f,
    stiffness = 300f
)

// 卡片展开动画规格
val AnimationSpecCardExpand: AnimationSpec<Float> = tween(
    durationMillis = ANIMATION_DURATION_CARD_EXPAND,
    easing = EasingEaseOutBack
)

// 加载指示器动画规格
val AnimationSpecLoading: AnimationSpec<Float> = tween(
    durationMillis = ANIMATION_DURATION_LOADING_CYCLE,
    easing = EasingLinear
)

// 刷新动画规格
val AnimationSpecRefresh: AnimationSpec<Float> = tween(
    durationMillis = ANIMATION_DURATION_REFRESH,
    easing = EasingEaseOut
)

// ===== 无限循环动画 =====

// 加载旋转动画（无限循环）
val AnimationSpecLoadingInfinite = infiniteRepeatable<Float>(
    animation = tween(
        durationMillis = ANIMATION_DURATION_LOADING_CYCLE,
        easing = EasingLinear
    ),
    repeatMode = RepeatMode.Restart
)

// 脉冲动画（无限循环，用于强调元素）
val AnimationSpecPulseInfinite = infiniteRepeatable<Float>(
    animation = tween(
        durationMillis = ANIMATION_DURATION_CARD_EXPAND,
        easing = EasingEaseInOut
    ),
    repeatMode = RepeatMode.Reverse
)

// ===== 性能目标 =====

// 动画性能目标：60fps，最多每秒掉帧 2 次（平均 16.6ms/帧）
const val TARGET_FPS = 60
const val MAX_FRAME_DROPS_PER_SECOND = 2
const val AVERAGE_FRAME_TIME_MS = 16.6f

// ===== 减少运动偏好 =====

// 如果用户启用了减少运动偏好，应该使用更简单的动画或禁用动画
// 可以通过 isSystemReducedMotion() 检查系统设置