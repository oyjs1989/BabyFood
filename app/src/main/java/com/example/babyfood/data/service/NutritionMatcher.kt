package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.data.remote.api.NutritionApiService
import com.example.babyfood.data.remote.dto.nutrition.NutritionGoalsUpdate
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.NutritionIntake
import com.example.babyfood.domain.model.Recipe
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 营养匹配器服务
 *
 * 负责分析营养摄入情况，并与目标进行匹配分析
 * 
 * 【重构说明】现在优先从后端 API 获取营养目标和计算摄入，本地计算作为离线备份
 */
@Singleton
class NutritionMatcher @Inject constructor(
    private val nutritionApiService: NutritionApiService
) {
    companion object {
        private const val TAG = "NutritionMatcher"
    }

    /**
     * 营养匹配结果
     */
    data class NutritionMatchResult(
        val goal: NutritionGoal,
        val intake: NutritionIntake,
        val overallScore: Float,           // 总体匹配度 (0-100)
        val caloriesProgress: Float,       // 热量达成率
        val proteinProgress: Float,        // 蛋白质达成率
        val calciumProgress: Float,        // 钙达成率
        val ironProgress: Float,           // 铁达成率
        val isBalanced: Boolean,           // 是否营养均衡
        val deficiencies: List<String>,    // 营养缺乏项
        val excesses: List<String>,        // 营养过量项
        val recommendations: List<String>   // 优化建议
    )

    /**
     * 从后端 API 获取宝宝的营养目标
     * 
     * 【新增】优先使用后端 API，失败时返回 null
     */
    suspend fun getNutritionGoals(babyId: Int): NutritionGoal? {
        Log.d(TAG, "========== 获取营养目标 ==========")
        Log.d(TAG, "宝宝ID: $babyId")
        
        return try {
            val response = nutritionApiService.getNutritionGoals(babyId)
            Log.d(TAG, "✓ 从后端 API 获取营养目标成功")
            
            NutritionGoal(
                calories = response.calories.toFloat(),
                protein = response.protein.toFloat(),
                calcium = response.calcium.toFloat(),
                iron = response.iron.toFloat()
            )
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 从后端 API 获取营养目标失败: ${e.message}")
            Log.w(TAG, "请确保营养目标已在本地设置")
            null
        }
    }

    /**
     * 更新宝宝的营养目标
     * 
     * 【新增】调用后端 API 更新营养目标
     */
    suspend fun updateNutritionGoals(
        babyId: Int, 
        goals: NutritionGoal
    ): Result<NutritionGoal> {
        Log.d(TAG, "========== 更新营养目标 ==========")
        Log.d(TAG, "宝宝ID: $babyId")
        
        return try {
            val updateDto = NutritionGoalsUpdate(
                calories = goals.calories.toDouble(),
                protein = goals.protein.toDouble(),
                calcium = goals.calcium.toDouble(),
                iron = goals.iron.toDouble()
            )
            
            val response = nutritionApiService.updateNutritionGoals(babyId, updateDto)
            Log.d(TAG, "✓ 更新营养目标成功")
            
            val updatedGoal = NutritionGoal(
                calories = response.calories.toFloat(),
                protein = response.protein.toFloat(),
                calcium = response.calcium.toFloat(),
                iron = response.iron.toFloat()
            )
            
            Result.success(updatedGoal)
        } catch (e: Exception) {
            Log.e(TAG, "❌ 更新营养目标失败: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 计算营养摄入（调用后端 API）
     * 
     * 【新增】优先使用后端 API 计算，失败时使用本地计算
     */
    suspend fun calculateNutritionIntake(
        babyId: Int,
        intake: NutritionIntake
    ): NutritionMatchResult? {
        Log.d(TAG, "========== 计算营养摄入 ==========")
        Log.d(TAG, "宝宝ID: $babyId")
        
        return try {
            // 调用后端 API 计算摄入
            val intakeMap = mapOf(
                "calories" to intake.calories.toDouble(),
                "protein" to intake.protein.toDouble(),
                "calcium" to intake.calcium.toDouble(),
                "iron" to intake.iron.toDouble()
            )
            
            val response = nutritionApiService.calculateNutritionIntake(babyId, intakeMap)
            Log.d(TAG, "✓ 从后端 API 计算营养摄入成功")
            
            // 获取营养目标
            val goal = response.goals?.let {
                NutritionGoal(
                    calories = it.calories.toFloat(),
                    protein = it.protein.toFloat(),
                    calcium = it.calcium.toFloat(),
                    iron = it.iron.toFloat()
                )
            } ?: return null
            
            // 构建匹配结果
            val actualIntake = response.actualIntake
            val caloriesProgress = calculateProgress(actualIntake.calories.toFloat(), goal.calories)
            val proteinProgress = calculateProgress(actualIntake.protein.toFloat(), goal.protein)
            val calciumProgress = calculateProgress(actualIntake.calcium.toFloat(), goal.calcium)
            val ironProgress = calculateProgress(actualIntake.iron.toFloat(), goal.iron)
            
            val overallScore = (caloriesProgress + proteinProgress + calciumProgress + ironProgress) / 4f
            
            // 分析缺乏和过量
            val deficiencies = mutableListOf<String>()
            val excesses = mutableListOf<String>()
            val recommendations = mutableListOf<String>()
            
            // 检查各项营养
            if (caloriesProgress < 80f) {
                deficiencies.add("热量")
                recommendations.add("增加主食摄入")
            } else if (caloriesProgress > 120f) {
                excesses.add("热量")
                recommendations.add("减少主食摄入")
            }
            
            if (proteinProgress < 80f) {
                deficiencies.add("蛋白质")
                recommendations.add("增加肉类、蛋类或豆制品")
            } else if (proteinProgress > 150f) {
                excesses.add("蛋白质")
                recommendations.add("适当减少肉类摄入")
            }
            
            if (calciumProgress < 80f) {
                deficiencies.add("钙")
                recommendations.add("增加奶制品或豆制品")
            }
            
            if (ironProgress < 80f) {
                deficiencies.add("铁")
                recommendations.add("增加红肉或动物肝脏")
            }
            
            // 添加后端返回的警告
            response.warnings?.let { warnings ->
                recommendations.addAll(warnings)
            }
            
            NutritionMatchResult(
                goal = goal,
                intake = NutritionIntake(
                    calories = actualIntake.calories.toFloat(),
                    protein = actualIntake.protein.toFloat(),
                    calcium = actualIntake.calcium.toFloat(),
                    iron = actualIntake.iron.toFloat()
                ),
                overallScore = overallScore,
                caloriesProgress = caloriesProgress,
                proteinProgress = proteinProgress,
                calciumProgress = calciumProgress,
                ironProgress = ironProgress,
                isBalanced = deficiencies.isEmpty() && excesses.isEmpty(),
                deficiencies = deficiencies,
                excesses = excesses,
                recommendations = recommendations
            )
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 从后端 API 计算营养摄入失败: ${e.message}")
            Log.w(TAG, "请检查网络连接或稍后重试")
            null
        }
    }

    /**
     * 分析食谱与营养目标的匹配度（本地计算）
     * 
     * 【保留】用于离线场景
     */
    fun analyzeRecipeMatch(
        recipe: Recipe,
        goal: NutritionGoal,
        multiplier: Float = 1.0f
    ): NutritionMatchResult {
        Log.d(TAG, "========== 开始分析食谱匹配度（本地计算） ==========")
        Log.d(TAG, "食谱: ${recipe.name}")
        Log.d(TAG, "倍数: $multiplier")

        val intake = calculateRecipeIntake(recipe, multiplier)
        val result = analyzeMatch(goal, intake)

        Log.d(TAG, "总体匹配度: ${result.overallScore}%")
        Log.d(TAG, "营养均衡: ${result.isBalanced}")
        Log.d(TAG, "========== 分析完成 ==========")

        return result
    }

    /**
     * 分析每日营养摄入与目标的匹配度（本地计算）
     * 
     * 【保留】用于离线场景
     */
    fun analyzeDailyMatch(
        recipes: List<Recipe>,
        goal: NutritionGoal
    ): NutritionMatchResult {
        Log.d(TAG, "========== 开始分析每日匹配度（本地计算） ==========")
        Log.d(TAG, "食谱数量: ${recipes.size}")

        val totalIntake = calculateTotalIntake(recipes)
        val result = analyzeMatch(goal, totalIntake)

        Log.d(TAG, "总体匹配度: ${result.overallScore}%")
        Log.d(TAG, "营养均衡: ${result.isBalanced}")
        Log.d(TAG, "========== 分析完成 ==========")

        return result
    }

    /**
     * 计算食谱的营养摄入
     */
    private fun calculateRecipeIntake(recipe: Recipe, multiplier: Float): NutritionIntake {
        return NutritionIntake(
            calories = (recipe.nutrition?.calories ?: 0f) * multiplier,
            protein = (recipe.nutrition?.protein ?: 0f) * multiplier,
            calcium = (recipe.nutrition?.calcium ?: 0f) * multiplier,
            iron = (recipe.nutrition?.iron ?: 0f) * multiplier
        )
    }

    /**
     * 计算总营养摄入
     */
    private fun calculateTotalIntake(recipes: List<Recipe>): NutritionIntake {
        return recipes.fold(NutritionIntake(0f, 0f, 0f, 0f)) { acc, recipe ->
            acc.copy(
                calories = acc.calories + (recipe.nutrition?.calories ?: 0f),
                protein = acc.protein + (recipe.nutrition?.protein ?: 0f),
                calcium = acc.calcium + (recipe.nutrition?.calcium ?: 0f),
                iron = acc.iron + (recipe.nutrition?.iron ?: 0f)
            )
        }
    }

    /**
     * 分析匹配度（本地计算）
     */
    private fun analyzeMatch(goal: NutritionGoal, intake: NutritionIntake): NutritionMatchResult {
        val caloriesProgress = calculateProgress(intake.calories, goal.calories)
        val proteinProgress = calculateProgress(intake.protein, goal.protein)
        val calciumProgress = calculateProgress(intake.calcium, goal.calcium)
        val ironProgress = calculateProgress(intake.iron, goal.iron)

        val overallScore = (caloriesProgress + proteinProgress + calciumProgress + ironProgress) / 4f

        val deficiencies = mutableListOf<String>()
        val excesses = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        // 营养检查规则：定义名称、阈值和对应建议
        val nutritionRules = listOf(
            NutritionRule("热量", caloriesProgress, deficiencyThreshold = 80f, excessThreshold = 120f, deficiencyRecommendation = "增加主食摄入", excessRecommendation = "减少主食摄入"),
            NutritionRule("蛋白质", proteinProgress, deficiencyThreshold = 80f, excessThreshold = 150f, deficiencyRecommendation = "增加肉类、蛋类或豆制品", excessRecommendation = "适当减少肉类摄入"),
            NutritionRule("钙", calciumProgress, deficiencyThreshold = 80f, excessThreshold = Float.MAX_VALUE, deficiencyRecommendation = "增加奶制品或豆制品", excessRecommendation = null),
            NutritionRule("铁", ironProgress, deficiencyThreshold = 80f, excessThreshold = Float.MAX_VALUE, deficiencyRecommendation = "增加红肉或动物肝脏", excessRecommendation = null)
        )

        // 应用营养检查规则
        nutritionRules.forEach { rule ->
            if (rule.progress < rule.deficiencyThreshold) {
                deficiencies.add(rule.name)
                rule.deficiencyRecommendation?.let { recommendations.add(it) }
            } else if (rule.progress > rule.excessThreshold && rule.excessRecommendation != null) {
                excesses.add(rule.name)
                recommendations.add(rule.excessRecommendation)
            }
        }

        // 判断是否均衡
        val isBalanced = deficiencies.isEmpty() && excesses.isEmpty()

        return NutritionMatchResult(
            goal = goal,
            intake = intake,
            overallScore = overallScore,
            caloriesProgress = caloriesProgress,
            proteinProgress = proteinProgress,
            calciumProgress = calciumProgress,
            ironProgress = ironProgress,
            isBalanced = isBalanced,
            deficiencies = deficiencies,
            excesses = excesses,
            recommendations = recommendations
        )
    }

    /**
     * 营养检查规则
     */
    private data class NutritionRule(
        val name: String,
        val progress: Float,
        val deficiencyThreshold: Float,
        val excessThreshold: Float,
        val deficiencyRecommendation: String?,
        val excessRecommendation: String?
    )

    /**
     * 计算达成率
     */
    private fun calculateProgress(intake: Float, goal: Float): Float {
        if (goal == 0f) return 0f
        return (intake / goal) * 100f
    }

    /**
     * 获取营养亮点
     */
    fun getHighlights(result: NutritionMatchResult): List<String> {
        val highlights = mutableListOf<String>()

        if (result.proteinProgress >= 100f) {
            highlights.add("蛋白质充足")
        }
        if (result.calciumProgress >= 100f) {
            highlights.add("钙充足")
        }
        if (result.ironProgress >= 100f) {
            highlights.add("铁充足")
        }
        if (result.isBalanced) {
            highlights.add("营养均衡")
        }

        return highlights
    }

    /**
     * 获取营养等级
     */
    fun getNutritionGrade(result: NutritionMatchResult): NutritionGrade {
        return when {
            result.overallScore >= 90f -> NutritionGrade.EXCELLENT
            result.overallScore >= 75f -> NutritionGrade.GOOD
            result.overallScore >= 60f -> NutritionGrade.FAIR
            result.overallScore >= 40f -> NutritionGrade.POOR
            else -> NutritionGrade.VERY_POOR
        }
    }

    /**
     * 营养等级
     */
    enum class NutritionGrade(val displayName: String, val color: String) {
        EXCELLENT("优秀", "#4CAF50"),
        GOOD("良好", "#8BC34A"),
        FAIR("一般", "#FFC107"),
        POOR("较差", "#FF9800"),
        VERY_POOR("很差", "#F44336")
    }
}
