package com.example.babyfood.data.ai

import com.example.babyfood.data.ai.ruleengine.RuleEngine
import com.example.babyfood.data.ai.ruleengine.ValidationConstraints
import com.example.babyfood.data.ai.strategy.CheapModelStrategy
import com.example.babyfood.data.ai.strategy.MainModelStrategy
import com.example.babyfood.domain.model.RecommendationRequest
import com.example.babyfood.domain.model.RecommendationResponse
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.InventoryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI 推荐服务
 * 整合规则层和模型策略，提供完整的辅食推荐功能
 */
@Singleton
class RecommendationService @Inject constructor(
    private val candidateRecipeService: CandidateRecipeService,
    private val mainModelStrategy: MainModelStrategy,
    private val cheapModelStrategy: CheapModelStrategy,
    private val ruleEngine: RuleEngine,
    private val babyRepository: BabyRepository,
    private val inventoryRepository: InventoryRepository
) {

    private val TAG = "AI推荐服务"

    /**
     * 生成辅食推荐
     * @param request 推荐请求
     * @return 推荐响应
     */
    suspend fun generateRecommendation(request: RecommendationRequest): RecommendationResponse {
        return try {
            logRequestData(request)
            android.util.Log.d(TAG, "步骤1: 开始筛选候选食谱")
            android.util.Log.d(TAG, "  年龄范围: ${request.ageInMonths}个月")
            android.util.Log.d(TAG, "  过敏食材: ${request.allergies.joinToString(", ")}")
            android.util.Log.d(TAG, "  偏好食材: ${request.preferences.joinToString(", ")}")
            android.util.Log.d(TAG, "  可用食材: ${request.availableIngredients.joinToString(", ")}")
            android.util.Log.d(TAG, "  仅使用可用食材: ${request.useAvailableIngredientsOnly}")

            // 1. 获取候选食谱集合（规则层过滤）
            val candidateSet = candidateRecipeService.getCandidateRecipes(
                ageInMonths = request.ageInMonths,
                allergies = request.allergies,
                preferences = request.preferences,
                availableIngredients = request.availableIngredients,
                avoidIngredients = request.constraints.avoidIngredients,
                useAvailableIngredientsOnly = request.useAvailableIngredientsOnly
            )

            android.util.Log.d(TAG, "  ✓ 候选食谱筛选完成")
            android.util.Log.d(TAG, "    - 早餐候选数: ${candidateSet.breakfast.size}")
            android.util.Log.d(TAG, "    - 午餐候选数: ${candidateSet.lunch.size}")
            android.util.Log.d(TAG, "    - 晚餐候选数: ${candidateSet.dinner.size}")
            android.util.Log.d(TAG, "    - 点心候选数: ${candidateSet.snack.size}")
            android.util.Log.d(TAG, "    - 总候选食谱数: ${candidateSet.allRecipes.size}")
            android.util.Log.d(TAG, "    - 被过滤食谱数: ${candidateSet.filteredCount}")

            if (candidateSet.allRecipes.isEmpty()) {
                android.util.Log.w(TAG, "  ❌ 候选食谱为空")
                val reason = analyzeEmptyCandidateReason(request)
                android.util.Log.e(TAG, "  失败原因: $reason")
                return RecommendationResponse(success = false, errorMessage = reason)
            }

            android.util.Log.d(TAG, "步骤2: 使用主力模型生成周计划")
            // 2. 使用主力模型生成一周饮食计划
            val weeklyPlan = mainModelStrategy.generateWeeklyPlan(
                candidateSet = candidateSet,
                ageInMonths = request.ageInMonths,
                constraints = request.constraints,
                startDate = request.startDate
            )

            android.util.Log.d(TAG, "  ✓ 周计划生成完成")
            android.util.Log.d(TAG, "    - 计划天数: ${weeklyPlan.dailyPlans.size}")

            android.util.Log.d(TAG, "步骤3: 使用便宜模型生成文案")
            val updatedWeeklyPlan = weeklyPlan.copy(
                dailyPlans = weeklyPlan.dailyPlans.map { dailyPlan ->
                    dailyPlan.copy(meals = cheapModelStrategy.generateBatchMealTexts(dailyPlan.meals))
                }
            )

            android.util.Log.d(TAG, "  ✓ 文案生成完成")

            android.util.Log.d(TAG, "步骤4: 规则校验（仅记录，不影响结果）")
            val allRecipes = updatedWeeklyPlan.dailyPlans.flatMap { it.meals.map { it.recipe } }
            android.util.Log.d(TAG, "  - 待校验食谱数: ${allRecipes.size}")

            val validationConstraints = ValidationConstraints(
                ageInMonths = request.ageInMonths,
                allergies = request.allergies,
                maxFishPerWeek = request.constraints.maxFishPerWeek,
                maxEggPerWeek = request.constraints.maxEggPerWeek,
                minDailyCalories = 500.0,
                maxDailyCalories = 1500.0
            )
            android.util.Log.d(TAG, "  - 校验约束: 年龄=${validationConstraints.ageInMonths}个月, 每周最多鱼类${validationConstraints.maxFishPerWeek}次, 每周最多蛋类${validationConstraints.maxEggPerWeek}次")

            val validationResult = ruleEngine.validateWeeklyPlan(
                recipes = allRecipes,
                constraints = validationConstraints
            )

            android.util.Log.d(TAG, "  ✓ 规则校验完成")
            android.util.Log.d(TAG, "    - 验证结果: ${if (validationResult.isValid) "通过" else "失败"}")
            android.util.Log.d(TAG, "    - 违规项: ${validationResult.violations.joinToString("; ")}")
            android.util.Log.d(TAG, "    - 警告项: ${validationResult.warnings.joinToString("; ")}")

            android.util.Log.d(TAG, "✓ 推荐成功返回（完全由 AI 生成）")
            RecommendationResponse(
                success = true,
                weeklyPlan = updatedWeeklyPlan,
                warnings = validationResult.warnings
            )

        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ 生成推荐发生异常: ${e.javaClass.simpleName}")
            android.util.Log.e(TAG, "  异常消息: ${e.message}")
            android.util.Log.e(TAG, "  异常堆栈: ", e)
            RecommendationResponse(success = false, errorMessage = "生成推荐失败：${e.message}")
        }
    }

    /**
     * 获取推荐约束建议
     * 根据宝宝年龄返回推荐的约束条件
     */
    suspend fun getRecommendedConstraints(babyId: Long): com.example.babyfood.domain.model.RecommendationConstraints {
        val baby = babyRepository.getBabyById(babyId)
        val ageInMonths = baby?.ageInMonths ?: 12

        return when {
            ageInMonths < 12 -> com.example.babyfood.domain.model.RecommendationConstraints(
                maxFishPerWeek = 1,
                maxEggPerWeek = 2,
                breakfastComplexity = com.example.babyfood.domain.model.ComplexityLevel.SIMPLE,
                maxDailyMeals = 3
            )
            ageInMonths < 24 -> com.example.babyfood.domain.model.RecommendationConstraints(
                maxFishPerWeek = 2,
                maxEggPerWeek = 3,
                breakfastComplexity = com.example.babyfood.domain.model.ComplexityLevel.MODERATE,
                maxDailyMeals = 4
            )
            else -> com.example.babyfood.domain.model.RecommendationConstraints(
                maxFishPerWeek = 2,
                maxEggPerWeek = 4,
                breakfastComplexity = com.example.babyfood.domain.model.ComplexityLevel.MODERATE,
                maxDailyMeals = 4
            )
        }
    }

    /**
     * 获取库存中的可用食材列表
     * 优先返回即将过期的食材
     * @return 可用食材名称列表
     */
    suspend fun getAvailableIngredientsFromInventory(): List<String> {
        return try {
            android.util.Log.d(TAG, "========== 获取库存食材 ==========")

            val expiringIngredients = inventoryRepository.getExpiringIngredients()
            android.util.Log.d(TAG, "即将过期的食材: ${expiringIngredients.joinToString(", ")}")

            val allIngredients = inventoryRepository.getAvailableIngredients()
            android.util.Log.d(TAG, "所有可用食材: ${allIngredients.joinToString(", ")}")

            val result = (expiringIngredients + allIngredients).distinct()
            android.util.Log.d(TAG, "✓ 获取到 ${result.size} 种可用食材 ==========")

            result
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ 获取库存食材失败: ${e.message}")
            emptyList()
        }
    }

    /**
     * 验证推荐请求
     */
    fun validateRequest(request: RecommendationRequest): Pair<Boolean, List<String>> {
        val errors = mutableListOf<String>()

        if (request.ageInMonths < 6) {
            errors.add("6个月以下的宝宝不建议添加辅食")
        }

        if (request.ageInMonths > 36) {
            errors.add("当前仅支持36个月以下的宝宝")
        }

        return Pair(errors.isEmpty(), errors)
    }

    /**
     * 记录请求数据
     */
    private fun logRequestData(request: RecommendationRequest) {
        android.util.Log.d(TAG, "========== AI 接口请求数据 ==========")
        android.util.Log.d(TAG, "babyId: ${request.babyId}")
        android.util.Log.d(TAG, "ageInMonths: ${request.ageInMonths}")
        android.util.Log.d(TAG, "allergies: ${request.allergies.joinToString(", ")}")
        android.util.Log.d(TAG, "preferences: ${request.preferences.joinToString(", ")}")
        android.util.Log.d(TAG, "availableIngredients: ${request.availableIngredients.joinToString(", ")}")
        android.util.Log.d(TAG, "useAvailableIngredientsOnly: ${request.useAvailableIngredientsOnly}")
        android.util.Log.d(TAG, "startDate: ${request.startDate}")
        android.util.Log.d(TAG, "constraints:")
        android.util.Log.d(TAG, "  - maxFishPerWeek: ${request.constraints.maxFishPerWeek}")
        android.util.Log.d(TAG, "  - maxEggPerWeek: ${request.constraints.maxEggPerWeek}")
        android.util.Log.d(TAG, "  - breakfastComplexity: ${request.constraints.breakfastComplexity}")
        android.util.Log.d(TAG, "  - maxDailyMeals: ${request.constraints.maxDailyMeals}")
        android.util.Log.d(TAG, "  - avoidIngredients: ${request.constraints.avoidIngredients.joinToString(", ")}")
        android.util.Log.d(TAG, "========== AI 接口请求数据结束 ==========")
    }

    /**
     * 分析候选食谱为空的原因
     */
    private fun analyzeEmptyCandidateReason(request: RecommendationRequest): String {
        val age = request.ageInMonths
        val hasAllergies = request.allergies.isNotEmpty()

        return when {
            age < 6 -> "宝宝年龄${age}个月，尚未达到添加辅食的年龄（建议6个月以上）"
            age > 36 -> "宝宝年龄${age}个月，超出了辅食推荐的年龄范围（支持36个月以下）"
            hasAllergies -> "宝宝设置了过敏食材（${request.allergies.joinToString("、")}），过滤后没有找到合适的食谱"
            else -> "当前年龄段（${age}个月）没有适合的辅食食谱"
        }
    }
}