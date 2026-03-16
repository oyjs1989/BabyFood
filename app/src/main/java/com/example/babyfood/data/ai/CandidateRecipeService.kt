package com.example.babyfood.data.ai

import android.util.Log
import com.example.babyfood.data.ai.ruleengine.RuleEngine
import com.example.babyfood.data.remote.api.CandidateRecipesRequest
import com.example.babyfood.data.remote.api.RecipeApiService
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.data.repository.RecipeRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 食谱候选集合服务
 * 优先从后端获取候选食谱，网络失败时降级到本地筛选
 */
@Singleton
class CandidateRecipeService @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val ruleEngine: RuleEngine,
    private val recipeApiService: RecipeApiService
) {

    companion object {
        private const val TAG = "CandidateRecipeService"
    }

    /**
     * 获取候选食谱集合
     * 优先从后端获取，失败时降级到本地筛选
     *
     * @param ageInMonths 宝宝年龄（月）
     * @param allergies 过敏食材列表（硬性过滤）
     * @param preferences 偏好食材列表（仅提供给后端/AI参考）
     * @param availableIngredients 可用食材列表（仅提供给后端/AI参考）
     * @param avoidIngredients 需要避免的食材列表（硬性过滤）
     * @return 候选食谱集合
     */
    suspend fun getCandidateRecipes(
        ageInMonths: Int,
        allergies: List<String>,
        preferences: List<String> = emptyList(),
        availableIngredients: List<String> = emptyList(),
        avoidIngredients: List<String> = emptyList(),
        useAvailableIngredientsOnly: Boolean = false
    ): CandidateRecipeSet {
        Log.d(TAG, "========== 获取候选食谱 ==========")
        Log.d(TAG, "参数: age=$ageInMonths, allergies=${allergies.size}, preferences=${preferences.size}")
        
        // 优先从后端获取
        return try {
            Log.d(TAG, "尝试从后端获取候选食谱...")
            val response = recipeApiService.getCandidates(
                CandidateRecipesRequest(
                    ageInMonths = ageInMonths,
                    allergies = allergies,
                    preferences = preferences,
                    availableIngredients = availableIngredients,
                    avoidIngredients = avoidIngredients,
                    useAvailableIngredientsOnly = useAvailableIngredientsOnly
                )
            )
            
            Log.d(TAG, "✓ 后端返回候选食谱: 总数=${response.totalCount}, 过滤=${response.filteredCount}")
            Log.d(TAG, "  早餐=${response.breakfast.size}, 午餐=${response.lunch.size}, 晚餐=${response.dinner.size}, 点心=${response.snack.size}")
            
            // 将后端返回的 cloudId 映射到本地 Recipe
            mapBackendResponseToCandidateSet(response)
            
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ 后端获取候选食谱失败: ${e.message}，降级到本地筛选")
            // 降级到本地筛选
            getLocalCandidateRecipes(
                ageInMonths = ageInMonths,
                allergies = allergies,
                avoidIngredients = avoidIngredients
            )
        }
    }
    
    /**
     * 将后端响应映射到本地候选集合
     */
    private suspend fun mapBackendResponseToCandidateSet(
        response: com.example.babyfood.data.remote.api.CandidateRecipesResponse
    ): CandidateRecipeSet {
        // 获取所有本地食谱用于映射
        val allLocalRecipes = recipeRepository.getAllRecipes().first()
        val recipeByCloudId = allLocalRecipes.filter { !it.cloudId.isNullOrEmpty() }.associateBy { it.cloudId!! }
        
        // 映射每个餐次的食谱
        fun mapItems(items: List<com.example.babyfood.data.remote.api.CandidateRecipeItem>): List<Recipe> {
            return items.mapNotNull { item ->
                recipeByCloudId[item.cloudId]?.let { recipe ->
                    Log.d(TAG, "  映射成功: ${item.name} -> localId=${recipe.id}")
                    recipe
                } ?: run {
                    Log.w(TAG, "  ⚠️ 未找到本地映射: cloudId=${item.cloudId}, name=${item.name}")
                    null
                }
            }
        }
        
        val breakfast = mapItems(response.breakfast)
        val lunch = mapItems(response.lunch)
        val dinner = mapItems(response.dinner)
        val snack = mapItems(response.snack)
        val allRecipes = (breakfast + lunch + dinner + snack).distinctBy { it.id }
        
        Log.d(TAG, "✓ 映射完成: 总计${allRecipes.size}个食谱")
        
        return CandidateRecipeSet(
            breakfast = breakfast,
            lunch = lunch,
            dinner = dinner,
            snack = snack,
            allRecipes = allRecipes,
            filteredCount = response.filteredCount
        )
    }
    
    /**
     * 本地筛选候选食谱（降级方案）
     */
    private suspend fun getLocalCandidateRecipes(
        ageInMonths: Int,
        allergies: List<String>,
        avoidIngredients: List<String>
    ): CandidateRecipeSet {
        Log.d(TAG, "========== 本地筛选候选食谱（降级） ==========")
        
        // 1. 获取所有食谱
        val allRecipes = recipeRepository.getAllRecipes().first()

        // 2. 硬性过滤：过敏食材和避免食材
        val filteredRecipes = if (allergies.isEmpty() && avoidIngredients.isEmpty()) {
            allRecipes.filter { recipe ->
                ageInMonths >= recipe.minAgeMonths && ageInMonths <= recipe.maxAgeMonths
            }
        } else {
            val filterResult = ruleEngine.filterRecipes(
                recipes = allRecipes,
                ageInMonths = ageInMonths,
                allergies = allergies,
                avoidIngredients = avoidIngredients
            )
            filterResult.passedRecipes
        }

        // 3. 按餐次分类
        val breakfastRecipes = filteredRecipes.filter { inferMealPeriod(it) == MealPeriod.BREAKFAST }
        val lunchRecipes = filteredRecipes.filter { inferMealPeriod(it) == MealPeriod.LUNCH }
        val dinnerRecipes = filteredRecipes.filter { inferMealPeriod(it) == MealPeriod.DINNER }
        val snackRecipes = filteredRecipes.filter { inferMealPeriod(it) == MealPeriod.SNACK }

        Log.d(TAG, "✓ 本地筛选完成: 总计${filteredRecipes.size}个食谱")
        
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
     * 推断食谱的餐段时间段
     */
    private fun inferMealPeriod(recipe: Recipe): MealPeriod {
        val name = recipe.name.lowercase()
        val category = recipe.category.lowercase()
        
        return when {
            // 早餐
            listOf("粥", "面", "面包", "蛋", "奶", "米糊", "燕麦").any { it in name || it in category } -> MealPeriod.BREAKFAST
            // 午餐
            listOf("饭", "肉", "菜", "鸡", "牛", "虾", "鱼", "丸", "饼", "馄饨").any { it in name || it in category } -> MealPeriod.LUNCH
            // 晚餐
            listOf("汤", "蒸", "烧", "焖").any { it in name || it in category } -> MealPeriod.DINNER
            // 点心
            listOf("水果", "酸奶", "饼干", "蛋糕", "布丁", "沙拉").any { it in name || it in category } -> MealPeriod.SNACK
            // 默认
            else -> MealPeriod.LUNCH
        }
    }

    /**
     * 获取食谱替代方案
     *
     * @param recipe 原食谱
     * @param candidateSet 候选食谱集合
     * @return 候选替代食谱列表
     */
    suspend fun getAlternatives(
        recipe: Recipe,
        candidateSet: CandidateRecipeSet
    ): List<Recipe> {
        val mealPeriod = inferMealPeriod(recipe)
        val alternatives = when (mealPeriod) {
            MealPeriod.BREAKFAST -> candidateSet.breakfast
            MealPeriod.LUNCH -> candidateSet.lunch
            MealPeriod.DINNER -> candidateSet.dinner
            MealPeriod.SNACK -> candidateSet.snack
        }

        return alternatives.filter { it.id != recipe.id }
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