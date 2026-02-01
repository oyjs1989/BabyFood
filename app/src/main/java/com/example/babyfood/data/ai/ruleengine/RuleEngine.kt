package com.example.babyfood.data.ai.ruleengine

import com.example.babyfood.domain.model.Recipe
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 规则引擎
 * 奥卡姆剃刀原则：仅保留必要的安全过滤（如过敏食材），其余判断交给AI
 */
@Singleton
class RuleEngine @Inject constructor() {

    /**
     * 过滤食谱：仅过滤过敏食材（硬性过滤）
     * 奥卡姆剃刀原则：不做任何营养范围、年龄禁忌等复杂判断，交给AI处理
     *
     * @param recipes 候选食谱列表
     * @param ageInMonths 宝宝年龄（月）
     * @param allergies 过敏食材列表（硬性过滤）
     * @param avoidIngredients 需要避免的食材列表（硬性过滤）
     * @return 过滤后的食谱列表和被过滤的原因
     */
    fun filterRecipes(
        recipes: List<Recipe>,
        ageInMonths: Int,
        allergies: List<String>,
        avoidIngredients: List<String> = emptyList()
    ): FilterResult {
        val passedRecipes = mutableListOf<Recipe>()
        val filteredRecipes = mutableListOf<FilteredRecipe>()

        recipes.forEach { recipe ->
            val reasons = mutableListOf<String>()

            // 1. 检查年龄范围（仅做基础过滤）
            if (ageInMonths < recipe.minAgeMonths || ageInMonths > recipe.maxAgeMonths) {
                reasons.add("年龄不匹配（${ageInMonths}个月，适用范围：${recipe.minAgeMonths}-${recipe.maxAgeMonths}个月）")
            }

            // 2. 检查过敏食材（硬性过滤）
            val recipeIngredients = recipe.ingredients.map { it.name }
            allergies.forEach { allergy ->
                if (recipeIngredients.any { it.contains(allergy, ignoreCase = true) }) {
                    reasons.add("含过敏食材：$allergy")
                }
            }

            // 3. 检查避免食材（硬性过滤）
            avoidIngredients.forEach { avoid ->
                if (recipeIngredients.any { it.contains(avoid, ignoreCase = true) }) {
                    reasons.add("含避免食材：$avoid")
                }
            }

            // 奥卡姆剃刀：不做营养范围检查、年龄禁忌检查等复杂判断
            // 这些判断交给AI处理

            if (reasons.isEmpty()) {
                passedRecipes.add(recipe)
            } else {
                filteredRecipes.add(FilteredRecipe(recipe, reasons))
            }
        }

        return FilterResult(
            passedRecipes = passedRecipes,
            filteredRecipes = filteredRecipes
        )
    }

    /**
     * 验证一周饮食计划
     * 奥卡姆剃刀原则：仅记录违规和警告，不影响AI结果
     *
     * @param recipes 食谱列表
     * @param constraints 验证约束条件
     * @return 验证结果（仅供参考）
     */
    fun validateWeeklyPlan(
        recipes: List<Recipe>,
        constraints: ValidationConstraints
    ): ValidationResult {
        val warnings = mutableListOf<String>()
        val violations = mutableListOf<String>()

        // 1. 统计各类食材出现次数（仅供参考）
        val fishCount = recipes.count { it.category.contains("鱼", ignoreCase = true) }
        val eggCount = recipes.count { it.category.contains("蛋", ignoreCase = true) }

        // 2. 检查约束（仅供参考，不阻止AI结果）
        if (constraints.maxFishPerWeek > 0 && fishCount > constraints.maxFishPerWeek) {
            violations.add("鱼类出现次数超过限制（$fishCount 次，限制：${constraints.maxFishPerWeek} 次）")
        }

        if (constraints.maxEggPerWeek > 0 && eggCount > constraints.maxEggPerWeek) {
            violations.add("蛋类出现次数超过限制（$eggCount 次，限制：${constraints.maxEggPerWeek} 次）")
        }

        // 3. 检查营养均衡性（仅供参考）
        val totalCalories: Double = recipes.sumOf { (it.nutrition.calories ?: 0f).toDouble() }
        if (recipes.isNotEmpty()) {
            val avgCalories: Double = totalCalories / recipes.size
            if (avgCalories < constraints.minDailyCalories) {
                warnings.add("平均热量偏低（${avgCalories.toInt()} kcal，建议：${constraints.minDailyCalories.toInt()} kcal）")
            } else if (avgCalories > constraints.maxDailyCalories) {
                warnings.add("平均热量偏高（${avgCalories.toInt()} kcal，建议：${constraints.maxDailyCalories.toInt()} kcal）")
            }
        }

        // 4. 检查过敏食材（仅供参考）
        val allIngredients = recipes.flatMap { it.ingredients.map { it.name } }
        constraints.allergies.forEach { allergy ->
            if (allIngredients.any { it.contains(allergy, ignoreCase = true) }) {
                violations.add("计划中包含过敏食材：$allergy")
            }
        }

        // 奥卡姆剃刀：不做年龄禁忌检查，交给AI处理

        return ValidationResult(
            isValid = violations.isEmpty(),
            warnings = warnings,
            violations = violations
        )
    }
}

/**
 * 过滤结果
 */
data class FilterResult(
    val passedRecipes: List<Recipe>,
    val filteredRecipes: List<FilteredRecipe>
)

/**
 * 被过滤的食谱
 */
data class FilteredRecipe(
    val recipe: Recipe,
    val reasons: List<String>
)

/**
 * 验证约束条件
 */
data class ValidationConstraints(
    val ageInMonths: Int,
    val allergies: List<String>,
    val maxFishPerWeek: Int = 2,
    val maxEggPerWeek: Int = 3,
    val minDailyCalories: Double = 800.0,
    val maxDailyCalories: Double = 1200.0
)

/**
 * 验证结果
 */
data class ValidationResult(
    val isValid: Boolean,
    val warnings: List<String>,
    val violations: List<String>
)