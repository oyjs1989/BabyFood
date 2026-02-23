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
 * 遵循奥卡姆剃刀原则：仅做必要的硬性过滤（如过敏），其余判断交给AI
 */
@Singleton
class CandidateRecipeService @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val ruleEngine: RuleEngine
) {

    /**
     * 获取候选食谱集合
     * 奥卡姆剃刀原则：仅过滤过敏食材，其余排序和优先级由AI决定
     *
     * @param ageInMonths 宝宝年龄（月）
     * @param allergies 过敏食材列表（硬性过滤）
     * @param preferences 偏好食材列表（仅提供给AI参考）
     * @param availableIngredients 可用食材列表（仅提供给AI参考）
     * @param avoidIngredients 需要避免的食材列表（硬性过滤）
     * @return 候选食谱集合，不做任何本地排序
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

        // 2. 仅做硬性过滤：过敏食材（绝对禁用）
        val filteredRecipes = if (allergies.isEmpty()) {
            // 无过敏：只过滤年龄范围
            allRecipes.filter { recipe ->
                ageInMonths >= recipe.minAgeMonths && ageInMonths <= recipe.maxAgeMonths
            }
        } else {
            // 有过敏：过滤过敏食材和避免食材
            val filterResult = ruleEngine.filterRecipes(
                recipes = allRecipes,
                ageInMonths = ageInMonths,
                allergies = allergies,
                avoidIngredients = avoidIngredients
            )
            filterResult.passedRecipes
        }

        // 3. 奥卡姆剃刀：不做任何本地排序，将所有信息提供给AI
        //    AI会根据偏好、可用食材、营养需求等进行智能排序和选择

        // 4. 按餐段时间段分类（仅做基础分类，不排序）
        val breakfastRecipes = filteredRecipes.filter { isSuitableForMeal(it, MealPeriod.BREAKFAST) }
        val lunchRecipes = filteredRecipes.filter { isSuitableForMeal(it, MealPeriod.LUNCH) }
        val dinnerRecipes = filteredRecipes.filter { isSuitableForMeal(it, MealPeriod.DINNER) }
        val snackRecipes = filteredRecipes.filter { isSuitableForMeal(it, MealPeriod.SNACK) }

        return CandidateRecipeSet(
            breakfast = breakfastRecipes,
            lunch = lunchRecipes,
            dinner = dinnerRecipes,
            snack = snackRecipes,
            allRecipes = filteredRecipes,
            filteredCount = allRecipes.size - filteredRecipes.size
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
     * 奥卡姆剃刀原则：仅返回同餐次的食谱，由AI进行智能匹配
     *
     * @param recipe 原食谱
     * @param candidateSet 候选食谱集合
     * @return 候选替代食谱列表（不做任何排序）
     */
    suspend fun getAlternatives(
        recipe: Recipe,
        candidateSet: CandidateRecipeSet
    ): List<Recipe> {
        // 返回所有同餐次的食谱（排除自身）
        val mealPeriod = inferMealPeriod(recipe)
        val alternatives = when (mealPeriod) {
            MealPeriod.BREAKFAST -> candidateSet.breakfast
            MealPeriod.LUNCH -> candidateSet.lunch
            MealPeriod.DINNER -> candidateSet.dinner
            MealPeriod.SNACK -> candidateSet.snack
        }

        return alternatives.filter { it.id != recipe.id }
    }

    /**
     * 推断食谱的餐段时间段
     */
    private fun inferMealPeriod(recipe: Recipe): MealPeriod {
        return when {
            isSuitableForMeal(recipe, MealPeriod.BREAKFAST) -> MealPeriod.BREAKFAST
            isSuitableForMeal(recipe, MealPeriod.LUNCH) -> MealPeriod.LUNCH
            isSuitableForMeal(recipe, MealPeriod.DINNER) -> MealPeriod.DINNER
            isSuitableForMeal(recipe, MealPeriod.SNACK) -> MealPeriod.SNACK
            else -> MealPeriod.LUNCH  // 默认
        }
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