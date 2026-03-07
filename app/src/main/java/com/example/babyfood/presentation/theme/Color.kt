package com.example.babyfood.presentation.theme

import androidx.compose.ui.graphics.Color

// ===== Avocado Design System (New v2.0) =====
val AvocadoPrimary = Color(0xFF98FB98)
val AvocadoDark = Color(0xFF2D5A27)
val AvocadoBackgroundLight = Color(0xFFF5F8F5)
val AvocadoBackgroundDark = Color(0xFF102210)
val AvocadoCream = Color(0xFFFDFDF5)

// ===== HTML Design System Colors =====
// 主色调
val Peach = Color(0xFFFFDAB9)           // 桃色 - 温暖柔和
val PeachDark = Color(0xFFFFB6A0)       // 深桃色
val Cream = Color(0xFFF5F5DC)           // 奶油色
val Coral = Color(0xFFFFB6C1)           // 珊瑚粉
val CoralDark = Color(0xFFFF6B6B)       // 深珊瑚色
val SoftBrown = Color(0xFF8B7355)       // 柔和棕色
val Charcoal = Color(0xFF333333)        // 炭灰色
val BackgroundLight = Color(0xFFFFF9F0) // 浅暖背景
val BackgroundDark = Color(0xFF2D2A26)  // 深色背景

// 文字颜色 (v2.0)
val AvocadoTextPrimary = Color(0xFF0F172A) // Slate 900
val AvocadoTextSecondary = Color(0xFF64748B) // Slate 500
val AvocadoTextDarkPrimary = Color(0xFFF1F5F9) // Slate 100

// ===== 基础颜色定义 =====

// 页面主背景渐变：从淡绿到白色的垂直渐变（三色渐变确保底部纯白）
val PageGradientStart = Color(0xFF98FB98)      // #98FB98 淡草绿
val PageGradientMiddle = Color(0xFFC8F7C8)    // #C8F7C8 浅绿
val PageGradientEnd = Color(0xFFFFFFFF)       // #FFFFFF 纯白

// 页面主背景（用于不支持渐变的后备色）
val PageBackground = Color(0xFFFFF9F0)       // 浅奶油白（设计规范）

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

val Primary = Color(0xFF98FB98)              // 淡草绿 - 品牌核心色（设计规范）
val PrimaryLight = Color(0xFFC8F7C8)         // 浅草绿 - 延伸色，用于渐变和悬停

// ===== 辅助色 - 健康/天然/科技/温馨 =====

// 柔和绿 - 健康/天然，用于 WHO 标准参考线
val Green = Color(0xFF88C999)

// 清新蓝 - 科技/智能，用于中国标准参考线
val Blue = Color(0xFF73A6FF)

// 奶黄 - 温馨点缀
val CreamYellow = Color(0xFFE8A3)

// ===== 营养指标颜色 =====

val NutritionCalories = Peach                        // 卡路里 - 桃色（设计规范）
val NutritionProtein = AvocadoPrimary                // 蛋白质 - 淡草绿
val NutritionCalcium = Peach                         // 钙 - 桃色
val NutritionIron = Coral                            // 铁 - 浅珊瑚红

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
val DarkPageBackground = Color(0xFF2D2A26)    // #2D2A26 深棕灰（设计规范）
val DarkCardBackground = Color(0xFF3D3A36)    // #3D3A36 卡片深棕灰

// 暗色主色调（HSL 调整：亮度 65%→45%，饱和度 90%→72%）
val DarkPrimary = Color(0xFF4CAF50)           // #4CAF50 暗绿色

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

// ===== 风险等级颜色（Risk Level Colors）=====

// 禁止 - 红色
val RiskForbidden = Color(0xFFFF5252)
val RiskForbiddenContainer = Color(0xFFFFEBEE)

// 不推荐 - 黄色
val RiskNotRecommended = Color(0xFFFFC107)
val RiskNotRecommendedContainer = Color(0xFFFFF8E1)

// 需要特殊处理 - 蓝色
val RiskRequiresHandling = Color(0xFF2196F3)
val RiskRequiresHandlingContainer = Color(0xFFE3F2FD)

// 谨慎尝试 - 橙色
val RiskCautious = Color(0xFFFF9800)
val RiskCautiousContainer = Color(0xFFFFF3E0)

// 正常 - 绿色
val RiskNormal = Color(0xFF4CAF50)
val RiskNormalContainer = Color(0xFFE8F5E9)

// ===== 营养评分颜色（Nutrition Score Colors）=====

// 优秀 (90+)
val ScoreExcellent = Color(0xFF4CAF50)
val ScoreExcellentContainer = Color(0xFFE8F5E9)

// 良好 (75-89)
val ScoreGood = Color(0xFF8BC34A)

// 中等 (60-74)
val ScoreMedium = Color(0xFFFFC107)

// 一般 (40-59)
val ScoreFair = Color(0xFFFF9800)

