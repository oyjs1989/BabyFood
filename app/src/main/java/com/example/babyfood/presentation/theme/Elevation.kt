package com.example.babyfood.presentation.theme

import androidx.compose.ui.unit.dp

// ===== 阴影分级规范 - BabyFood 设计系统 =====
// 遵循 MD3 高程逻辑，适配 Apple 精致阴影质感

// ===== Level 1 =====
// 应用组件：普通列表卡片、标签、图标容器
val ElevationLevel1 = 2.dp
val ShadowBlurLevel1 = 4.dp

// ===== Level 2 =====
// 应用组件：弹窗、底部导航栏、悬浮按钮
val ElevationLevel2 = 8.dp
val ShadowBlurLevel2 = 16.dp

// ===== Level 3 =====
// 应用组件：模态层、全屏浮层、重要提示弹窗
val ElevationLevel3 = 24.dp
val ShadowBlurLevel3 = 32.dp

// ===== 特殊高程 =====

// 悬停状态（用于按钮悬停）
val ElevationHover = 12.dp

// 按下状态（用于按钮点击）
val ElevationPressed = 0.dp

// ===== 高程状态枚举 =====
enum class ElevationLevel(val elevation: Float, val shadowBlur: Float) {
    LEVEL1(ElevationLevel1.value, ShadowBlurLevel1.value),
    LEVEL2(ElevationLevel2.value, ShadowBlurLevel2.value),
    LEVEL3(ElevationLevel3.value, ShadowBlurLevel3.value),
    HOVER(ElevationHover.value, ShadowBlurLevel2.value),
    PRESSED(ElevationPressed.value, 0f)
}