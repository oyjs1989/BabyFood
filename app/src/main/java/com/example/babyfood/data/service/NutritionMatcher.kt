package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.NutritionIntake
import com.example.babyfood.domain.model.Recipe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 营养匹配器服务
 *
 * 负责分析营养摄入情况，并与目标进行匹配分析
 */
@Singleton
class NutritionMatcher @Inject constructor() {
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
     * 分析食谱与营养目标的匹配度
     */
    fun analyzeRecipeMatch(
        recipe: Recipe,
        goal: NutritionGoal,
        multiplier: Float = 1.0f
    ): NutritionMatchResult {
        Log.d(TAG, "========== 开始分析食谱匹配度 ==========")
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
     * 分析每日营养摄入与目标的匹配度
     */
    fun analyzeDailyMatch(
        recipes: List<Recipe>,
        goal: NutritionGoal
    ): NutritionMatchResult {
        Log.d(TAG, "========== 开始分析每日匹配度 ==========")
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
     * 分析匹配度
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