// 较差 (<40)
val ScorePoor = Color(0xFFF44336)

// ===== 餐次标签颜色（Meal Period Colors）=====

// 早餐 - 橙色
val MealBreakfast = Color(0xFFFFB347)

// 午餐 - 天蓝色
val MealLunch = Color(0xFF87CEEB)

// 晚餐 - 淡紫色
val MealDinner = Color(0xFFDDA0DD)

// 点心 - 淡绿色
val MealSnack = Color(0xFF98FB98)

// ===== 食材标签颜色（Ingredient Tag Colors）=====

// 新食材标签 - 紫色
val NewIngredientTag = Color(0xFF9C27B0)
val NewIngredientContainer = Color(0xFFF3E5F5)

// 快手标签
val QuickCookTag = Color(0xFF2EC77C)

// 营养均衡标签
val BalancedNutritionTag = Color(0xFF2EB9A0)

// ===== 进食状态颜色（Eating Status Colors）=====

// 完成 - 绿色
val StatusFinished = Color(0xFF2EC77C)

// 吃了一半 - 橙色
val StatusHalf = Color(0xFFFFB347)

// 不喜欢 - 橙色
val StatusDisliked = Color(0xFFFFB347)

// 过敏 - 红色
val StatusAllergy = Color(0xFFE74C3C)

// ===== 难度等级颜色（Difficulty Colors）=====

// 简单 (1-2)
val DifficultyEasy = Color(0xFF4CAF50)

// 中等 (3-4)
val DifficultyMedium = Color(0xFFFF9800)

// 困难 (5+)
val DifficultyHard = Color(0xFFF44336)

// ===== 新鲜度颜色（Freshness Colors）=====

// 当天 - 红色
val FreshnessToday = Color(0xFFFF5252)

// 3天内 - 橙色
val Freshness3Days = Color(0xFFFF9800)

// 1周内 - 黄色
val Freshness1Week = Color(0xFFFFC107)

// 较长 - 绿色
val FreshnessLong = Color(0xFF4CAF50)

// ===== 营养高亮颜色（Nutrition Highlight Colors）=====

// 蛋白质/高蛋白
val HighlightProtein = Color(0xFF4CAF50)

// 钙/高钙
val HighlightCalcium = Color(0xFF2196F3)

// 铁/高铁
val HighlightIron = Color(0xFFFF9800)

// 低热量
val HighlightLowCalorie = Color(0xFF03A9F4)

// 富含维生素
val HighlightVitamin = Color(0xFF8BC34A)

// 营养均衡
val HighlightBalanced = Color(0xFF9C27B0)

// 默认高亮
val HighlightDefault = Color(0xFF607D8B)

// ===== 存储建议颜色（Storage Advice Colors）=====

// 新鲜推荐
val StorageFreshContainer = Color(0xFFE8F5E9)

// 冷冻推荐
val StorageFrozenContainer = Color(0xFFE3F2FD)

// 罐装可接受
val StorageCannedContainer = Color(0xFFF3E5F5)

// 注意保质期
val StorageExpiryWarningContainer = Color(0xFFFFF3E0)

// ===== 交互组件颜色（Interactive Component Colors）=====

// 主按钮颜色
val ButtonPrimary = Color(0xFFFF7F3E)
val ButtonPrimaryDisabled = Color(0xFFE0E0E0)

// 按钮描边
val ButtonOutline = Color(0xFFDDDDDD)

// 输入框禁用背景
val InputDisabledContainer = Color(0xFFF5F5F5)

// 分割线（细）
val DividerLight = Color(0xFFF0F0F0)

// 圆形进度条背景
val CircularProgressBackground = Color(0xFFE5E7EB)
val CircularProgressBackgroundLight = Color(0xFFE0E0E0)

// ===== 灰度色板（Grayscale）=====

// 纯黑
val PureBlack = Color(0xFF000000)

// 深灰（一级文字）
val Gray900 = Color(0xFF333333)

// 中深灰（二级文字）
val Gray700 = Color(0xFF666666)

// 中灰（三级文字）
val Gray500 = Color(0xFF999999)

// 浅灰（辅助文字）
val Gray400 = Color(0xFF9CA3AF)

// 淡灰（占位文字）
val Gray300 = Color(0xFFE5E7EB)

// 背景灰
val GrayBackground = Color(0xFFF8F9FA)

// ===== 橙色色板（Orange Palette）=====

// 深橙
val Orange600 = Color(0xFFEA580C)

// 橙色容器
val OrangeContainer = Color(0xFFFFEDD5)

// ===== 绿色色板（Green Palette）=====

// 绿色容器
val GreenContainer = Color(0xFFDCFCE7)

// ===== 红色色板（Red Palette）=====

// 深红
val Red800 = Color(0xFFC62828)

// 红色容器
val RedContainer = Color(0xFFFFEBEE)