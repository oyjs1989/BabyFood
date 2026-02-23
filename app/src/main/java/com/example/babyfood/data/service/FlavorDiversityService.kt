package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.data.remote.api.IngredientTrialsApiService
import com.example.babyfood.data.remote.dto.ingredient_trials.IngredientTrialCreate
import com.example.babyfood.data.remote.dto.ingredient_trials.IngredientTrialUpdate
import com.example.babyfood.data.repository.IngredientTrialRepository
import com.example.babyfood.domain.model.IngredientTrial
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
 * 
 * 【重构说明】现在优先从后端 API 获取口味多样性数据，本地数据库作为离线备份
 */
@Singleton
class FlavorDiversityService @Inject constructor(
    private val ingredientTrialRepository: IngredientTrialRepository,
    private val ingredientTrialsApiService: IngredientTrialsApiService
) {
    companion object {
        private const val TAG = "FlavorDiversityService"
        private const val NEW_INGREDIENT_BONUS = 10.0f  // 新食材加分
        private const val HIGH_FREQUENCY_PENALTY = 5.0f  // 高频食材扣分
        private const val DIVERSITY_BONUS = 5.0f  // 食材种类多样性加分
    }

    /**
     * 从后端 API 获取口味多样性分析
     * 
     * 【新增】优先使用后端 API，失败时返回 null
     */
    suspend fun getFlavorDiversityFromApi(babyId: Long): com.example.babyfood.data.remote.dto.ingredient_trials.FlavorDiversityResponse? {
        Log.d(TAG, "========== 从后端 API 获取口味多样性 ==========")
        Log.d(TAG, "宝宝ID: $babyId")

        return try {
            val response = ingredientTrialsApiService.getFlavorDiversity(babyId.toInt())
            Log.d(TAG, "✓ 从后端 API 获取口味多样性成功")
            Log.d(TAG, "多样性分数: ${response.diversityScore}")
            Log.d(TAG, "已尝试食材数: ${response.totalTried}")
            response
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 从后端 API 获取口味多样性失败: ${e.message}")
            null
        }
    }

    /**
     * 从后端 API 获取已尝试食材列表
     * 
     * 【新增】优先使用后端 API，失败时降级到本地数据库
     */
    suspend fun getTriedIngredients(babyId: Long): List<String> {
        Log.d(TAG, "========== 获取已尝试食材列表 ==========")
        Log.d(TAG, "宝宝ID: $babyId")

        return try {
            // 优先从后端 API 获取
            val ingredients = ingredientTrialsApiService.getTriedIngredients(babyId.toInt())
            Log.d(TAG, "✓ 从后端 API 获取已尝试食材列表成功，共 ${ingredients.size} 个")
            ingredients
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 从后端 API 获取失败: ${e.message}，降级到本地数据库")
            // 降级到本地数据库
            val localIngredients = ingredientTrialRepository.getTriedIngredients(babyId)
            Log.d(TAG, "✓ 从本地数据库获取已尝试食材列表成功，共 ${localIngredients.size} 个")
            localIngredients
        }
    }

    /**
     * 分析食谱的口味多样性
     * 
     * 【重构后】优先从后端 API 获取口味多样性数据
     */
    suspend fun analyzeDiversity(recipe: Recipe, babyId: Long): DiversityAnalysis {
        Log.d(TAG, "========== 分析口味多样性 ==========")
        Log.d(TAG, "食谱ID: ${recipe.id}")
        Log.d(TAG, "食谱名称: ${recipe.name}")
        Log.d(TAG, "宝宝ID: $babyId")

        // 尝试从后端 API 获取口味多样性数据
        val apiDiversity = getFlavorDiversityFromApi(babyId)

        // 获取已尝试食材列表（优先使用 API，失败时使用本地）
        val triedIngredients = getTriedIngredients(babyId)
        Log.d(TAG, "已尝试食材数量: ${triedIngredients.size}")

        // 分析食谱中的食材
        val recipeIngredients = recipe.ingredients.map { it.name }
        Log.d(TAG, "食谱食材数量: ${recipeIngredients.size}")

        // 计算新食材
        val newIngredients = recipeIngredients.filter { it !in triedIngredients }
        Log.d(TAG, "新食材数量: ${newIngredients.size}")
        Log.d(TAG, "新食材: ${newIngredients.joinToString(", ")}")

        // 计算多样性分数
        val diversityScore = if (apiDiversity != null) {
            // 使用 API 返回的分数，但针对当前食谱进行调整
            apiDiversity.diversityScore.toFloat() + (newIngredients.size * NEW_INGREDIENT_BONUS)
        } else {
            // 本地计算
            calculateDiversityScore(recipeIngredients, triedIngredients, newIngredients)
        }.coerceIn(0f, 100f)

        // 判断是否原味（无调味品）
        val isNaturalFlavor = isNaturalFlavorRecipe(recipe)
        Log.d(TAG, "原味食谱: $isNaturalFlavor")

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
            varietyDescription = getVarietyDescription(diversityScore),
            apiDataUsed = apiDiversity != null
        )

        Log.d(TAG, "多样性分数: ${analysis.diversityScore}")
        Log.d(TAG, "多样性描述: ${analysis.varietyDescription}")
        if (apiDiversity != null) {
            Log.d(TAG, "✓ 使用了后端 API 数据")
        } else {
            Log.d(TAG, "⚠️ 使用了本地计算数据")
        }
        Log.d(TAG, "========== 分析完成 ==========")

        return analysis
    }

    /**
     * 推荐多样化食谱
     * 
     * 【重构后】优先从后端 API 获取数据用于排序
     */
    suspend fun recommendDiverseRecipes(recipes: List<Recipe>, babyId: Long): List<RecipeWithDiversityScore> {
        Log.d(TAG, "========== 推荐多样化食谱 ==========")
        Log.d(TAG, "候选食谱数量: ${recipes.size}")
        Log.d(TAG, "宝宝ID: $babyId")

        // 尝试从后端 API 获取口味多样性数据
        val apiDiversity = getFlavorDiversityFromApi(babyId)

        // 获取已尝试食材列表
        val triedIngredients = getTriedIngredients(babyId)
        Log.d(TAG, "已尝试食材: ${triedIngredients.size} 个")

        // 分析每个食谱
        val scoredRecipes = recipes.map { recipe ->
            val recipeIngredients = recipe.ingredients.map { it.name }
            val newIngredients = recipeIngredients.filter { it !in triedIngredients }
            
            // 计算多样性分数
            val diversityScore = if (apiDiversity != null) {
                // 基于 API 数据计算
                val baseScore = apiDiversity.diversityScore.toFloat()
                val newIngredientBonus = newIngredients.size * NEW_INGREDIENT_BONUS
                (baseScore + newIngredientBonus).coerceIn(0f, 100f)
            } else {
                // 本地计算
                calculateDiversityScore(recipeIngredients, triedIngredients, newIngredients)
            }
            
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
        
        Log.d(TAG, "✓ 推荐完成，共 ${sortedRecipes.size} 个食谱")
        if (apiDiversity != null) {
            Log.d(TAG, "✓ 使用了后端 API 数据")
        } else {
            Log.d(TAG, "⚠️ 使用了本地计算数据")
        }
        Log.d(TAG, "========== 推荐完成 ==========")

        return sortedRecipes
    }

    /**
     * 向后端 API 创建食材尝试记录
     * 
     * 【新增】优先使用后端 API，失败时保存到本地
     */
    suspend fun createTrial(trial: IngredientTrial): Result<IngredientTrial> {
        Log.d(TAG, "========== 创建食材尝试记录 ==========")
        Log.d(TAG, "宝宝ID: ${trial.babyId}, 食材: ${trial.ingredientName}")

        return try {
            // 尝试通过后端 API 创建
            val createDto = IngredientTrialCreate(
                babyId = trial.babyId.toInt(),
                ingredientName = trial.ingredientName,
                trialDate = trial.trialDate.toString(),
                reaction = "NONE",  // 默认无反应
                notes = trial.reaction
            )

            val response = ingredientTrialsApiService.createTrial(createDto)
            Log.d(TAG, "✓ 通过后端 API 创建成功，ID: ${response.id}")
            
            // 同时保存到本地数据库（用于离线备份）
            ingredientTrialRepository.insert(trial)
            
            Result.success(trial.copy(id = response.id.toLong()))
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 后端 API 创建失败: ${e.message}，保存到本地数据库")
            
            // 保存到本地数据库
            val id = ingredientTrialRepository.insert(trial)
            Log.d(TAG, "✓ 保存到本地数据库成功，ID: $id")
            
            Result.success(trial.copy(id = id))
        }
    }

    /**
     * 更新食材尝试记录
     * 
     * 【新增】调用后端 API 更新
     */
    suspend fun updateTrial(trialId: Int, reaction: String, notes: String? = null): Result<Unit> {
        Log.d(TAG, "========== 更新食材尝试记录 ==========")
        Log.d(TAG, "记录ID: $trialId, 反应: $reaction")

        return try {
            val updateDto = IngredientTrialUpdate(
                reaction = reaction,
                notes = notes
            )

            ingredientTrialsApiService.updateTrial(trialId, updateDto)
            Log.d(TAG, "✓ 通过后端 API 更新成功")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ 更新失败: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 获取宝宝的所有尝试记录
     * 
     * 【新增】优先从后端 API 获取
     */
    suspend fun getTrialsByBaby(babyId: Long): List<IngredientTrial> {
        Log.d(TAG, "========== 获取宝宝尝试记录 ==========")
        Log.d(TAG, "宝宝ID: $babyId")

        return try {
            // 优先从后端 API 获取
            val apiTrials = ingredientTrialsApiService.getTrialsByBaby(babyId.toInt())
            Log.d(TAG, "✓ 从后端 API 获取成功，共 ${apiTrials.size} 条记录")

            // 转换为本地模型
            apiTrials.map { response ->
                IngredientTrial(
                    id = response.id.toLong(),
                    babyId = response.babyId.toLong(),
                    ingredientName = response.ingredientName,
                    trialDate = kotlinx.datetime.LocalDate.parse(response.trialDate),
                    isAllergic = response.reaction == "SEVERE",
                    reaction = response.reaction,
                    preference = null
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 从后端 API 获取失败: ${e.message}，降级到本地数据库")
            
            // 降级到本地数据库
            val localTrials = ingredientTrialRepository.getTrialsByBabyId(babyId)
            Log.d(TAG, "✓ 从本地数据库获取成功，共 ${localTrials.size} 条记录")
            localTrials
        }
    }

    /**
     * 计算多样性分数（本地计算）
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
     * 自动记录食材尝试（本地）
     * 
     * 【保留】用于离线场景
     */
    suspend fun autoRecordIngredients(recipe: Recipe, babyId: Long) {
        Log.d(TAG, "========== 自动记录食材尝试（本地） ==========")
        Log.d(TAG, "食谱ID: ${recipe.id}")
        Log.d(TAG, "宝宝ID: $babyId")

        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val recipeIngredients = recipe.ingredients.map { it.name }

        // 检查哪些食材是新的
        val triedIngredients = ingredientTrialRepository.getTriedIngredients(babyId)
        val newIngredients = recipeIngredients.filter { it !in triedIngredients }

        // 记录新食材
        newIngredients.forEach { ingredient ->
            val trial = IngredientTrial(
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

        Log.d(TAG, "记录新食材数量: ${newIngredients.size}")
        Log.d(TAG, "✓ 本地记录完成")
        Log.d(TAG, "========== 记录完成 ==========")
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
    val varietyDescription: String,  // 多样性描述
    val apiDataUsed: Boolean = false  // 是否使用了 API 数据
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
