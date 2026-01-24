package com.example.babyfood.domain.model

import kotlinx.datetime.LocalDate

/**
 * AI 推荐辅食请求
 */
data class RecommendationRequest(
    val babyId: Long,
    val ageInMonths: Int,
    val allergies: List<String>,
    val preferences: List<String>,
    val availableIngredients: List<String> = emptyList(),
    val useAvailableIngredientsOnly: Boolean = false,
    val constraints: RecommendationConstraints = RecommendationConstraints(),
    val startDate: LocalDate = kotlinx.datetime.LocalDate.fromEpochDays((kotlinx.datetime.Clock.System.now().toEpochMilliseconds() / 86400000).toInt())
)

/**
 * 推荐约束条件
 */
data class RecommendationConstraints(
    val maxFishPerWeek: Int = 2,
    val maxEggPerWeek: Int = 3,
    val breakfastComplexity: ComplexityLevel = ComplexityLevel.SIMPLE,
    val maxDailyMeals: Int = 4,
    val avoidIngredients: List<String> = emptyList()
)

/**
 * 制作难度级别
 */
enum class ComplexityLevel {
    SIMPLE,     // 简单（10-15分钟）
    MODERATE,   // 中等（15-30分钟）
    COMPLEX     // 复杂（30分钟以上）
}

/**
 * 一周饮食计划
 */
data class WeeklyMealPlan(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailyPlans: List<DailyMealPlan>,
    val nutritionSummary: NutritionSummary,
    val alternativeOptions: Map<MealPeriod, List<RecipeAlternative>>
)

/**
 * 每日饮食计划
 */
data class DailyMealPlan(
    val date: LocalDate,
    val meals: List<PlannedMeal>
)

/**
 * 计划的餐食
 */
data class PlannedMeal(
    val mealPeriod: MealPeriod,
    val recipe: Recipe,
    val nutritionNotes: String,           // 营养说明（给家长看）
    val childFriendlyText: String,        // 宝宝友好文案（给宝宝听）
    val alternatives: List<RecipeAlternative> = emptyList()
)

/**
 * 食谱替代方案
 */
data class RecipeAlternative(
    val recipe: Recipe,
    val reason: String,  // 替代原因
    val nutritionDifference: String  // 营养差异说明
)

/**
 * 营养摘要
 */
data class NutritionSummary(
    val weeklyCalories: Double,
    val weeklyProtein: Double,
    val weeklyCalcium: Double,
    val weeklyIron: Double,
    val dailyAverage: DailyNutritionAverage,
    val highlights: List<String>  // 营养亮点
)

/**
 * 每日平均营养
 */
data class DailyNutritionAverage(
    val calories: Double,
    val protein: Double,
    val calcium: Double,
    val iron: Double
)

/**
 * AI 推荐响应
 */
data class RecommendationResponse(
    val success: Boolean,
    val weeklyPlan: WeeklyMealPlan? = null,
    val errorMessage: String? = null,
    val warnings: List<String> = emptyList()
)