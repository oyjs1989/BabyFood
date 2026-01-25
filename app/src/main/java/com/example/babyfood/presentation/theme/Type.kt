package com.example.babyfood.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ===== 基础排版系统 - Soft UI Evolution 风格 =====
// 优化行高和字间距，提升可读性和视觉层次

val Typography = Typography(
    // ===== 显示文字（用于非常醒目的标题，如大横幅）=====
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 57.sp,
        lineHeight = 64.sp,  // 优化行高
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 45.sp,
        lineHeight = 52.sp,  // 优化行高
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,  // 优化行高
        letterSpacing = 0.sp
    ),

    // ===== 标题文字（用于页面主要部分和重要卡片）=====
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,  // 优化行高
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,  // 优化行高
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,  // 优化行高
        letterSpacing = 0.sp
    ),

    // ===== 标题文字（用于列表项、卡片标题等）=====
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,  // 优化行高
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,  // 优化行高
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,  // 优化行高
        letterSpacing = 0.1.sp
    ),

    // ===== 正文文字（用于主要内容）=====
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,  // 优化行高，提升可读性
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,  // 优化行高
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,  // 优化行高
        letterSpacing = 0.4.sp
    ),

    // ===== 标签文字（用于按钮、标签等）=====
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,  // 优化行高
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 18.sp,  // 优化行高
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,  // 优化行高
        letterSpacing = 0.5.sp
    )
)

// ===== 自定义辅助样式 - Soft UI Evolution 风格 =====

// 数字样式（用于数据展示）
val NumberTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = (-0.5).sp
)

// 卡片标题样式
val CardTitleStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp
)

// 列表项主文本样式
val ListItemPrimaryStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.sp
)

// 列表项副文本样式
val ListItemSecondaryStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.25.sp
)

// 营养数据标签样式
val NutritionLabelStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.25.sp
)

// 营养数值样式
val NutritionValueStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = (-0.5).sp
)

// 按钮文本样式
val ButtonTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.5.sp
)

// ===== 新增：友好的圆角字体样式（用于宝宝相关内容）=====
val FriendlyHeadingStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.5.sp  // 增加字间距，营造友好感
)

val FriendlyBodyStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.3.sp  // 增加字间距，提升可读性
)

// ===== 新增：强调文本样式 =====
val EmphasizedTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    letterSpacing = 0.sp
)

// ===== 新增：辅助文本样式（用于次要信息）=====
val HelperTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.2.sp
)