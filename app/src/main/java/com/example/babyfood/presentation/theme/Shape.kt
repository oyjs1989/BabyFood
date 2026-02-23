package com.example.babyfood.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// ===== 圆角分级规格 - BabyFood 设计系统 =====
// 基于柔和护眼、层次清晰原则

// ===== 按钮圆角 =====

// 全尺寸主按钮、核心操作按钮
val ButtonPrimaryRadius = 24.dp
val ButtonPrimaryShape = RoundedCornerShape(ButtonPrimaryRadius)

// 小型标签按钮、次要功能按钮
val ButtonSecondaryRadius = 16.dp
val ButtonSecondaryShape = RoundedCornerShape(ButtonSecondaryRadius)

// ===== 卡片圆角 =====

// 首页餐单大卡片、食谱详情大卡片
val CardLargeRadius = 24.dp
val CardLargeShape = RoundedCornerShape(CardLargeRadius)

// 食谱列表小卡片、库存条目卡片
val CardSmallRadius = 20.dp
val CardSmallShape = RoundedCornerShape(CardSmallRadius)

// 营养标签、状态标签
val CardLabelRadius = 16.dp
val CardLabelShape = RoundedCornerShape(CardLabelRadius)

// ===== 输入框圆角 =====

// 文本输入框、搜索框、表单输入域
val InputFieldRadius = 16.dp
val InputFieldShape = RoundedCornerShape(InputFieldRadius)

// ===== 弹窗圆角 =====

// 全局弹窗、模态框、底部浮层
val DialogRadius = 28.dp
val DialogShape = RoundedCornerShape(DialogRadius)

// ===== 图标容器圆角 =====

// 小型图标外容器
val IconSmallRadius = 12.dp
val IconSmallShape = RoundedCornerShape(IconSmallRadius)

// 中型、大型图标外容器
val IconMediumRadius = 16.dp
val IconMediumShape = RoundedCornerShape(IconMediumRadius)

// ===== 特殊圆角形状 =====

// 仅顶部圆角（用于底部弹出面板）
val TopRoundedCornerShape = RoundedCornerShape(
    topStart = DialogRadius,
    topEnd = DialogRadius,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

// 仅底部圆角
val BottomRoundedCornerShape = RoundedCornerShape(
    topStart = 0.dp,
    topEnd = 0.dp,
    bottomStart = CardLargeRadius,
    bottomEnd = CardLargeRadius
)

// ===== 胶囊形状 =====

val CapsuleShape = RoundedCornerShape(50)

// ===== 通用圆角形状 =====

// 大圆角（用于主要卡片）
val LargeRoundedCornerShape = RoundedCornerShape(24.dp)

// 中等圆角（用于次要卡片）
val MediumRoundedCornerShape = RoundedCornerShape(16.dp)

// 按钮圆角
val ButtonShape = RoundedCornerShape(24.dp)

// ===== 响应式间距系统 - 8dp 网格 =====

// 最小单位
val GridUnit = 8.dp

// 标准间距
val SpacingXS = GridUnit * 1     // 8dp  - 紧密元素
val SpacingSM = GridUnit * 2     // 16dp - 标准间距
val SpacingMD = GridUnit * 3     // 24dp - 分组元素
val SpacingLG = GridUnit * 4     // 32dp - 主要区块
val SpacingXL = GridUnit * 6     // 48dp - 大间距

// ===== 触控区域尺寸 =====

// 最小触控区域
val TouchTargetMinimum = 44.dp

// 推荐触控区域
val TouchTargetRecommended = 48.dp

// ===== 图标尺寸 =====

val IconSizeSmall = 24.dp
val IconSizeMedium = 32.dp
val IconSizeLarge = 48.dp

// ===== 组件尺寸 =====

// 按钮高度
val ButtonHeightSmall = 40.dp
val ButtonHeightMedium = 48.dp
val ButtonHeightLarge = 56.dp

// 卡片最小高度
val CardHeightSmall = 120.dp
val CardHeightMedium = 160.dp
val CardHeightLarge = 200.dp