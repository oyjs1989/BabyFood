package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.data.repository.IngredientTrialRepository
import com.example.babyfood.domain.model.Recipe
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 口味多样性推荐服务
 *
 * 实现"原味多样化"原则，引导用户遵循"原味优先"原则，
 * 记录宝宝尝试过的食材，优先推荐新食材预防挑食
 */
@Singleton
class FlavorDiversityService @Inject constructor(
    private val ingredientTrialRepository: IngredientTrialRepository
) {
    companion object {
        private const val TAG = "FlavorDiversityService"
        private const val NEW_INGREDIENT_BONUS = 10.0f  // 新食材加分
        private const val HIGH_FREQUENCY_PENALTY = 5.0f  // 高频食材扣分
        private const val DIVERSITY_BONUS = 5.0f  // 食材种类多样性加分
    }

    /**
     * 分析食谱的口味多样性
     *
     * @param recipe 食谱
     * @param babyId 宝宝ID
     * @return 多样性分析结果
     */
    suspend fun analyzeDiversity(recipe: Recipe, babyId: Long): DiversityAnalysis {
        android.util.Log.d(TAG, "========== 分析口味多样性 ==========")
        android.util.Log.d(TAG, "食谱ID: ${recipe.id}")
        android.util.Log.d(TAG, "食谱名称: ${recipe.name}")
        android.util.Log.d(TAG, "宝宝ID: $babyId")

        // 获取宝宝已尝试的食材列表
        val triedIngredients = ingredientTrialRepository.getTriedIngredients(babyId)
        android.util.Log.d(TAG, "已尝试食材数量: ${triedIngredients.size}")

        // 分析食谱中的食材
        val recipeIngredients = recipe.ingredients.map { it.name }
        android.util.Log.d(TAG, "食谱食材数量: ${recipeIngredients.size}")

        // 计算新食材
        val newIngredients = recipeIngredients.filter { it !in triedIngredients }
        android.util.Log.d(TAG, "新食材数量: ${newIngredients.size}")
        android.util.Log.d(TAG, "新食材: ${newIngredients.joinToString(", ")}")

        // 计算多样性分数（0-100）
        val diversityScore = calculateDiversityScore(recipeIngredients, triedIngredients, newIngredients)

        // 判断是否原味（无调味品）
        val isNaturalFlavor = isNaturalFlavorRecipe(recipe)
        android.util.Log.d(TAG, "原味食谱: $isNaturalFlavor")

        // 生成分析结果
        val analysis = DiversityAnalysis(
            recipeId = recipe.id,
            isNaturalFlavor = isNaturalFlavor,
            diversityScore = diversityScore,
            totalIngredients = recipeIngredients.size,
            newIngredients = newIngredients,
            newIngredientCount = newIngredients.size,
            triedIngredientCount = recipeIngredients.size - newIngredients.size,
            isHighDiversity = diversityScore >= 70,
            varietyDescription = getVarietyDescription(diversityScore)
        )

        android.util.Log.d(TAG, "多样性分数: ${analysis.diversityScore}")
        android.util.Log.d(TAG, "多样性描述: ${analysis.varietyDescription}")
        android.util.Log.d(TAG, "✓ 分析完成")
        android.util.Log.d(TAG, "========== 分析完成 ==========")

        return analysis
    }

    /**
     * 推荐多样化食谱
     *
     * @param recipes 候选食谱列表
     * @param babyId 宝宝ID
     * @return 排序后的食谱列表（优先推荐高多样性）
     */
    suspend fun recommendDiverseRecipes(recipes: List<Recipe>, babyId: Long): List<RecipeWithDiversityScore> {
        android.util.Log.d(TAG, "========== 推荐多样化食谱 ==========")
        android.util.Log.d(TAG, "候选食谱数量: ${recipes.size}")
        android.util.Log.d(TAG, "宝宝ID: $babyId")

        // 获取宝宝已尝试的食材列表
        val triedIngredients = ingredientTrialRepository.getTriedIngredients(babyId)
        android.util.Log.d(TAG, "已尝试食材: $triedIngredients")

        // 分析每个食谱
        val scoredRecipes = recipes.map { recipe ->
            val recipeIngredients = recipe.ingredients.map { it.name }
            val newIngredients = recipeIngredients.filter { it !in triedIngredients }
            
            // 计算多样性分数
            val diversityScore = calculateDiversityScore(recipeIngredients, triedIngredients, newIngredients)
            val isNaturalFlavor = isNaturalFlavorRecipe(recipe)

            RecipeWithDiversityScore(
                recipe = recipe,
                diversityScore = diversityScore,
                isNaturalFlavor = isNaturalFlavor,
                newIngredients = newIngredients,
                newIngredientCount = newIngredients.size
            )
        }

        // 按多样性分数排序（降序）
        val sortedRecipes = scoredRecipes.sortedByDescending { it.diversityScore }
        
        android.util.Log.d(TAG, "✓ 推荐完成")
        android.util.Log.d(TAG, "========== 推荐完成 ==========")

        return sortedRecipes
    }

    /**
     * 计算多样性分数
     */
    private fun calculateDiversityScore(
        recipeIngredients: List<String>,
        triedIngredients: List<String>,
        newIngredients: List<String>
    ): Float {
        var score = 50.0f  // 基础分

        // 新食材加分
        score += newIngredients.size * NEW_INGREDIENT_BONUS

        // 高频食材扣分（出现3次及以上）
        val highFrequencyCount = recipeIngredients.count { ingredient ->
            triedIngredients.count { it == ingredient } >= 3
        }
        score -= highFrequencyCount * HIGH_FREQUENCY_PENALTY

        // 食材种类多样性加分（5种及以上）
        if (recipeIngredients.size >= 5) {
            score += DIVERSITY_BONUS
        }

        // 确保分数在 0-100 范围内
        return score.coerceIn(0f, 100f)
    }

    /**
     * 判断是否为原味食谱
     */
    private fun isNaturalFlavorRecipe(recipe: Recipe): Boolean {
        val seasonings = setOf("盐", "糖", "酱油", "醋", "料酒", "味精", "鸡精", "耗油", "豆瓣酱", "番茄酱", "沙拉酱")
        val recipeIngredients = recipe.ingredients.map { it.name.lowercase() }

        // 检查是否含有任何调味品
        return !recipeIngredients.any { ingredient ->
            seasonings.any { seasoning -> ingredient.contains(seasoning) }
        }
    }

    /**
     * 获取多样性描述
     */
    private fun getVarietyDescription(score: Float): String {
        val thresholds = listOf(
            80f to "非常丰富",
            60f to "较为丰富",
            40f to "一般",
            20f to "较少",
            0f to "单调"
        )

        return thresholds.firstOrNull { score >= it.first }?.second ?: "单调"
    }

    /**
     * 自动记录食材尝试
     *
     * 当用户食用食谱时，自动记录食材尝试
     */
    suspend fun autoRecordIngredients(recipe: Recipe, babyId: Long) {
        android.util.Log.d(TAG, "========== 自动记录食材尝试 ==========")
        android.util.Log.d(TAG, "食谱ID: ${recipe.id}")
        android.util.Log.d(TAG, "宝宝ID: $babyId")

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val recipeIngredients = recipe.ingredients.map { it.name }

        // 检查哪些食材是新的
        val triedIngredients = ingredientTrialRepository.getTriedIngredients(babyId)
        val newIngredients = recipeIngredients.filter { it !in triedIngredients }

        // 记录新食材
        newIngredients.forEach { ingredient ->
            val trial = com.example.babyfood.domain.model.IngredientTrial(
                id = 0,
                babyId = babyId,
                ingredientName = ingredient,
                trialDate = today,
                isAllergic = false,
                reaction = null,
                preference = null
            )
            ingredientTrialRepository.insert(trial)
        }

        android.util.Log.d(TAG, "记录新食材数量: ${newIngredients.size}")
        android.util.Log.d(TAG, "✓ 记录完成")
        android.util.Log.d(TAG, "========== 记录完成 ==========")
    }
}

/**
 * 多样性分析结果
 */
data class DiversityAnalysis(
    val recipeId: Long,
    val isNaturalFlavor: Boolean,  // 是否原味
    val diversityScore: Float,  // 多样性分数 (0-100)
    val totalIngredients: Int,  // 总食材数
    val newIngredients: List<String>,  // 新食材列表
    val newIngredientCount: Int,  // 新食材数量
    val triedIngredientCount: Int,  // 已尝试食材数量
    val isHighDiversity: Boolean,  // 是否高多样性
    val varietyDescription: String  // 多样性描述
)

/**
 * 带多样性分数的食谱
 */
data class RecipeWithDiversityScore(
    val recipe: Recipe,
    val diversityScore: Float,
    val isNaturalFlavor: Boolean,
    val newIngredients: List<String>,
    val newIngredientCount: Int
)