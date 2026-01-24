package com.example.babyfood.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// ===== 基础圆角形状 =====

// 小圆角 - 用于按钮、标签等小组件
val SmallRoundedCorner = 8.dp

// 中等圆角 - 用于卡片、输入框等
val MediumRoundedCorner = 12.dp

// 大圆角 - 用于大卡片、对话框等
val LargeRoundedCorner = 16.dp

// 超大圆角 - 用于模态对话框、全屏卡片等
val ExtraLargeRoundedCorner = 24.dp

// ===== 圆角形状实例 =====

val SmallRoundedCornerShape = RoundedCornerShape(SmallRoundedCorner)

val MediumRoundedCornerShape = RoundedCornerShape(MediumRoundedCorner)

val LargeRoundedCornerShape = RoundedCornerShape(LargeRoundedCorner)

val ExtraLargeRoundedCornerShape = RoundedCornerShape(ExtraLargeRoundedCorner)

// ===== 特殊圆角形状 =====

// 仅顶部圆角（用于底部弹出面板）
val TopRoundedCornerShape = RoundedCornerShape(
    topStart = ExtraLargeRoundedCorner,
    topEnd = ExtraLargeRoundedCorner,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

// 仅底部圆角
val BottomRoundedCornerShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = LargeRoundedCorner,
    bottomEnd = LargeRoundedCorner
)

// 仅左侧圆角
val LeftRoundedCornerShape = RoundedCornerShape(
    topStart = LargeRoundedCorner,
    topEnd = 0.dp,
    bottomStart = LargeRoundedCorner,
    bottomEnd = 0.dp
)

// 仅右侧圆角
val RightRoundedCornerShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = LargeRoundedCorner,
    bottomStart = 0.dp,
    bottomEnd = LargeRoundedCorner
)

// ===== 胶囊形状 =====

val CapsuleShape = RoundedCornerShape(50)

// ===== 椭圆形 =====

val OvalShape = RoundedCornerShape(50)

// ===== 裁剪形状 =====

// 对角线裁剪（需要自定义 Shape）
// 可以用于特殊设计的卡片或按钮
