package com.example.babyfood.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// ===== 基础圆角形状 - Soft UI Evolution 风格 =====

// 小圆角 - 用于标签等小组件
val SmallRoundedCorner = 8.dp

// 中等圆角 - 用于卡片、输入框等
val MediumRoundedCorner = 12.dp

// 大圆角 - 用于对话框等
val LargeRoundedCorner = 16.dp

// 超大圆角 - 用于模态对话框、全屏卡片等
val ExtraLargeRoundedCorner = 24.dp

// ===== 圆角形状实例 - Soft UI Evolution 风格 =====

val SmallRoundedCornerShape = RoundedCornerShape(SmallRoundedCorner)

val MediumRoundedCornerShape = RoundedCornerShape(MediumRoundedCorner)

val LargeRoundedCornerShape = RoundedCornerShape(LargeRoundedCorner)

val ExtraLargeRoundedCornerShape = RoundedCornerShape(ExtraLargeRoundedCorner)

// ===== 按钮形状 - Soft UI Evolution 风格 =====

// 胶囊按钮形状 - 用于主要按钮
val ButtonShape = RoundedCornerShape(50)

// ===== 特殊圆角形状 - Soft UI Evolution 风格 =====

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

// ===== 新增：响应式间距系统 =====

// 基础间距单位
val SpacingUnit = 4.dp

// 小间距（8dp）- 用于紧密元素
val SpacingSmall = SpacingUnit * 2

// 中等间距（16dp）- 用于标准间距
val SpacingMedium = SpacingUnit * 4

// 大间距（24dp）- 用于分组元素
val SpacingLarge = SpacingUnit * 6

// 超大间距（32dp）- 用于主要区块
val SpacingExtraLarge = SpacingUnit * 8

// ===== 新增：响应式内边距 =====

// 标准内边距（16dp）
val PaddingStandard = SpacingMedium

// 紧凑内边距（12dp）
val PaddingCompact = SpacingUnit * 3

// 宽松内边距（24dp）
val PaddingComfortable = SpacingLarge

// ===== 新增：响应式尺寸 =====

// 小型图标（16dp）
val IconSizeSmall = SpacingUnit * 4

// 中型图标（24dp）
val IconSizeMedium = SpacingUnit * 6

// 大型图标（32dp）
val IconSizeLarge = SpacingUnit * 8

// 超大型图标（48dp）
val IconSizeExtraLarge = SpacingUnit * 12

// 小型按钮高度（40dp）
val ButtonHeightSmall = SpacingUnit * 10

// 中型按钮高度（48dp）
val ButtonHeightMedium = SpacingUnit * 12

// 大型按钮高度（56dp）
val ButtonHeightLarge = SpacingUnit * 14

// 小型卡片高度（120dp）
val CardHeightSmall = SpacingUnit * 30

// 中型卡片高度（160dp）
val CardHeightMedium = SpacingUnit * 40

// 大型卡片高度（200dp）
val CardHeightLarge = SpacingUnit * 50

// ===== 新增：响应式阴影 =====

// 轻微阴影（用于悬浮元素）
val ElevationSmall = 2.dp

// 中等阴影（用于卡片）
val ElevationMedium = 4.dp

// 大阴影（用于模态对话框）
val ElevationLarge = 8.dp

// 超大阴影（用于强调元素）
val ElevationExtraLarge = 12.dp

// ===== 新增：响应式容器宽度 =====

// 小容器（用于紧凑内容）
val ContainerWidthSmall = 320.dp

// 中等容器（用于标准内容）
val ContainerWidthMedium = 480.dp

// 大容器（用于宽屏内容）
val ContainerWidthLarge = 640.dp

// 超大容器（用于全宽内容）
val ContainerWidthExtraLarge = 960.dp