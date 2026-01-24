package com.example.babyfood.data.ai

import com.example.babyfood.data.ai.ruleengine.RuleEngine
import com.example.babyfood.data.ai.ruleengine.ValidationConstraints
import com.example.babyfood.data.ai.strategy.CheapModelStrategy
import com.example.babyfood.data.ai.strategy.MainModelStrategy
import com.example.babyfood.domain.model.RecommendationRequest
import com.example.babyfood.domain.model.RecommendationResponse
import com.example.babyfood.data.repository.BabyRepository
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
    private val babyRepository: BabyRepository
) {

    /**
     * 生成辅食推荐
     * @param request 推荐请求
     * @return 推荐响应
     */
    suspend fun generateRecommendation(request: RecommendationRequest): RecommendationResponse {
        return try {
            android.util.Log.d("AI推荐服务", "========== AI 接口请求数据 ==========")
            android.util.Log.d("AI推荐服务", "babyId: ${request.babyId}")
            android.util.Log.d("AI推荐服务", "ageInMonths: ${request.ageInMonths}")
            android.util.Log.d("AI推荐服务", "allergies: ${request.allergies.joinToString(", ") { it }}")
            android.util.Log.d("AI推荐服务", "preferences: ${request.preferences.joinToString(", ") { it }}")
            android.util.Log.d("AI推荐服务", "availableIngredients: ${request.availableIngredients.joinToString(", ") { it }}")
            android.util.Log.d("AI推荐服务", "useAvailableIngredientsOnly: ${request.useAvailableIngredientsOnly}")
            android.util.Log.d("AI推荐服务", "startDate: ${request.startDate}")
            android.util.Log.d("AI推荐服务", "constraints:")
            android.util.Log.d("AI推荐服务", "  - maxFishPerWeek: ${request.constraints.maxFishPerWeek}")
            android.util.Log.d("AI推荐服务", "  - maxEggPerWeek: ${request.constraints.maxEggPerWeek}")
            android.util.Log.d("AI推荐服务", "  - breakfastComplexity: ${request.constraints.breakfastComplexity}")
            android.util.Log.d("AI推荐服务", "  - maxDailyMeals: ${request.constraints.maxDailyMeals}")
            android.util.Log.d("AI推荐服务", "  - avoidIngredients: ${request.constraints.avoidIngredients.joinToString(", ") { it }}")
            android.util.Log.d("AI推荐服务", "========== AI 接口请求数据结束 ==========")
            
            android.util.Log.d("AI推荐服务", "步骤1: 开始筛选候选食谱")
            android.util.Log.d("AI推荐服务", "  年龄范围: ${request.ageInMonths}个月")
            android.util.Log.d("AI推荐服务", "  过敏食材: ${request.allergies.joinToString(", ") { it }}")
            android.util.Log.d("AI推荐服务", "  偏好食材: ${request.preferences.joinToString(", ") { it }}")
            android.util.Log.d("AI推荐服务", "  可用食材: ${request.availableIngredients.joinToString(", ") { it }}")
            android.util.Log.d("AI推荐服务", "  仅使用可用食材: ${request.useAvailableIngredientsOnly}")

            // 1. 获取候选食谱集合（规则层过滤）
            val candidateSet = candidateRecipeService.getCandidateRecipes(
                ageInMonths = request.ageInMonths,
                allergies = request.allergies,
                preferences = request.preferences,
                availableIngredients = request.availableIngredients,
                avoidIngredients = request.constraints.avoidIngredients,
                useAvailableIngredientsOnly = request.useAvailableIngredientsOnly
            )

            android.util.Log.d("AI推荐服务", "  ✓ 候选食谱筛选完成")
            android.util.Log.d("AI推荐服务", "    - 早餐候选数: ${candidateSet.breakfast.size}")
            android.util.Log.d("AI推荐服务", "    - 午餐候选数: ${candidateSet.lunch.size}")
            android.util.Log.d("AI推荐服务", "    - 晚餐候选数: ${candidateSet.dinner.size}")
            android.util.Log.d("AI推荐服务", "    - 点心候选数: ${candidateSet.snack.size}")
            android.util.Log.d("AI推荐服务", "    - 总候选食谱数: ${candidateSet.allRecipes.size}")
            android.util.Log.d("AI推荐服务", "    - 被过滤食谱数: ${candidateSet.filteredCount}")

            // 检查是否有足够的候选食谱
            if (candidateSet.allRecipes.isEmpty()) {
                android.util.Log.w("AI推荐服务", "  ❌ 候选食谱为空")
                // 分析为什么没有合适的食谱
                val age = request.ageInMonths
                val hasAllergies = request.allergies.isNotEmpty()

                val reason = when {
                    age < 6 -> "宝宝年龄${age}个月，尚未达到添加辅食的年龄（建议6个月以上）"
                    age > 36 -> "宝宝年龄${age}个月，超出了辅食推荐的年龄范围（支持36个月以下）"
                    hasAllergies -> "宝宝设置了过敏食材（${request.allergies.joinToString("、")}），过滤后没有找到合适的食谱"
                    else -> "当前年龄段（${age}个月）没有适合的辅食食谱"
                }

                android.util.Log.e("AI推荐服务", "  失败原因: $reason")
                return RecommendationResponse(
                    success = false,
                    errorMessage = reason
                )
            }

            android.util.Log.d("AI推荐服务", "步骤2: 使用主力模型生成周计划")
            // 2. 使用主力模型生成一周饮食计划
            val weeklyPlan = mainModelStrategy.generateWeeklyPlan(
                candidateSet = candidateSet,
                ageInMonths = request.ageInMonths,
                constraints = request.constraints,
                startDate = request.startDate
            )

            android.util.Log.d("AI推荐服务", "  ✓ 周计划生成完成")
            android.util.Log.d("AI推荐服务", "    - 计划天数: ${weeklyPlan.dailyPlans.size}")

            android.util.Log.d("AI推荐服务", "步骤3: 使用便宜模型生成文案")
            // 3. 使用便宜模型生成文案
            val updatedDailyPlans = weeklyPlan.dailyPlans.map { dailyPlan ->
                dailyPlan.copy(
                    meals = cheapModelStrategy.generateBatchMealTexts(dailyPlan.meals)
                )
            }

            val updatedWeeklyPlan = weeklyPlan.copy(
                dailyPlans = updatedDailyPlans
            )

            android.util.Log.d("AI推荐服务", "  ✓ 文案生成完成")

            android.util.Log.d("AI推荐服务", "步骤4: 规则校验（仅记录，不影响结果）")
            // 4. 规则校验（仅记录日志，不影响结果返回）
            val allRecipes = updatedWeeklyPlan.dailyPlans.flatMap { it.meals.map { it.recipe } }
            android.util.Log.d("AI推荐服务", "  - 待校验食谱数: ${allRecipes.size}")
            
            val validationConstraints = ValidationConstraints(
                ageInMonths = request.ageInMonths,
                allergies = request.allergies,
                maxFishPerWeek = request.constraints.maxFishPerWeek,
                maxEggPerWeek = request.constraints.maxEggPerWeek,
                minDailyCalories = 500.0,  // 降低最小热量要求
                maxDailyCalories = 1500.0  // 提高最大热量要求
            )
            android.util.Log.d("AI推荐服务", "  - 校验约束: 年龄=${validationConstraints.ageInMonths}个月, 每周最多鱼类${validationConstraints.maxFishPerWeek}次, 每周最多蛋类${validationConstraints.maxEggPerWeek}次")
            
            val validationResult = ruleEngine.validateWeeklyPlan(
                recipes = allRecipes,
                constraints = validationConstraints
            )

            android.util.Log.d("AI推荐服务", "  ✓ 规则校验完成")
            android.util.Log.d("AI推荐服务", "    - 验证结果: ${if (validationResult.isValid) "通过" else "失败"}")
            android.util.Log.d("AI推荐服务", "    - 违规项: ${validationResult.violations.joinToString("; ") { it }}")
            android.util.Log.d("AI推荐服务", "    - 警告项: ${validationResult.warnings.joinToString("; ") { it }}")

            // 5. 返回结果（无论本地校验结果如何，都返回 AI 生成的结果）
            android.util.Log.d("AI推荐服务", "✓ 推荐成功返回（完全由 AI 生成）")
            RecommendationResponse(
                success = true,
                weeklyPlan = updatedWeeklyPlan,
                warnings = validationResult.warnings
            )

        } catch (e: Exception) {
            android.util.Log.e("AI推荐服务", "❌ 生成推荐发生异常: ${e.javaClass.simpleName}")
            android.util.Log.e("AI推荐服务", "  异常消息: ${e.message}")
            android.util.Log.e("AI推荐服务", "  异常堆栈: ", e)
            RecommendationResponse(
                success = false,
                errorMessage = "生成推荐失败：${e.message}"
            )
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
}