package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.domain.model.Recipe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 制作方式推荐服务
 *
 * 根据宝宝月龄推荐自制或市售辅食
 */
@Singleton
class CookingMethodRecommender @Inject constructor() {

    companion object {
        private const val TAG = "CookingMethodRecommender"
    }

    // 适合自制的食材（新鲜、简单）
    private val homemadeFriendly = listOf(
        "南瓜", "胡萝卜", "红薯", "土豆", "菠菜", "西兰花",
        "苹果", "香蕉", "梨", "鸡肉", "鱼肉", "牛肉"
    )

    // 推荐市售的食材（制作复杂或营养要求高）
    private val storeBoughtRecommended = listOf(
        "高铁米粉", "配方米粉", "强化铁米粉",
        "婴儿面条", "婴儿面条",
        "肉泥", "肝泥", "鱼泥",
        "蔬菜泥", "水果泥"
    )

    // 10月龄以上适合自制的复杂食材
    private val advancedHomemade = listOf(
        "饺子", "馄饨", "丸子", "面条", "包子", "馒头",
        "肉饼", "鱼丸", "豆腐", "豆浆"
    )

    /**
     * 制作方式推荐类型
     */
    enum class CookingMethod {
        HOMEMADE,           // 自制
        STORE_BOUGHT,       // 市售
        HOMEMADE_OR_STORE,  // 自制或市售均可
        PROFESSIONAL        // 建议专业制作
    }

    /**
     * 制作方式推荐数据
     */
    data class CookingRecommendation(
        val recipeName: String,
        val recommendedMethod: CookingMethod,
        val reasons: List<String>,
        val tips: List<String>,
        val difficultyLevel: Int  // 1-5, 1最简单
    )

    /**
     * 分析食谱的制作方式推荐
     */
    fun recommendCookingMethod(recipe: Recipe, babyAgeMonths: Int): CookingRecommendation {
        android.util.Log.d(TAG, "========== 开始分析制作方式推荐 ==========")
        android.util.Log.d(TAG, "食谱: ${recipe.name}")
        android.util.Log.d(TAG, "宝宝月龄: $babyAgeMonths 月")

        val ingredients = recipe.ingredients.map { it.name }

        val recommendation = when {
            // 6-9月龄：优先推荐市售，辅以简单自制
            babyAgeMonths <= 9 -> {
                analyzeEarlyStage(recipe, ingredients, babyAgeMonths)
            }

            // 10-12月龄：自制与市售并重
            babyAgeMonths <= 12 -> {
                analyzeMiddleStage(recipe, ingredients, babyAgeMonths)
            }

            // 12月龄以上：鼓励自制
            else -> {
                analyzeLateStage(recipe, ingredients, babyAgeMonths)
            }
        }

        android.util.Log.d(TAG, "✓ 推荐方式: ${recommendation.recommendedMethod}")
        android.util.Log.d(TAG, "========== 分析完成 ==========")

        return recommendation
    }

    /**
     * 早期阶段（6-9月龄）推荐分析
     * 优先市售，辅以简单自制
     */
    private fun analyzeEarlyStage(
        recipe: Recipe,
        ingredients: List<String>,
        babyAgeMonths: Int
    ): CookingRecommendation {
        val containsStoreRecommended = ingredients.any { ingredient ->
            storeBoughtRecommended.any { recommended ->
                recommended.lowercase() in ingredient.lowercase()
            }
        }

        val containsHomemadeFriendly = ingredients.any { ingredient ->
            homemadeFriendly.any { friendly ->
                friendly.lowercase() in ingredient.lowercase()
            }
        }

        return if (containsStoreRecommended) {
            CookingRecommendation(
                recipeName = recipe.name,
                recommendedMethod = CookingMethod.STORE_BOUGHT,
                reasons = listOf(
                    "6-9月龄宝宝营养需求高，市售营养强化产品更可靠",
                    "市售辅食经过专业营养配比，安全卫生",
                    "适合初期辅食，营养均衡，制作方便"
                ),
                tips = listOf(
                    "选择大品牌、口碑好的产品",
                    "注意查看配料表，避免过敏原",
                    "按说明冲调，注意温度",
                    "开封后密封保存，及时食用"
                ),
                difficultyLevel = 1
            )
        } else if (containsHomemadeFriendly && ingredients.size <= 3) {
            CookingRecommendation(
                recipeName = recipe.name,
                recommendedMethod = CookingMethod.HOMEMADE,
                reasons = listOf(
                    "食材简单易得，制作过程安全",
                    "自制可控制食材质量和卫生",
                    "适合尝试自制辅食"
                ),
                tips = listOf(
                    "食材必须彻底清洗干净",
                    "确保充分煮熟煮透",
                    "制作时注意卫生",
                    "现做现吃，不要长时间存放"
                ),
                difficultyLevel = 2
            )
        } else {
            CookingRecommendation(
                recipeName = recipe.name,
                recommendedMethod = CookingMethod.STORE_BOUGHT,
                reasons = listOf(
                    "6-9月龄建议优先使用市售营养强化产品",
                    "自制辅食营养配比不易控制"
                ),
                tips = listOf(
                    "选择适合该月龄段的市售辅食",
                    "注意产品保质期和储存条件"
                ),
                difficultyLevel = 1
            )
        }
    }

