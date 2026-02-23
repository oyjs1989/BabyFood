package com.example.babyfood.data.service

import android.util.Log
import com.example.babyfood.domain.model.Recipe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 新鲜度建议顾问
 *
 * 根据食材类型和宝宝月龄提供新鲜度建议
 */
@Singleton
class FreshnessAdvisor @Inject constructor() {

    companion object {
        private const val TAG = "FreshnessAdvisor"
    }

    // 绿叶蔬菜列表
    private val leafyGreens = listOf(
        "菠菜", "小白菜", "油菜", "空心菜", "芹菜", "韭菜",
        "生菜", "芥蓝", "菜心", "苋菜", "茼蒿", "油麦菜",
        "西兰花", "青菜", "甘蓝", "羽衣甘蓝"
    )

    // 根茎类蔬菜
    private val rootVegetables = listOf(
        "胡萝卜", "白萝卜", "红薯", "土豆", "山药", "芋头",
        "洋葱", "莲藕", "红薯", "南瓜", "冬瓜", "丝瓜",
        "黄瓜", "茄子", "西红柿", "番茄"
    )

    // 易变质食材
    private val perishableIngredients = listOf(
        "豆腐", "鸡蛋", "鱼肉", "虾肉", "鸡肉", "牛肉",
        "猪肉", "肝脏", "虾仁", "蟹肉", "扇贝", "蛤蜊"
    )

    // 冷冻食材建议
    private val frozenFriendly = listOf(
        "豌豆", "玉米", "西兰花", "胡萝卜", "菠菜",
        "虾仁", "鱼肉", "扇贝", "蛤蜊"
    )

    // 罐装食材建议
    private val cannedFriendly = listOf(
        "玉米", "豌豆", "豆类", "番茄酱", "三文鱼"
    )

    /**
     * 新鲜度等级
     */
    enum class FreshnessLevel {
        FRESH,          // 新鲜
        FROZEN_RECOMMENDED,  // 推荐冷冻
        CANNED_ACCEPTABLE,   // 可接受罐装
        CONSIDER_EXPIRY     // 注意保质期
    }

    /**
     * 新鲜度建议数据
     */
    data class FreshnessAdvice(
        val ingredientName: String,
        val level: FreshnessLevel,
        val tips: List<String>,
        val storageAdvice: String,
        val recommendedStorageDays: Int? = null
    )

    /**
     * 分析食谱的新鲜度建议
     */
    fun analyzeRecipeFreshness(recipe: Recipe, babyAgeMonths: Int): List<FreshnessAdvice> {
        android.util.Log.d(TAG, "========== 开始分析食谱新鲜度 ==========")
        android.util.Log.d(TAG, "食谱: ${recipe.name}")
        android.util.Log.d(TAG, "宝宝月龄: $babyAgeMonths 月")

        val advices = mutableListOf<FreshnessAdvice>()

        recipe.ingredients.forEach { ingredient ->
            val advice = getIngredientFreshnessAdvice(ingredient.name, babyAgeMonths)
            advices.add(advice)
        }

        android.util.Log.d(TAG, "✓ 新鲜度建议分析完成: ${advices.size} 个食材")
        android.util.Log.d(TAG, "========== 分析完成 ==========")

        return advices
    }

