package com.example.babyfood.data.ai

import com.example.babyfood.data.ai.recommendation.IronRichStrategy
import com.example.babyfood.data.ai.ruleengine.RuleEngine
import com.example.babyfood.data.ai.ruleengine.ValidationConstraints
import com.example.babyfood.data.ai.ruleengine.ValidationResult
import com.example.babyfood.data.ai.strategy.CheapModelStrategy
import com.example.babyfood.data.ai.strategy.MainModelStrategy
import com.example.babyfood.data.local.database.dao.NutritionDataDao
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.InventoryRepository
import com.example.babyfood.domain.model.NutritionGoalInfo
import com.example.babyfood.domain.model.RecommendationConstraints
import com.example.babyfood.domain.model.RecommendationRequest
import com.example.babyfood.domain.model.RecommendationResponse
import com.example.babyfood.domain.model.WeeklyMealPlan
import com.example.babyfood.util.Loggable
import com.example.babyfood.util.Logger
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
    private val inventoryRepository: InventoryRepository,
    private val ironRichStrategy: IronRichStrategy,
    private val nutritionDataDao: NutritionDataDao
) : Loggable {

    override val logTag: String = "RecommendationService"

    /**
     * 生成辅食推荐
     * 奥卡姆剃刀原则：仅做必要的数据准备，将判断和分析完全交给AI
     */
    suspend fun generateRecommendation(request: RecommendationRequest): RecommendationResponse {
        return try {
            logMethodStart("生成辅食推荐")
            logRequestData(request)
            logD("奥卡姆剃刀原则：AI完全负责判断")

            // 步骤1: 筛选候选食谱
            val candidateSet = selectCandidates(request)
            if (candidateSet.allRecipes.isEmpty()) {
                return handleEmptyCandidates(request)
            }

            // 步骤2: 使用AI生成周计划
            val weeklyPlan = generateWeeklyPlanWithAI(candidateSet, request)

            // 步骤3: 生成友好文案
            val updatedWeeklyPlan = generateFriendlyTexts(weeklyPlan)

            // 步骤4: 规则校验
            val validationResult = validateWeeklyPlan(updatedWeeklyPlan, request)

            logSuccess("推荐生成成功（完全由 AI 生成）")
            logMethodEnd("生成辅食推荐")

            buildSuccessResponse(updatedWeeklyPlan, validationResult, request.ageInMonths)

        } catch (e: Exception) {
            logE("生成推荐发生异常: ${e.message}", e)
            RecommendationResponse(success = false, errorMessage = "生成推荐失败：${e.message}")
        }
    }

    /**
     * 步骤1: 筛选候选食谱
     */
    private suspend fun selectCandidates(request: RecommendationRequest): CandidateRecipeSet {
        logI("步骤1: 筛选候选食谱（仅过滤过敏食材）")

        val candidateSet = candidateRecipeService.getCandidateRecipes(
            ageInMonths = request.ageInMonths,
            allergies = request.allergies,
            preferences = request.preferences,
            availableIngredients = request.availableIngredients,
            avoidIngredients = request.constraints.avoidIngredients,
            useAvailableIngredientsOnly = request.useAvailableIngredientsOnly
        )

        logD("候选食谱筛选完成:")
        logD("  - 总候选食谱数: ${candidateSet.allRecipes.size}")
        logD("  - 被过滤食谱数: ${candidateSet.filteredCount}")
        logD("  - 早餐候选数: ${candidateSet.breakfast.size}")
        logD("  - 午餐候选数: ${candidateSet.lunch.size}")
        logD("  - 晚餐候选数: ${candidateSet.dinner.size}")
        logD("  - 点心候选数: ${candidateSet.snack.size}")

        return candidateSet
    }

    /**
     * 处理候选食谱为空的情况
     */
    private fun handleEmptyCandidates(request: RecommendationRequest): RecommendationResponse {
        logW("候选食谱为空")
        val reason = analyzeEmptyCandidateReason(request)
        logE("失败原因: $reason")
        return RecommendationResponse(success = false, errorMessage = reason)
    }

    /**
     * 步骤2: 使用AI生成周计划
     */
    private suspend fun generateWeeklyPlanWithAI(
        candidateSet: CandidateRecipeSet,
        request: RecommendationRequest
    ): WeeklyMealPlan {
        logI("步骤2: 使用AI生成周计划（包含分析思路和营养目标）")

        val weeklyPlan = mainModelStrategy.generateWeeklyPlan(
            candidateSet = candidateSet,
            ageInMonths = request.ageInMonths,
            constraints = request.constraints,
            startDate = request.startDate
        )

        logD("AI周计划生成完成，计划天数: ${weeklyPlan.dailyPlans.size}")
        return weeklyPlan
    }

    /**
     * 步骤3: 生成友好文案
     */
    private suspend fun generateFriendlyTexts(weeklyPlan: WeeklyMealPlan): WeeklyMealPlan {
        logI("步骤3: 使用AI生成友好文案")

        return weeklyPlan.copy(
            dailyPlans = weeklyPlan.dailyPlans.map { dailyPlan ->
                dailyPlan.copy(meals = cheapModelStrategy.generateBatchMealTexts(dailyPlan.meals))
            }
        )
    }

    /**
     * 步骤4: 规则校验
     */
    private fun validateWeeklyPlan(
        weeklyPlan: WeeklyMealPlan,
        request: RecommendationRequest
    ): ValidationResult {
        logI("步骤4: 规则校验（仅记录日志，不影响AI结果）")

        val allRecipes = weeklyPlan.dailyPlans.flatMap { it.meals.map { meal -> meal.recipe } }
        logD("待校验食谱数: ${allRecipes.size}")

        val validationConstraints = ValidationConstraints(
            ageInMonths = request.ageInMonths,
            allergies = request.allergies,
            maxFishPerWeek = request.constraints.maxFishPerWeek,
            maxEggPerWeek = request.constraints.maxEggPerWeek,
            minDailyCalories = 500.0,
            maxDailyCalories = 1500.0
        )

        val result = ruleEngine.validateWeeklyPlan(
            recipes = allRecipes,
            constraints = validationConstraints
        )

        logD("规则校验完成:")
        logD("  - 验证结果: ${if (result.isValid) "通过" else "失败"}")
        logD("  - 违规项: ${result.violations.joinToString("; ")}")
        logD("  - 警告项: ${result.warnings.joinToString("; ")}")

        return result
    }

    /**
     * 构建成功响应
     */
    private fun buildSuccessResponse(
        weeklyPlan: WeeklyMealPlan,
        validationResult: ValidationResult,
        ageInMonths: Int
    ): RecommendationResponse {
        return RecommendationResponse(
            success = true,
            weeklyPlan = weeklyPlan,
            warnings = validationResult.warnings,
            analysisReasoning = "AI根据宝宝年龄、过敏史、偏好食材、可用食材等综合分析，生成符合营养目标的周计划",
            nutritionGoals = getNutritionGoalsForAge(ageInMonths)
        )
    }

    /**
     * 获取指定年龄的营养目标（中国营养学会标准）
     */
    private fun getNutritionGoalsForAge(ageInMonths: Int): NutritionGoalInfo {
        return when (ageInMonths) {
            in 6..8 -> NutritionGoalInfo(
                ageRange = "6-8个月",
                calories = 500f,
                caloriesUnit = "kcal",
                protein = 20f,
                proteinUnit = "g",
                calcium = 260f,
                calciumUnit = "mg",
                iron = 8.8f,
                ironUnit = "mg",
                description = "6-8个月宝宝开始添加辅食，以富含铁的食物为主，如强化铁米粉、肉泥、肝泥等"
            )
            in 9..11 -> NutritionGoalInfo(
                ageRange = "9-11个月",
                calories = 600f,
                caloriesUnit = "kcal",
                protein = 25f,
                proteinUnit = "g",
                calcium = 350f,
                calciumUnit = "mg",
                iron = 9.0f,
                ironUnit = "mg",
                description = "9-11个月宝宝可尝试更多种类的食物，逐步增加食物的质地和多样性"
            )
            in 12..17 -> NutritionGoalInfo(
                ageRange = "12-17个月",
                calories = 700f,
                caloriesUnit = "kcal",
                protein = 30f,
                proteinUnit = "g",
                calcium = 500f,
                calciumUnit = "mg",
                iron = 9.0f,
                ironUnit = "mg",
                description = "12-17个月宝宝可逐渐过渡到家庭膳食，注意营养均衡和多样化"
            )
            in 18..23 -> NutritionGoalInfo(
                ageRange = "18-23个月",
                calories = 800f,
                caloriesUnit = "kcal",
                protein = 35f,
                proteinUnit = "g",
                calcium = 600f,
                calciumUnit = "mg",
                iron = 9.0f,
                ironUnit = "mg",
                description = "18-23个月宝宝可食用大部分家庭食物，注意控制盐糖摄入"
            )
            else -> NutritionGoalInfo(
                ageRange = "24个月及以上",
                calories = 1000f,
                caloriesUnit = "kcal",
                protein = 40f,
                proteinUnit = "g",
                calcium = 800f,
                calciumUnit = "mg",
                iron = 12.0f,
                ironUnit = "mg",
                description = "24个月及以上宝宝可完全适应家庭膳食，保持营养均衡"
            )
        }
    }

    /**
     * 获取推荐约束建议
     */
    suspend fun getRecommendedConstraints(babyId: Long): RecommendationConstraints {
        val baby = babyRepository.getById(babyId)
        val ageInMonths = baby?.ageInMonths ?: 12

        return when {
            ageInMonths < 12 -> RecommendationConstraints(
                maxFishPerWeek = 1,
                maxEggPerWeek = 2,
                breakfastComplexity = com.example.babyfood.domain.model.ComplexityLevel.SIMPLE,
                maxDailyMeals = 3
            )
            ageInMonths < 24 -> RecommendationConstraints(
                maxFishPerWeek = 2,
                maxEggPerWeek = 3,
                breakfastComplexity = com.example.babyfood.domain.model.ComplexityLevel.MODERATE,
                maxDailyMeals = 4
            )
            else -> RecommendationConstraints(
                maxFishPerWeek = 2,
                maxEggPerWeek = 4,
                breakfastComplexity = com.example.babyfood.domain.model.ComplexityLevel.MODERATE,
                maxDailyMeals = 4
            )
        }
    }

    /**
     * 获取库存中的可用食材列表
     */
    suspend fun getAvailableIngredientsFromInventory(): List<String> {
        return try {
            logMethodStart("获取库存食材")

            val expiringIngredients = inventoryRepository.getExpiringIngredients()
            logD("即将过期的食材: ${expiringIngredients.joinToString(", ")}")

            val allIngredients = inventoryRepository.getAvailableIngredients()
            logD("所有可用食材: ${allIngredients.joinToString(", ")}")

            val result = (expiringIngredients + allIngredients).distinct()
            logSuccess("获取到 ${result.size} 种可用食材")
            logMethodEnd("获取库存食材")

            result
        } catch (e: Exception) {
            logE("获取库存食材失败: ${e.message}", e)
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
        logD("AI 接口请求数据:")
        logD("  babyId: ${request.babyId}")
        logD("  ageInMonths: ${request.ageInMonths}")
        logD("  allergies: ${request.allergies.joinToString(", ")}")
        logD("  preferences: ${request.preferences.joinToString(", ")}")
        logD("  availableIngredients: ${request.availableIngredients.joinToString(", ")}")
        logD("  useAvailableIngredientsOnly: ${request.useAvailableIngredientsOnly}")
        logD("  startDate: ${request.startDate}")
        logD("  constraints: maxFishPerWeek=${request.constraints.maxFishPerWeek}, maxEggPerWeek=${request.constraints.maxEggPerWeek}")
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