    /**
     * 中期阶段（10-12月龄）推荐分析
     * 自制与市售并重
     */
    private fun analyzeMiddleStage(
        recipe: Recipe,
        ingredients: List<String>,
        babyAgeMonths: Int
    ): CookingRecommendation {
        val containsAdvancedIngredients = ingredients.any { ingredient ->
            advancedHomemade.any { advanced ->
                advanced.lowercase() in ingredient.lowercase()
            }
        }

        return if (containsAdvancedIngredients) {
            CookingRecommendation(
                recipeName = recipe.name,
                recommendedMethod = CookingMethod.HOMEMADE,
                reasons = listOf(
                    "10-12月龄可以尝试更复杂的自制辅食",
                    "锻炼宝宝的咀嚼能力",
                    "可添加多种食材，丰富口感"
                ),
                tips = listOf(
                    "注意食材的软硬度，便于咀嚼",
                    "可添加少量调料增加风味",
                    "制作时注意营养搭配",
                    "食材要处理成适合的大小"
                ),
                difficultyLevel = 3
            )
        } else {
            CookingRecommendation(
                recipeName = recipe.name,
                recommendedMethod = CookingMethod.HOMEMADE_OR_STORE,
                reasons = listOf(
                    "10-12月龄自制或市售均可",
                    "可根据时间和条件灵活选择"
                ),
                tips = listOf(
                    "自制时注意营养均衡",
                    "市售产品注意配料表",
                    "可根据宝宝喜好调整制作方式"
                ),
                difficultyLevel = 2
            )
        }
    }

    /**
     * 后期阶段（12月龄以上）推荐分析
     * 鼓励自制
     */
    private fun analyzeLateStage(
        recipe: Recipe,
        ingredients: List<String>,
        babyAgeMonths: Int
    ): CookingRecommendation {
        return CookingRecommendation(
            recipeName = recipe.name,
            recommendedMethod = CookingMethod.HOMEMADE,
            reasons = listOf(
                "12月龄以上推荐自制辅食",
                "可培养宝宝对各种食材的接受度",
                "家庭制作更经济实惠",
                "可根据宝宝口味调整"
            ),
            tips = listOf(
                "食材多样化，营养均衡",
                "注意食物的色香味搭配",
                "可适当使用调料",
                "培养宝宝自主进食能力"
            ),
            difficultyLevel = 4
        )
    }

    /**
     * 获取制作方式的文本描述
     */
    fun getMethodText(method: CookingMethod): String {
        return when (method) {
            CookingMethod.HOMEMADE -> "推荐自制"
            CookingMethod.STORE_BOUGHT -> "推荐市售"
            CookingMethod.HOMEMADE_OR_STORE -> "自制或市售均可"
            CookingMethod.PROFESSIONAL -> "建议专业制作"
        }
    }

    /**
     * 获取制作方式的图标
     */
    fun getMethodIcon(method: CookingMethod): String {
        return when (method) {
            CookingMethod.HOMEMADE -> "👨‍🍳"
            CookingMethod.STORE_BOUGHT -> "🛒"
            CookingMethod.HOMEMADE_OR_STORE -> "👨‍🍳🛒"
            CookingMethod.PROFESSIONAL -> "⭐"
        }
    }

    /**
     * 获取制作方式的颜色
     */
    fun getMethodColor(method: CookingMethod): String {
        return when (method) {
            CookingMethod.HOMEMADE -> "#4CAF50"  // 绿色
            CookingMethod.STORE_BOUGHT -> "#2196F3"  // 蓝色
            CookingMethod.HOMEMADE_OR_STORE -> "#FF9800"  // 橙色
            CookingMethod.PROFESSIONAL -> "#9C27B0"  // 紫色
        }
    }
}