    /**
     * 获取食材的新鲜度建议
     */
    fun getIngredientFreshnessAdvice(ingredientName: String, babyAgeMonths: Int): FreshnessAdvice {
        val normalizedName = ingredientName.trim().lowercase()

        return when {
            // 绿叶蔬菜 - 建议当天购买当天使用
            leafyGreens.any { it.lowercase() in normalizedName } -> {
                FreshnessAdvice(
                    ingredientName = ingredientName,
                    level = FreshnessLevel.FRESH,
                    tips = listOf(
                        "绿叶蔬菜应选择新鲜、叶片翠绿的",
                        "避免选择叶片发黄或有烂斑的",
                        "清洗后沥干水分，用厨房纸包裹存放"
                    ),
                    storageAdvice = "建议当天购买当天使用，最多存放2天",
                    recommendedStorageDays = 2
                )
            }

            // 根茎类蔬菜 - 可存放较长时间
            rootVegetables.any { it.lowercase() in normalizedName } -> {
                FreshnessAdvice(
                    ingredientName = ingredientName,
                    level = FreshnessLevel.FRESH,
                    tips = listOf(
                        "选择表面光滑、无发芽、无腐烂的",
                        "土豆、红薯应避光保存防止发芽"
                    ),
                    storageAdvice = "可冷藏保存3-5天，部分根茎类可常温存放",
                    recommendedStorageDays = 5
                )
            }

            // 易变质食材 - 注意新鲜度和保质期
            perishableIngredients.any { it.lowercase() in normalizedName } -> {
                FreshnessAdvice(
                    ingredientName = ingredientName,
                    level = FreshnessLevel.CONSIDER_EXPIRY,
                    tips = listOf(
                        "必须选择新鲜的，避免使用不新鲜的食材",
                        "注意食材的气味和质地变化"
                    ),
                    storageAdvice = "新鲜食材建议当天食用，或冷冻保存",
                    recommendedStorageDays = 1
                )
            }

            // 推荐冷冻的食材
            frozenFriendly.any { it.lowercase() in normalizedName } -> {
                FreshnessAdvice(
                    ingredientName = ingredientName,
                    level = FreshnessLevel.FROZEN_RECOMMENDED,
                    tips = listOf(
                        "冷冻食材可保留更多营养成分",
                        "选择速冻包装的，避免反复解冻"
                    ),
                    storageAdvice = "冷冻可保存3-6个月，解冻后应立即食用",
                    recommendedStorageDays = 180
                )
            }

            // 可接受罐装的食材
            cannedFriendly.any { it.lowercase() in normalizedName } -> {
                FreshnessAdvice(
                    ingredientName = ingredientName,
                    level = FreshnessLevel.CANNED_ACCEPTABLE,
                    tips = listOf(
                        "选择无添加糖、无添加盐的罐装食品",
                        "注意检查保质期和罐装状态"
                    ),
                    storageAdvice = "未开封可保存较长时间，开封后应冷藏并尽快食用",
                    recommendedStorageDays = 730
                )
            }

            // 其他食材 - 给出通用建议
            else -> {
                FreshnessAdvice(
                    ingredientName = ingredientName,
                    level = FreshnessLevel.FRESH,
                    tips = listOf(
                        "选择新鲜、无异味、无腐烂的食材",
                        "注意食材的颜色、质地和气味"
                    ),
                    storageAdvice = "根据食材特性选择适当的保存方式"
                )
            }
        }
    }

    /**
     * 获取食材的存储建议摘要
     */
    fun getStorageSummary(advices: List<FreshnessAdvice>): String {
        val freshCount = advices.count { it.level == FreshnessLevel.FRESH }
        val frozenCount = advices.count { it.level == FreshnessLevel.FROZEN_RECOMMENDED }
        val perishableCount = advices.count { it.level == FreshnessLevel.CONSIDER_EXPIRY }

        return when {
            perishableCount > 0 -> "包含 $perishableCount 种易变质食材，建议当天食用或冷冻保存"
            frozenCount > 0 -> "建议优先使用冷冻食材，营养保留更好"
            freshCount > 3 -> "包含多种新鲜食材，注意及时食用"
            else -> "食材新鲜度良好，按需准备即可"
        }
    }

    /**
     * 检查食材是否需要特殊处理
     */
    fun needsSpecialHandling(ingredientName: String): Boolean {
        val normalizedName = ingredientName.trim().lowercase()
        return perishableIngredients.any { it.lowercase() in normalizedName } ||
               leafyGreens.any { it.lowercase() in normalizedName }
    }
}