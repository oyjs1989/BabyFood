package com.example.babyfood.presentation.theme

import androidx.compose.ui.graphics.Color

// ===== 基础颜色定义 =====

// 页面主背景渐变：从橙色到白色的垂直渐变（三色渐变确保底部纯白）
val PageGradientStart = Color(0xFFFF9F69)      // #FF9F69 主暖橙
val PageGradientMiddle = Color(0xFFFFE8C0)    // #FFE8C0 浅橙黄
val PageGradientEnd = Color(0xFFFFFFFF)       // #FFFFFF 纯白

// 页面主背景（用于不支持渐变的后备色）
val PageBackground = Color(0xFFFDFBF8)       // 暖米色

// 卡片/模块背景
val CardBackground = Color(0xFFFFFFFF)        // 纯白

// 文字颜色
val TextPrimary = Color(0xFF333333)           // 一级正文
val TextSecondary = Color(0xFF666666)         // 二级正文
val TextTertiary = Color(0xFF505050)          // 辅助说明文字（调整为#505050以满足WCAG AA对比度要求）

// 分割线/描边
val Outline = Color(0xFFE5E5E5)               // 分割线/描边

// ===== Material Design 3 标准颜色 =====

// 背景色
val Background = PageBackground              // 页面背景色
val OnBackground = TextPrimary               // 背景上的文字颜色

// 表面色
val Surface = CardBackground                 // 卡片/模块背景色
val OnSurface = TextPrimary                  // 表面上的文字颜色
val SurfaceVariant = Color(0xFFF0F0F0)       // 表面变体色
val OnSurfaceVariant = TextSecondary         // 表面变体上的文字颜色
val SecondaryContainer = Color(0xFFE6E6E6)   // 次要容器色

// ===== 主色调 - BabyFood 柔和护眼色彩系统 =====
// 基于柔和护眼、低饱和度、层次清晰原则

val Primary = Color(0xFFFF9F69)              // 主暖橙 - 品牌核心色
val PrimaryLight = Color(0xFFFFD4BD)         // 浅暖橙 - 延伸色，用于渐变和悬停

// ===== 辅助色 - 健康/天然/科技/温馨 =====

// 柔和绿 - 健康/天然，用于 WHO 标准参考线
val Green = Color(0xFF88C999)

// 清新蓝 - 科技/智能，用于中国标准参考线
val Blue = Color(0xFF73A6FF)

// 奶黄 - 温馨点缀
val CreamYellow = Color(0xFFE8A3)

// ===== 营养指标颜色 =====

val NutritionCalories = Color(0xFFE85D04)            // 卡路里 - 深橙红
val NutritionProtein = Green                         // 蛋白质
val NutritionCalcium = Blue                          // 钙
val NutritionIron = Color(0xFFFF6B6B)                // 铁

// ===== 功能状态色 =====

// 成功色
val Success = Color(0xFF52C41A)
val SuccessContainer = Color(0xFFE6FFCB)
val OnSuccessContainer = Color(0xFF0B5300)

// 警告色
val Warning = Color(0xFFFAAD14)
val WarningContainer = Color(0xFFFFF7D1)
val OnWarningContainer = Color(0xFF6B4800)

// 错误/危险色
val Error = Color(0xFFF5222D)
val ErrorContainer = Color(0xFFFFD7D7)
val OnErrorContainer = Color(0xFF610000)

// ===== 营养进度条全配色 =====

// 进度条已完成段
val ProgressCompleted = Primary               // 主暖橙

// 进度条未完成段
val ProgressIncomplete = Outline              // 中性灰

// 进度条组件底色
val ProgressBackground = PageBackground       // 页面背景色

// ===== 生长曲线全配色 =====

// WHO 标准参考线
val ChartWHO = Green                          // 柔和绿

// 中国标准参考线
val ChartChina = Blue                         // 清新蓝

// 宝宝实际数据线
val ChartBaby = Primary                       // 主暖橙

// 曲线下方填充区域（20% 透明度）
val ChartBabyFill = Color(0xFFE6A366)         // 主暖橙 20% 透明度

// ===== 任务完成/进行中/未完成状态色 =====

// 已完成
val TaskCompleted = Success                    // 成功色

// 进行中
val TaskInProgress = Warning                   // 警告色

// 未完成
val TaskNotStarted = Outline                   // 中性灰

// ===== 渐变色 =====

// 主按钮渐变
val GradientStart = Primary                   // #FF9F69
val GradientEnd = PrimaryLight                // #FFD4BD

// 卡片底纹渐变
val CardGradientStart = CardBackground         // #FFFFFF
val CardGradientEnd = PageBackground          // #FDFBF8

// 营养进度条渐变
val NutritionGradientStart = Green             // #88C999
val NutritionGradientEnd = Success             // #52C41A

// ===== 暗色主题配色 =====
// 使用 HSL 调整公式：背景亮度从 95% 降至 5%，主色调亮度从 75% 降至 65% 且饱和度降低 20%

// 暗色背景
val DarkPageBackground = Color(0xFF121212)    // #121212
val DarkCardBackground = Color(0xFF1E1E1E)    // #1E1E1E

// 暗色主色调（HSL 调整：亮度 65%→45%，饱和度 90%→72%）
val DarkPrimary = Color(0xFFE67A4A)           // #E67A4A

// 暗色文字
val DarkTextPrimary = Color(0xFFF5F5F5)        // #F5F5F5（亮度 90%）
val DarkTextSecondary = Color(0xFFC1C1C1)      // #C1C1C1（亮度 80%）
val DarkTextTertiary = Color(0xFF8E8E8E)       // #8E8E8E（亮度 70%）

// 暗色分割线/描边（亮度 25%）
val DarkOutline = Color(0xFF3D3D3D)            // #3D3D3D

// 暗色功能色（饱和度降低 30%，亮度提升 10%）
val DarkSuccess = Color(0xFF6BDB3A)            // 成功色
val DarkWarning = Color(0xFFFFBE4E)            // 警告色
val DarkError = Color(0xFFFF6666)             // 错误色

// ===== 半透明遮罩 =====

// 标准遮罩（用于模态层）
val Scrim = Color(0x80000000)                  // 50% 透明度黑色

// 暗色模式遮罩（80% 透明度，降低夜间刺眼度）
val DarkScrim = Color(0xCC000000)             // 80% 透明度黑色

// ===== 图标容器颜色 =====

// 线性图标描边颜色
val IconStroke = TextPrimary                   // 一级正文色

// 填充图标颜色
val IconFill = Primary                         // 主暖橙

// ===== 插画配色 =====

// 插画主色
val IllustrationPrimary = Primary             // 主暖橙

// 插画辅助色 1
val IllustrationSecondary = Green             // 柔和绿

// 插画辅助色 2
val IllustrationTertiary = Blue               // 清新蓝

// 插画中性色
val IllustrationNeutral = TextSecondary       // 二级正文