package com.example.babyfood.data.remote.api

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * AI 推荐服务 API 接口
 * 用于调用云端 AI 生成辅食推荐计划
 */
interface RecommendationApiService {

    /**
     * 生成周饮食计划
     * 
     * 请求格式（JSON）：
     * {
     *   "babyId": 1,
     *   "ageInMonths": 14,
     *   "allergies": ["鸡蛋", "牛奶"],
     *   "preferences": ["南瓜", "胡萝卜"],
     *   "availableIngredients": ["鸡肉", "菠菜"],
     *   "useAvailableIngredientsOnly": false,
     *   "constraints": {
     *     "maxFishPerWeek": 2,
     *     "maxEggPerWeek": 3,
     *     "breakfastComplexity": "MODERATE",
     *     "maxDailyMeals": 4
     *   },
     *   "startDate": "2026-01-24",
     *   "days": 7
     * }
     * 
     * 响应格式（JSON）：
     * {
     *   "success": true,
     *   "errorMessage": null,
     *   "weeklyPlan": {
     *     "startDate": "2026-01-24",
     *     "endDate": "2026-01-30",
     *     "dailyPlans": [
     *       {
     *         "date": "2026-01-24",
     *         "meals": [
     *           {
     *             "mealPeriod": "BREAKFAST",
     *             "recipeId": 1,
     *             "recipeName": "南瓜米糊",
     *             "nutritionNotes": "提供优质蛋白质和碳水化合物",
     *             "childFriendlyText": "早餐有南瓜米糊，香香的很好吃哦～"
     *           },
     *           {
     *             "mealPeriod": "LUNCH",
     *             "recipeId": 15,
     *             "recipeName": "鸡肉粥",
     *             "nutritionNotes": "提供丰富的蛋白质、维生素和矿物质",
     *             "childFriendlyText": "午餐有鸡肉粥，营养满满！"
     *           }
     *         ]
     *       }
     *     ],
     *     "nutritionSummary": {
     *       "weeklyCalories": 3500.0,
     *       "weeklyProtein": 175.0,
     *       "weeklyCalcium": 2800.0,
     *       "weeklyIron": 70.0,
     *       "dailyAverage": {
     *         "calories": 500.0,
     *         "protein": 25.0,
     *         "calcium": 400.0,
     *         "iron": 10.0
     *       },
     *       "highlights": [
     *         "蛋白质摄入充足，有助于肌肉发育",
     *         "钙摄入充足，有助于骨骼发育"
     *       ]
     *     }
     *   },
     *   "warnings": [
     *     "平均热量偏低，建议增加主食摄入"
     *   ]
     * }
     * 
     * @param request 推荐请求
     * @return 推荐响应
     */
    @POST("api/recommendation/generate")
    suspend fun generateRecommendation(
        @Body request: RecommendationRequest
    ): RecommendationResponse
}

/**
 * 推荐请求
 */
@kotlinx.serialization.Serializable
data class RecommendationRequest(
    val babyId: Long,
    val ageInMonths: Int,
    val allergies: List<String>,
    val preferences: List<String>,
    val availableIngredients: List<String>,
    val useAvailableIngredientsOnly: Boolean,
    val constraints: RecommendationConstraintsDto,
    val startDate: String,  // ISO 8601 格式: "2026-01-24"
    val days: Int
)

/**
 * 推荐约束条件
 */
@kotlinx.serialization.Serializable
data class RecommendationConstraintsDto(
    val maxFishPerWeek: Int,
    val maxEggPerWeek: Int,
    val breakfastComplexity: String,  // "SIMPLE", "MODERATE", "COMPLEX"
    val maxDailyMeals: Int
)

/**
 * 推荐响应
 */
@kotlinx.serialization.Serializable
data class RecommendationResponse(
    val success: Boolean,
    val errorMessage: String?,
    val weeklyPlan: WeeklyPlanDto?,
    val warnings: List<String>
)

/**
 * 周计划
 */
@kotlinx.serialization.Serializable
data class WeeklyPlanDto(
    val startDate: String,
    val endDate: String,
    val dailyPlans: List<DailyPlanDto>,
    val nutritionSummary: NutritionSummaryDto
)

/**
 * 日计划
 */
@kotlinx.serialization.Serializable
data class DailyPlanDto(
    val date: String,
    val meals: List<MealDto>
)

/**
 * 餐食
 */
@kotlinx.serialization.Serializable
data class MealDto(
    val mealPeriod: String,  // "BREAKFAST", "LUNCH", "DINNER", "SNACK"
    val recipeId: Long,      // 食谱 ID，用于本地解析
    val recipeName: String,  // 食谱名称，用于显示
    val nutritionNotes: String,
    val childFriendlyText: String
)

/**
 * 营养摘要
 */
@kotlinx.serialization.Serializable
data class NutritionSummaryDto(
    val weeklyCalories: Double,
    val weeklyProtein: Double,
    val weeklyCalcium: Double,
    val weeklyIron: Double,
    val dailyAverage: DailyNutritionAverageDto,
    val highlights: List<String>
)

/**
 * 每日营养平均值
 */
@kotlinx.serialization.Serializable
data class DailyNutritionAverageDto(
    val calories: Double,
    val protein: Double,
    val calcium: Double,
    val iron: Double
)