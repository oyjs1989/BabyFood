package com.example.babyfood.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ===== 排版系统 - BabyFood 设计系统 =====
// 基于 WCAG 无障碍量化标准

val Typography = Typography(
    // ===== 显示文字（用于非常醒目的标题）=====
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = (26 * 1.3).sp,  // 标题行高 1.2-1.4 倍
        letterSpacing = 0.sp          // 标题字间距 0-0.2sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = (22 * 1.3).sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = (18 * 1.3).sp,
        letterSpacing = 0.sp
    ),

    // ===== 标题文字（用于页面主要部分）=====
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = (22 * 1.3).sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = (18 * 1.3).sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = (16 * 1.4).sp,
        letterSpacing = 0.sp
    ),

    // ===== 标题文字（用于列表项、卡片标题等）=====
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = (16 * 1.4).sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = (14 * 1.4).sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = (12 * 1.4).sp,
        letterSpacing = 0.sp
    ),

    // ===== 正文文字（用于主要内容）=====
    // 正文最小字号 12sp，行高 1.4-1.6 倍
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = (16 * 1.5).sp,  // 正文行高 1.4-1.6 倍
        letterSpacing = 0.1.sp         // 正文字间距 0.1sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = (14 * 1.5).sp,
        letterSpacing = 0.1.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,              // 正文最小字号
        lineHeight = (12 * 1.6).sp,
        letterSpacing = 0.1.sp
    ),

    // ===== 标签文字（用于按钮、标签等）=====
    // 按钮文字不小于 14sp
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,              // 按钮文字不小于 14sp
        lineHeight = (14 * 1.4).sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = (12 * 1.4).sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = (10 * 1.4).sp,
        letterSpacing = 0.sp
    )
)

// ===== 自定义辅助样式 =====

// 数字样式（用于数据展示）
val NumberTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = (16 * 1.5).sp,
    letterSpacing = 0.sp
)

// 卡片标题样式
val CardTitleStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = (16 * 1.4).sp,
    letterSpacing = 0.sp
)

// 列表项主文本样式
val ListItemPrimaryStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = (14 * 1.5).sp,
    letterSpacing = 0.sp
)

// 列表项副文本样式
val ListItemSecondaryStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = (12 * 1.6).sp,
    letterSpacing = 0.1.sp
)

// 营养数据标签样式
val NutritionLabelStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = (12 * 1.6).sp,
    letterSpacing = 0.1.sp
)

// 营养数值样式
val NutritionValueStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = (14 * 1.4).sp,
    letterSpacing = 0.sp
)

// 按钮文本样式
val ButtonTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,              // 按钮文字不小于 14sp
    lineHeight = (14 * 1.4).sp,
    letterSpacing = 0.2.sp
)

// ===== 友好的圆角字体样式（用于宝宝相关内容）=====
val FriendlyHeadingStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp,
    lineHeight = (18 * 1.3).sp,
    letterSpacing = 0.2.sp          // 增加字间距，营造友好感
)

val FriendlyBodyStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = (14 * 1.6).sp,
    letterSpacing = 0.3.sp          // 增加字间距，提升可读性（首选 0.3sp）
)

// ===== 辅助文本样式（用于次要信息）=====
val HelperTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = (12 * 1.6).sp,
    letterSpacing = 0.1.sp
)