package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.data.remote.api.NutritionApiService
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.domain.model.TextureAdvice
import com.example.babyfood.domain.model.TextureStatus
import com.example.babyfood.domain.model.TextureType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 质地匹配服务
 *
 * 根据宝宝的月龄和咀嚼能力发育阶段，推荐适合质地的食谱
 *
 * 基于5个发育阶段和4级质地分类标准：
 * - 阶段1: 纯奶喂养期（0-6个月）
 * - 阶段2: 辅食引入期（6-8个月）→ 泥糊状
 * - 阶段3: 咀嚼发育期（9-11个月）→ 稠泥/末状
 * - 阶段4: 咀嚼成熟期（12-18个月）→ 软固体/块状
 * - 阶段5: 家庭膳食期（18个月以上）→ 家庭膳食
 *
 * 【重构说明】现在优先从后端 API 获取质地建议，本地计算作为离线备份
 *
 * @property recipeRepository 食谱仓库
 * @property nutritionApiService 营养指导 API 服务
 */
@Singleton
class TextureMatchingService @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val nutritionApiService: NutritionApiService
) {
    private val TAG = "TextureMatchingService"

    /**
     * 从后端 API 获取质地建议
     *
     * 【新增】优先使用后端 API 获取质地建议
     *
     * @param ageInMonths 月龄
     * @return 质地建议，失败时返回 null
     */
    suspend fun getTextureAdviceFromApi(ageInMonths: Int): TextureAdvice? {
        Log.d(TAG, "========== 从后端 API 获取质地建议 ==========")
        Log.d(TAG, "月龄: $ageInMonths 个月")

        return try {
            val response = nutritionApiService.getTextureAdvice(ageInMonths)
            Log.d(TAG, "✓ 从后端 API 获取质地建议成功")

            // 将 API 响应转换为本地模型
            val textureType = try {
                TextureType.valueOf(response.textureType)
            } catch (e: IllegalArgumentException) {
                Log.w(TAG, "⚠️ 未知的质地类型: ${response.textureType}，使用默认值")
                TextureType.getByAge(ageInMonths) ?: TextureType.PUREE
            }

            TextureAdvice(
                currentTexture = textureType,
                recommendedTexture = textureType,
                status = TextureStatus.SUITABLE,
                message = response.description
            )
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 从后端 API 获取质地建议失败: ${e.message}")
            null
        }
    }

    /**
     * 根据宝宝月龄获取适合的食谱列表
     *
     * @param baby 宝宝信息
     * @return 适合质地的食谱列表
     */
    suspend fun getSuitableRecipes(baby: Baby): List<Recipe> {
        Log.d(TAG, "========== 开始获取适合质地的食谱 ==========")
        Log.d(TAG, "宝宝ID: ${baby.id}")
        Log.d(TAG, "宝宝月龄: ${baby.ageInMonths} 个月")
        Log.d(TAG, "咀嚼能力: ${baby.chewingAbility}")

        // 尝试从后端 API 获取质地建议
        val apiTextureAdvice = getTextureAdviceFromApi(baby.ageInMonths)

        // 获取推荐的质地类型（优先使用 API 返回的，否则使用本地计算）
        val recommendedTexture = apiTextureAdvice?.recommendedTexture
            ?: TextureType.getByAge(baby.ageInMonths)

        if (recommendedTexture == null) {
            Log.d(TAG, "⚠️ 宝宝月龄小于6个月，不推荐辅食")
            Log.d(TAG, "========== 获取适合质地的食谱完成 ==========")
            return emptyList()
        }

        Log.d(TAG, "推荐质地: ${recommendedTexture.displayName}")
        if (apiTextureAdvice != null) {
            Log.d(TAG, "✓ 使用了后端 API 的质地建议")
        } else {
            Log.d(TAG, "⚠️ 使用了本地计算的质地建议")
        }

        // 获取适合质地的食谱
        val suitableRecipes: List<Recipe> = recipeRepository.getByTextureTypeSync(recommendedTexture)
        Log.d(TAG, "✓ 找到 ${suitableRecipes.size} 个适合质地的食谱")
        Log.d(TAG, "========== 获取适合质地的食谱完成 ==========")

        return suitableRecipes
    }

    /**
     * 获取适合质地的食谱流
     *
     * @param baby 宝宝信息
     * @return 适合质地的食谱流
     */
    fun getSuitableRecipesFlow(baby: Baby): Flow<List<Recipe>> {
        Log.d(TAG, "========== 开始获取适合质地的食谱流 ==========")
        Log.d(TAG, "宝宝ID: ${baby.id}")
        Log.d(TAG, "宝宝月龄: ${baby.ageInMonths} 个月")

        val recommendedTexture = TextureType.getByAge(baby.ageInMonths)

        if (recommendedTexture == null) {
            Log.d(TAG, "⚠️ 宝宝月龄小于6个月，不推荐辅食")
            Log.d(TAG, "========== 获取适合质地的食谱流完成 ==========")
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }

        Log.d(TAG, "推荐质地: ${recommendedTexture.displayName}")

        val recipesFlow: Flow<List<Recipe>> = recipeRepository.getByTextureType(recommendedTexture)

        val resultFlow = recipesFlow.map { recipes: List<Recipe> ->
            Log.d(TAG, "✓ 找到 ${recipes.size} 个适合质地的食谱")
            recipes
        }

        Log.d(TAG, "========== 获取适合质地的食谱流完成 ==========")
        return resultFlow
    }

    /**
     * 评估食谱对宝宝的质地适配性
     *
     * 【重构后】优先从后端 API 获取质地建议，用于评估适配性
     *
     * @param recipe 食谱
     * @param baby 宝宝信息
     * @return 质地适配建议
     */
    suspend fun evaluateTextureSuitability(recipe: Recipe, baby: Baby): TextureAdvice {
        Log.d(TAG, "========== 开始评估质地适配性 ==========")
        Log.d(TAG, "食谱ID: ${recipe.id}")
        Log.d(TAG, "食谱名称: ${recipe.name}")
        Log.d(TAG, "宝宝ID: ${baby.id}")
        Log.d(TAG, "宝宝月龄: ${baby.ageInMonths} 个月")

        // 尝试从后端 API 获取质地建议
        val apiTextureAdvice = getTextureAdviceFromApi(baby.ageInMonths)

        // 获取食谱的质地类型
        val recipeTextureString = recipe.textureType

        if (recipeTextureString == null) {
            Log.d(TAG, "⚠️ 食谱未设置质地类型")
            val recommendedTexture = apiTextureAdvice?.recommendedTexture
                ?: TextureType.getByAge(baby.ageInMonths)
            val advice = TextureAdvice(
                currentTexture = TextureType.PUREE, // 默认
                recommendedTexture = recommendedTexture,
                status = TextureStatus.TOO_COMPLEX,
                message = "该食谱未设置质地类型，无法评估适配性"
            )
            Log.d(TAG, "========== 评估质地适配性完成 ==========")
            return advice
        }

        // 将String转换为TextureType
        val recipeTexture = try {
            TextureType.valueOf(recipeTextureString)
        } catch (e: IllegalArgumentException) {
            Log.d(TAG, "⚠️ 无效的质地类型: $recipeTextureString")
            val recommendedTexture = apiTextureAdvice?.recommendedTexture
                ?: TextureType.getByAge(baby.ageInMonths)
            val advice = TextureAdvice(
                currentTexture = TextureType.PUREE, // 默认
                recommendedTexture = recommendedTexture,
                status = TextureStatus.TOO_COMPLEX,
                message = "该食谱的质地类型无效: $recipeTextureString"
            )
            Log.d(TAG, "========== 评估质地适配性完成 ==========")
            return advice
        }

        Log.d(TAG, "食谱质地: ${recipeTexture.displayName}")

        // 使用 API 获取的推荐质地或本地计算的推荐质地
        val recommendedTexture = apiTextureAdvice?.recommendedTexture
            ?: TextureType.getByAge(baby.ageInMonths)

        // 如果 API 返回了描述，使用 API 的描述，否则使用本地计算的建议
        val advice = if (apiTextureAdvice != null && recommendedTexture != null) {
            // 根据 API 建议评估适配性
            val status = when {
                recipeTexture == recommendedTexture -> TextureStatus.SUITABLE
                recipeTexture.ordinal < recommendedTexture.ordinal -> TextureStatus.TOO_SIMPLE
                else -> TextureStatus.TOO_COMPLEX
            }

            TextureAdvice(
                currentTexture = recipeTexture,
                recommendedTexture = recommendedTexture,
                status = status,
                message = apiTextureAdvice.message
            )
        } else {
            // 降级到本地计算
            TextureType.getTextureAdvice(recipeTexture, baby.ageInMonths)
        }

        Log.d(TAG, "========== 评估质地适配性完成 ==========")
        return advice
    }

    /**
     * 根据宝宝月龄推荐质地类型
     *
     * 【重构后】优先从后端 API 获取质地建议
     *
     * @param baby 宝宝信息
     * @return 推荐的质地类型
     */
    suspend fun recommendTextureType(baby: Baby): TextureType? {
        Log.d(TAG, "========== 开始推荐质地类型 ==========")
        Log.d(TAG, "宝宝ID: ${baby.id}")
        Log.d(TAG, "宝宝月龄: ${baby.ageInMonths} 个月")
        Log.d(TAG, "咀嚼能力: ${baby.chewingAbility}")

        // 优先从后端 API 获取质地建议
        val apiTextureAdvice = getTextureAdviceFromApi(baby.ageInMonths)

        val recommendedTexture = apiTextureAdvice?.recommendedTexture
            ?: TextureType.getByAge(baby.ageInMonths)

        if (recommendedTexture == null) {
            Log.d(TAG, "⚠️ 宝宝月龄小于6个月，不推荐辅食")
            Log.d(TAG, "========== 推荐质地类型完成 ==========")
            return null
        }

        if (apiTextureAdvice != null) {
            Log.d(TAG, "✓ 使用了后端 API 的质地建议: ${recommendedTexture.displayName}")
        } else {
            Log.d(TAG, "⚠️ 使用了本地计算的质地建议: ${recommendedTexture.displayName}")
        }

        // 根据咀嚼能力调整推荐
        val adjustedTexture = when (baby.chewingAbility) {
            "STRONG" -> {
                // 咀嚼能力强，可以尝试更复杂的质地
                when (recommendedTexture) {
                    TextureType.PUREE -> TextureType.THICK_PUREE
                    TextureType.THICK_PUREE -> TextureType.SOFT_SOLID
                    else -> recommendedTexture
                }
            }
            "WEAK" -> {
                // 咀嚼能力弱，选择更简单的质地
                when (recommendedTexture) {
                    TextureType.SOFT_SOLID -> TextureType.THICK_PUREE
                    TextureType.THICK_PUREE -> TextureType.PUREE
                    else -> recommendedTexture
                }
            }
            else -> recommendedTexture
        }

        if (adjustedTexture != recommendedTexture) {
            Log.d(TAG, "⚠️ 根据咀嚼能力调整推荐质地")
            Log.d(TAG, "原始推荐: ${recommendedTexture.displayName}")
            Log.d(TAG, "调整后推荐: ${adjustedTexture.displayName}")
        }

        Log.d(TAG, "✓ 推荐质地: ${adjustedTexture.displayName}")
        Log.d(TAG, "========== 推荐质地类型完成 ==========")

        return adjustedTexture
    }

    /**
     * 获取质地升级建议
     *
     * 当宝宝月龄增长时，建议逐步升级质地类型
     *
     * @param currentTexture 当前质地类型
     * @param ageInMonths 月龄
     * @return 质地升级建议
     */
    suspend fun getTextureUpgradeAdvice(currentTexture: TextureType, ageInMonths: Int): TextureUpgradeAdvice {
        Log.d(TAG, "========== 开始获取质地升级建议 ==========")
        Log.d(TAG, "当前质地: ${currentTexture.displayName}")
        Log.d(TAG, "月龄: $ageInMonths 个月")

        // 优先从后端 API 获取质地建议
        val apiTextureAdvice = getTextureAdviceFromApi(ageInMonths)
        val recommendedTexture = apiTextureAdvice?.recommendedTexture
            ?: TextureType.getByAge(ageInMonths)

        if (recommendedTexture == null) {
            Log.d(TAG, "⚠️ 月龄小于6个月，不推荐辅食")
            val advice = TextureUpgradeAdvice(
                currentTexture = currentTexture,
                shouldUpgrade = false,
                recommendedTexture = null,
                reason = "宝宝月龄小于6个月，还不适合添加辅食"
            )
            Log.d(TAG, "========== 获取质地升级建议完成 ==========")
            return advice
        }

        // 检查是否需要升级
        val shouldUpgrade = when {
            currentTexture == TextureType.PUREE && recommendedTexture == TextureType.THICK_PUREE -> true
            currentTexture == TextureType.THICK_PUREE && recommendedTexture == TextureType.SOFT_SOLID -> true
            currentTexture == TextureType.SOFT_SOLID && recommendedTexture == TextureType.FAMILY_MEAL -> true
            else -> false
        }

        val reason = if (shouldUpgrade) {
            val source = if (apiTextureAdvice != null) "后端 API 建议" else "本地计算"
            "宝宝月龄已达到${recommendedTexture.applicableAgeRange.first}个月，$source 可以尝试更丰富的质地以锻炼咀嚼能力"
        } else {
            "当前质地适合宝宝月龄，无需升级"
        }

        val advice = TextureUpgradeAdvice(
            currentTexture = currentTexture,
            shouldUpgrade = shouldUpgrade,
            recommendedTexture = if (shouldUpgrade) recommendedTexture else null,
            reason = reason
        )

        Log.d(TAG, "是否需要升级: ${advice.shouldUpgrade}")
        Log.d(TAG, "升级原因: ${advice.reason}")
        Log.d(TAG, "========== 获取质地升级建议完成 ==========")

        return advice
    }

    /**
     * 获取所有质地类型的发育阶段信息
     *
     * @return 质地发育阶段列表
     */
    fun getTextureStages(): List<TextureStageInfo> {
        Log.d(TAG, "========== 开始获取质地发育阶段信息 ==========")

        val stages = TextureType.entries.map { textureType ->
            TextureStageInfo(
                textureType = textureType,
                ageRange = textureType.applicableAgeRange,
                description = textureType.description,
                chewingAbility = textureType.chewingAbility,
                example = textureType.example
            )
        }

        Log.d(TAG, "✓ 获取到 ${stages.size} 个发育阶段")
        Log.d(TAG, "========== 获取质地发育阶段信息完成 ==========")

        return stages
    }
}

/**
 * 质地升级建议
 *
 * @property currentTexture 当前质地
 * @property shouldUpgrade 是否需要升级
 * @property recommendedTexture 推荐升级到的质地
 * @property reason 升级原因
 */
data class TextureUpgradeAdvice(
    val currentTexture: TextureType,
    val shouldUpgrade: Boolean,
    val recommendedTexture: TextureType?,
    val reason: String
)

/**
 * 质地发育阶段信息
 *
 * @property textureType 质地类型
 * @property ageRange 适用月龄范围
 * @property description 描述
 * @property chewingAbility 咀嚼能力
 * @property example 示例
 */
data class TextureStageInfo(
    val textureType: TextureType,
    val ageRange: IntRange,
    val description: String,
    val chewingAbility: String,
    val example: String
)
