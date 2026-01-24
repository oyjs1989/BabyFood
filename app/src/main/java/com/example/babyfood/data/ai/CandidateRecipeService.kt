package com.example.babyfood.data.ai

import com.example.babyfood.data.ai.ruleengine.RuleEngine
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.data.repository.RecipeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 食谱候选集合服务
 * 基于规则层过滤候选食谱
 */
@Singleton
class CandidateRecipeService @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val ruleEngine: RuleEngine
) {

    /**
     * 获取候选食谱集合
     * @param ageInMonths 宝宝年龄（月）
     * @param allergies 过敏食材列表
     * @param preferences 偏好食材列表
     * @param availableIngredients 可用食材列表
     * @param avoidIngredients 需要避免的食材列表
     * @return 候选食谱集合，按餐段时间段分类
     */
    suspend fun getCandidateRecipes(
        ageInMonths: Int,
        allergies: List<String>,
        preferences: List<String> = emptyList(),
        availableIngredients: List<String> = emptyList(),
        avoidIngredients: List<String> = emptyList(),
        useAvailableIngredientsOnly: Boolean = false
    ): CandidateRecipeSet {
        // 1. 获取所有食谱
        val allRecipes = recipeRepository.getAllRecipes().first()

        // 2. 应用规则过滤（如果没有过敏食材，则不进行过敏过滤）
        var passedRecipes: List<Recipe> = if (allergies.isEmpty()) {
            // 如果没有过敏食材，只过滤年龄范围
            allRecipes.filter { recipe ->
                ageInMonths >= recipe.minAgeMonths && ageInMonths <= recipe.maxAgeMonths
            }
        } else {
            // 如果有过敏食材，进行完整过滤
            val filterResult = ruleEngine.filterRecipes(
                recipes = allRecipes,
                ageInMonths = ageInMonths,
                allergies = allergies,
                avoidIngredients = avoidIngredients
            )
            filterResult.passedRecipes
        }

        // 3. 根据用户选择处理可用食材
        val ingredientProcessedRecipes = if (availableIngredients.isNotEmpty()) {
            if (useAvailableIngredientsOnly) {
                // 开关开启：只使用包含可用食材的食谱（硬性过滤）
                passedRecipes.filter { recipe ->
                    recipe.ingredients.any { ingredient ->
                        availableIngredients.any { available ->
                            ingredient.name.contains(available, ignoreCase = true)
                        }
                    }
                }
            } else {
                // 开关关闭：不进行硬性过滤，保留所有食谱
                passedRecipes
            }
        } else {
            passedRecipes
        }

        // 4. 如果有可用食材，提升包含这些食材的食谱优先级（无论开关是否开启）
        val prioritizedRecipes = if (availableIngredients.isNotEmpty()) {
            ingredientProcessedRecipes.map { recipe ->
                val hasAvailable = recipe.ingredients.any { ingredient ->
                    availableIngredients.any { available ->
                        ingredient.name.contains(available, ignoreCase = true)
                    }
                }
                // 包含可用食材的食谱优先级为3，偏好食材优先级为2，其他为1
                val preferenceScore = if (preferences.isNotEmpty()) {
                    val hasPreferred = recipe.ingredients.any { ingredient ->
                        preferences.any { pref ->
                            ingredient.name.contains(pref, ignoreCase = true)
                        }
                    }
                    if (hasPreferred) 2 else 1
                } else {
                    1
                }
                RecipeWithPriority(recipe, if (hasAvailable) 3 else preferenceScore)
            }.sortedByDescending { it.priority }.map { it.recipe }
        } else if (preferences.isNotEmpty()) {
            // 如果没有可用食材但有偏好食材，按偏好排序
            ingredientProcessedRecipes.map { recipe ->
                val hasPreferred = recipe.ingredients.any { ingredient ->
                    preferences.any { pref ->
                        ingredient.name.contains(pref, ignoreCase = true)
                    }
                }
                RecipeWithPriority(recipe, if (hasPreferred) 2 else 1)
            }.sortedByDescending { it.priority }.map { it.recipe }
        } else {
            ingredientProcessedRecipes
        }

        // 5. 按餐段时间段分类
        val breakfastRecipes = prioritizedRecipes.filter { isSuitableForMeal(it, MealPeriod.BREAKFAST) }
        val lunchRecipes = prioritizedRecipes.filter { isSuitableForMeal(it, MealPeriod.LUNCH) }
        val dinnerRecipes = prioritizedRecipes.filter { isSuitableForMeal(it, MealPeriod.DINNER) }
        val snackRecipes = prioritizedRecipes.filter { isSuitableForMeal(it, MealPeriod.SNACK) }

        return CandidateRecipeSet(
            breakfast = breakfastRecipes,
            lunch = lunchRecipes,
            dinner = dinnerRecipes,
            snack = snackRecipes,
            allRecipes = prioritizedRecipes,
            filteredCount = 0
        )
    }

    /**
     * 判断食谱是否适合特定餐段时间段
     */
    private fun isSuitableForMeal(recipe: Recipe, mealPeriod: MealPeriod): Boolean {
        return when (mealPeriod) {
            MealPeriod.BREAKFAST -> {
                // 早餐适合：粥、面条、面包、鸡蛋、牛奶等
                val breakfastKeywords = listOf("粥", "面", "面包", "蛋", "奶", "米糊", "燕麦")
                breakfastKeywords.any { keyword ->
                    recipe.category.contains(keyword, ignoreCase = true) ||
                    recipe.name.contains(keyword, ignoreCase = true)
                }
            }
            MealPeriod.LUNCH -> {
                // 午餐适合：米饭、肉类、蔬菜等正餐
                val lunchKeywords = listOf("饭", "肉", "菜", "鸡", "牛", "虾", "鱼", "丸", "饼", "馄饨")
                lunchKeywords.any { keyword ->
                    recipe.category.contains(keyword, ignoreCase = true) ||
                    recipe.name.contains(keyword, ignoreCase = true)
                }
            }
            MealPeriod.DINNER -> {
                // 晚餐适合：易消化的食物、汤类
                val dinnerKeywords = listOf("粥", "汤", "面", "蒸", "烧", "焖")
                dinnerKeywords.any { keyword ->
                    recipe.category.contains(keyword, ignoreCase = true) ||
                    recipe.name.contains(keyword, ignoreCase = true)
                }
            }
            MealPeriod.SNACK -> {
                // 点心适合：水果、酸奶、小饼干等
                val snackKeywords = listOf("水果", "酸奶", "饼干", "蛋糕", "布丁", "沙拉")
                snackKeywords.any { keyword ->
                    recipe.category.contains(keyword, ignoreCase = true) ||
                    recipe.name.contains(keyword, ignoreCase = true)
                }
            }
        }
    }

    /**
     * 获取食谱替代方案
     * @param recipe 原食谱
     * @param candidateSet 候选食谱集合
     * @param ageInMonths 宝宝年龄
     * @param allergies 过敏食材列表
     * @return 替代食谱列表
     */
    suspend fun getAlternatives(
        recipe: Recipe,
        candidateSet: CandidateRecipeSet,
        ageInMonths: Int,
        allergies: List<String>
    ): List<com.example.babyfood.domain.model.RecipeAlternative> {
        val alternatives = mutableListOf<com.example.babyfood.domain.model.RecipeAlternative>()

        // 1. 找到同类别的食谱
        val similarRecipes = candidateSet.allRecipes.filter { it.id != recipe.id }

        // 2. 按营养素相似度排序
        val similarByNutrition: List<Recipe> = similarRecipes.sortedBy { other ->
            val calDiff: Double = kotlin.math.abs((other.nutrition.calories ?: 0f).toDouble() - (recipe.nutrition.calories ?: 0f).toDouble())
            val proteinDiff: Double = kotlin.math.abs((other.nutrition.protein ?: 0f).toDouble() - (recipe.nutrition.protein ?: 0f).toDouble())
            calDiff + proteinDiff * 10.0  // 蛋白质差异权重更高
        }.take(3)

        // 3. 生成替代方案说明
        similarByNutrition.forEach { alternative ->
            val reason = when {
                alternative.category.contains("肉", ignoreCase = true) && recipe.category.contains("肉", ignoreCase = true) -> "同类型肉类替代"
                alternative.category.contains("菜", ignoreCase = true) && recipe.category.contains("菜", ignoreCase = true) -> "同类型蔬菜替代"
                alternative.category.contains("蛋", ignoreCase = true) && recipe.category.contains("蛋", ignoreCase = true) -> "同类型蛋类替代"
                else -> "营养素相近替代"
            }

            val nutritionDifference = buildString {
                val calDiff: Double = (alternative.nutrition.calories ?: 0f).toDouble() - (recipe.nutrition.calories ?: 0f).toDouble()
                val proteinDiff: Double = (alternative.nutrition.protein ?: 0f).toDouble() - (recipe.nutrition.protein ?: 0f).toDouble()
                val calciumDiff: Double = (alternative.nutrition.calcium ?: 0f).toDouble() - (recipe.nutrition.calcium ?: 0f).toDouble()
                val ironDiff: Double = (alternative.nutrition.iron ?: 0f).toDouble() - (recipe.nutrition.iron ?: 0f).toDouble()

                append("热量${if (calDiff > 0) "+" else ""}${calDiff.toInt()}kcal，")
                append("蛋白质${if (proteinDiff > 0) "+" else ""}${proteinDiff.toInt()}g，")
                append("钙${if (calciumDiff > 0) "+" else ""}${calciumDiff.toInt()}mg，")
                append("铁${if (ironDiff > 0) "+" else ""}${ironDiff.toInt()}mg")
            }

            alternatives.add(
                com.example.babyfood.domain.model.RecipeAlternative(
                    recipe = alternative,
                    reason = reason,
                    nutritionDifference = nutritionDifference
                )
            )
        }

        return alternatives
    }
}

/**
 * 候选食谱集合
 */
data class CandidateRecipeSet(
    val breakfast: List<Recipe>,
    val lunch: List<Recipe>,
    val dinner: List<Recipe>,
    val snack: List<Recipe>,
    val allRecipes: List<Recipe>,
    val filteredCount: Int  // 被过滤掉的食谱数量
)

/**
 * 带优先级的食谱
 */
private data class RecipeWithPriority(
    val recipe: Recipe,
    val priority: Int
)