package com.example.babyfood.data.ai.strategy

import com.example.babyfood.domain.model.PlannedMeal
import com.example.babyfood.domain.model.Recipe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 便宜模型策略
 * 负责批量生成文案与解释
 * 使用本地模板生成（可扩展为调用便宜的 LLM API 如 DeepSeek/豆包）
 */
@Singleton
class CheapModelStrategy @Inject constructor() {

    /**
     * 为计划的餐食生成文案
     * @param plannedMeal 计划的餐食
     * @return 更新后的餐食（包含生成的文案）
     */
    fun generateMealTexts(plannedMeal: PlannedMeal): PlannedMeal {
        return plannedMeal.copy(
            nutritionNotes = generateNutritionNotes(plannedMeal.recipe),
            childFriendlyText = generateChildFriendlyText(plannedMeal.recipe)
        )
    }

    /**
     * 批量生成文案
     * @param plannedMeals 计划的餐食列表
     * @return 更新后的餐食列表
     */
    fun generateBatchMealTexts(plannedMeals: List<PlannedMeal>): List<PlannedMeal> {
        return plannedMeals.map { generateMealTexts(it) }
    }

    /**
     * 生成营养说明（给家长看）
     */
    private fun generateNutritionNotes(recipe: Recipe): String {
        val notes = mutableListOf<String>()

        // 热量说明
        when {
            (recipe.nutrition.calories ?: 0f) < 100 -> notes.add("低热量")
            (recipe.nutrition.calories ?: 0f) > 300 -> notes.add("高热量")
            else -> notes.add("适中热量")
        }

        // 蛋白质说明
        when {
            (recipe.nutrition.protein ?: 0f) < 5 -> notes.add("低蛋白")
            (recipe.nutrition.protein ?: 0f) > 15 -> notes.add("高蛋白")
            else -> notes.add("适中蛋白")
        }

        // 钙说明
        if ((recipe.nutrition.calcium ?: 0f) > 100) {
            notes.add("富含钙质")
        }

        // 铁说明
        if ((recipe.nutrition.iron ?: 0f) > 3) {
            notes.add("富含铁质")
        }

        // 根据类别添加特殊说明
        when {
            recipe.category.contains("肉", ignoreCase = true) -> {
                notes.add("提供优质蛋白质")
            }
            recipe.category.contains("菜", ignoreCase = true) -> {
                notes.add("提供维生素和膳食纤维")
            }
            recipe.category.contains("蛋", ignoreCase = true) -> {
                notes.add("提供优质蛋白质和卵磷脂")
            }
            recipe.category.contains("奶", ignoreCase = true) -> {
                notes.add("提供优质蛋白质和钙质")
            }
            recipe.category.contains("鱼", ignoreCase = true) -> {
                notes.add("提供优质蛋白质和DHA")
            }
        }

        return notes.joinToString("，") + "。"
    }

    /**
     * 生成宝宝友好文案（给宝宝听）
     */
    private fun generateChildFriendlyText(recipe: Recipe): String {
        val adjectives = listOf("香香的", "甜甜的", "软软的", "好吃的", "美味的", "营养的")
        val adjective = adjectives.random()

        val mainIngredient = recipe.ingredients.firstOrNull()?.name ?: "好吃的"

        return "今天有${adjective}${recipe.name}，${mainIngredient}${adjective}很好吃哦～"
    }

    /**
     * 生成食谱推荐理由
     */
    fun generateRecommendationReason(recipe: Recipe, ageInMonths: Int): String {
        val reasons = mutableListOf<String>()

        // 年龄适配性
        reasons.add("适合${ageInMonths}个月的宝宝")

        // 营养特点
        if ((recipe.nutrition.protein ?: 0f) > 10) {
            reasons.add("富含蛋白质")
        }
        if ((recipe.nutrition.calcium ?: 0f) > 100) {
            reasons.add("富含钙质")
        }
        if ((recipe.nutrition.iron ?: 0f) > 3) {
            reasons.add("富含铁质")
        }

        // 类别特点
        when {
            recipe.category.contains("肉", ignoreCase = true) -> {
                reasons.add("提供优质蛋白质")
            }
            recipe.category.contains("菜", ignoreCase = true) -> {
                reasons.add("提供维生素")
            }
            recipe.category.contains("蛋", ignoreCase = true) -> {
                reasons.add("易消化吸收")
            }
        }

        return reasons.joinToString("，")
    }

    /**
     * 生成营养亮点
     */
    fun generateNutritionHighlights(recipe: Recipe): List<String> {
        val highlights = mutableListOf<String>()

        if ((recipe.nutrition.calories ?: 0f) > 200) {
            highlights.add("能量充足")
        }
        if ((recipe.nutrition.protein ?: 0f) > 10) {
            highlights.add("蛋白质丰富")
        }
        if ((recipe.nutrition.calcium ?: 0f) > 100) {
            highlights.add("钙质丰富")
        }
        if ((recipe.nutrition.iron ?: 0f) > 3) {
            highlights.add("铁质丰富")
        }

        // 添加维生素相关的亮点
        if (recipe.ingredients.any { it.name.contains("胡萝卜", ignoreCase = true) }) {
            highlights.add("富含维生素A")
        }
        if (recipe.ingredients.any { it.name.contains("菠菜", ignoreCase = true) }) {
            highlights.add("富含叶酸")
        }
        if (recipe.ingredients.any { it.name.contains("番茄", ignoreCase = true) }) {
            highlights.add("富含维生素C")
        }

        return highlights
    }
}