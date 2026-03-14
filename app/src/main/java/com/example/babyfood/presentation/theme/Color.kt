package com.example.babyfood.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * BabyFood 暖阳辅食橙色板 (Plan A)
 * 核心理念：食欲、温暖、专业、护眼
 */

// ===== 品牌核心色 (Brand Colors) =====
val PrimaryOrange = Color(0xFFFF9A3C)      // 辅食橙 (核心主色)
val PrimaryOrangeLight = Color(0xFFFFB067) // 浅橙
val PrimaryOrangeDark = Color(0xFFE67E22)  // 深橙
val SecondaryYellow = Color(0xFFFFD166)    // 南瓜黄

// ===== 背景与表面色 (Background & Surface) =====
val BackgroundWarm = Color(0xFFFFF9F0)     // 燕麦奶油白
val SurfaceWhite = Color(0xFFFFFFFF)       // 纯白
val SurfaceCream = Color(0xFFFFF6E9)       // 奶白色

// ===== 文字颜色 (Typography) =====
val TextMain = Color(0xFF3D2B1F)           // 深巧克力褐
val TextSub = Color(0xFF7D6E63)            // 暖中灰
val TextMute = Color(0xFFA89A90)           // 浅暖灰

// ===== 功能状态色 (Functional Colors) =====
val StatusSuccess = Color(0xFF78C6A3)      // 薄荷绿
val StatusWarning = Color(0xFFFFD166)      // 暖黄
val StatusError = Color(0xFFFF6B6B)        // 珊瑚红
val StatusInfo = Color(0xFF8ECAE6)         // 天空蓝

// ===== 分割线与描边 (UI Elements) =====
val DividerLight = Color(0xFFF2EAE0)       // 极浅暖灰
val OutlineWarm = Color(0xFFEBDDCF)        // 描边色

// ===== 标准语义别名 (Semantic Aliases) =====
val Primary = PrimaryOrange
val PrimaryLight = PrimaryOrangeLight
val PrimaryDark = PrimaryOrangeDark
val Background = BackgroundWarm
val Surface = SurfaceWhite
val Success = StatusSuccess
val Warning = StatusWarning
val Error = StatusError

// ===== 业务代码兼容性别名 (用于修复编译错误) =====
// 主色相关
val AvocadoPrimary = PrimaryOrange
val AvocadoDark = PrimaryOrangeDark
val AvocadoBackgroundLight = BackgroundWarm
val Peach = PrimaryOrangeLight
val PeachDark = PrimaryOrangeDark
val ButtonPrimary = PrimaryOrange
val ButtonPrimaryDisabled = TextMute
val ButtonOutline = OutlineWarm

// 表面与背景
val PageBackground = BackgroundWarm
val BackgroundLight = BackgroundWarm
val GrayBackground = BackgroundWarm
val CardBackground = SurfaceWhite
val CardGradientStart = SurfaceWhite
val CardGradientEnd = BackgroundWarm
val SurfaceVariant = SurfaceCream
val OrangeContainer = SurfaceCream
val GreenContainer = StatusSuccess.copy(alpha = 0.1f)
val Orange600 = PrimaryOrangeDark
val Cream = SurfaceCream

// 文字
val TextPrimary = TextMain
val TextSecondary = TextSub
val TextTertiary = TextMute
val Charcoal = TextMain
val Gray900 = TextMain
val Gray700 = TextSub
val Gray500 = TextMute
val Gray400 = TextMute
val Gray300 = DividerLight
val SoftBrown = TextSub
val OnBackground = TextMain
val OnSurface = TextMain
val OnSurfaceVariant = TextSub

// 功能色映射
val Green = StatusSuccess
val Blue = StatusInfo
val Coral = StatusError
val Red800 = StatusError
val RedContainer = StatusError.copy(alpha = 0.1f)
val SuccessContainer = StatusSuccess.copy(alpha = 0.1f)
val OnSuccessContainer = StatusSuccess
val ErrorContainer = StatusError.copy(alpha = 0.1f)
val OnErrorContainer = StatusError
val WarningContainer = StatusWarning.copy(alpha = 0.1f)
val OnWarningContainer = StatusWarning

// 进度与营养
val GradientStart = PrimaryOrange
val GradientEnd = PrimaryOrangeLight
val NutritionGradientStart = StatusSuccess
val NutritionGradientEnd = StatusSuccess.copy(alpha = 0.7f)
val CircularProgressBackground = DividerLight
val NutritionProtein = StatusSuccess
val NutritionCalcium = StatusInfo
val NutritionCalories = PrimaryOrangeLight
val NutritionInsufficient = StatusError
val NutritionNormal = StatusSuccess
val NutritionExcess = StatusWarning

// 风险、评分与标签
val RiskNormal = StatusSuccess
val RiskNormalContainer = StatusSuccess.copy(alpha = 0.1f)
val RiskCautiousContainer = PrimaryOrangeLight.copy(alpha = 0.1f)
val RiskForbiddenContainer = StatusError.copy(alpha = 0.1f)
val RiskNotRecommendedContainer = StatusWarning.copy(alpha = 0.1f)
val RiskRequiresHandlingContainer = StatusInfo.copy(alpha = 0.1f)
val RiskForbidden = StatusError
val RiskCautious = PrimaryOrangeLight
val RiskNotRecommended = StatusWarning
val RiskRequiresHandling = StatusInfo
val ScoreExcellent = StatusSuccess
val ScoreGood = StatusSuccess.copy(alpha = 0.8f)
val ScoreMedium = StatusWarning
val ScoreFair = PrimaryOrangeLight
val ScorePoor = StatusError
val QuickCookTag = StatusSuccess
val BalancedNutritionTag = StatusInfo
val StatusFinished = StatusSuccess
val StatusHalf = PrimaryOrangeLight
val StatusAllergy = StatusError
val FreshnessLong = StatusSuccess
val StorageCannedContainer = SurfaceCream
val DifficultyEasy = StatusSuccess
val DifficultyMedium = StatusWarning
val DifficultyHard = StatusError
val FreshnessToday = StatusError
val Freshness3Days = StatusWarning
val Freshness1Week = SecondaryYellow
val StorageFreshContainer = StatusSuccess.copy(alpha = 0.1f)
val StorageFrozenContainer = StatusInfo.copy(alpha = 0.1f)
val StorageExpiryWarningContainer = StatusWarning.copy(alpha = 0.1f)

// 营养高亮
val HighlightProtein = StatusSuccess
val HighlightCalcium = StatusInfo
val HighlightIron = PrimaryOrange
val HighlightBalanced = SecondaryYellow
val HighlightLowCalorie = StatusInfo
val HighlightVitamin = StatusSuccess
val HighlightDefault = TextSub

// 图表
val ChartBaby = PrimaryOrange
val ChartBabyFill = PrimaryOrange.copy(alpha = 0.2f)
val ChartChina = StatusInfo
val ChartWHO = StatusSuccess

// 辅助
val Outline = OutlineWarm
val InputDisabledContainer = DividerLight

// ===== 特色餐段色 =====
val MealBreakfast = Color(0xFFFFB347)
val MealLunch = Color(0xFFFFD166)
val MealDinner = Color(0xFFE67E22)
val MealSnack = Color(0xFF78C6A3)
val NewIngredientColor = Color(0xFF9C27B0)
val NewIngredientContainer = Color(0xFFF3E5F5)
