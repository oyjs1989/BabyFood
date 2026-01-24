package com.example.babyfood.data.ai.strategy

import android.util.Log
import com.example.babyfood.data.ai.CandidateRecipeSet
import com.example.babyfood.data.remote.api.MealDto
import com.example.babyfood.data.remote.api.NutritionSummaryDto
import com.example.babyfood.data.remote.api.RecommendationApiService
import com.example.babyfood.data.remote.api.RecommendationConstraintsDto
import com.example.babyfood.data.remote.api.RecommendationRequest
import com.example.babyfood.data.remote.api.RecommendationResponse
import com.example.babyfood.data.remote.api.WeeklyPlanDto
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.DailyMealPlan as DomainDailyMealPlan
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.NutritionSummary
import com.example.babyfood.domain.model.PlannedMeal
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.domain.model.RecommendationConstraints
import com.example.babyfood.domain.model.WeeklyMealPlan
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 远程推荐策略
 * 调用云端 AI API 生成辅食推荐计划
 * 云端返回指定 JSON 格式，包含食谱 ID 以便本地解析
 */
@Singleton
class RemoteRecommendationStrategy @Inject constructor(
    private val recommendationApiService: RecommendationApiService,
    private val recipeRepository: RecipeRepository
) {

    /**
     * 生成一周饮食计划（远程）
     * @param candidateSet 候选食谱集合（用于获取食谱 ID 映射）
     * @param ageInMonths 宝宝年龄（月）
     * @param constraints 约束条件
     * @param startDate 开始日期
     * @return 一周饮食计划
     */
    suspend fun generateWeeklyPlan(
        candidateSet: CandidateRecipeSet,
        ageInMonths: Int,
        constraints: RecommendationConstraints,
        startDate: LocalDate
    ): WeeklyMealPlan {
        Log.d("RemoteRecommendationStrategy", "========== 开始调用云端 AI 推荐服务 ==========")
        
        // 构建请求
        val request = RecommendationRequest(
            babyId = 0L,  // 临时 ID，实际使用时需要传入
            ageInMonths = ageInMonths,
            allergies = emptyList(),  // 从 candidateSet 中提取
            preferences = emptyList(),  // 从 candidateSet 中提取
            availableIngredients = emptyList(),
            useAvailableIngredientsOnly = false,
            constraints = RecommendationConstraintsDto(
                maxFishPerWeek = constraints.maxFishPerWeek,
                maxEggPerWeek = constraints.maxEggPerWeek,
                breakfastComplexity = constraints.breakfastComplexity.name,
                maxDailyMeals = constraints.maxDailyMeals
            ),
            startDate = startDate.toString(),
            days = 7
        )
        
        Log.d("RemoteRecommendationStrategy", "请求参数: babyId=${request.babyId}, age=${request.ageInMonths}个月")
        Log.d("RemoteRecommendationStrategy", "约束条件: 每周最多鱼类${constraints.maxFishPerWeek}次, 每周最多蛋类${constraints.maxEggPerWeek}次")
        
        return try {
            // 调用云端 API
            Log.d("RemoteRecommendationStrategy", "调用云端 API...")
            val response = recommendationApiService.generateRecommendation(request)
            
            Log.d("RemoteRecommendationStrategy", "✓ 云端 API 响应: success=${response.success}")
            
            if (!response.success) {
                Log.e("RemoteRecommendationStrategy", "❌ 云端 API 返回失败: ${response.errorMessage}")
                throw Exception(response.errorMessage ?: "云端推荐失败")
            }
            
            if (response.weeklyPlan == null) {
                Log.e("RemoteRecommendationStrategy", "❌ 云端 API 返回的周计划为空")
                throw Exception("云端返回的周计划为空")
            }
            
            // 解析响应并转换为领域模型
            val weeklyPlan = parseWeeklyPlan(response.weeklyPlan!!)
            
            Log.d("RemoteRecommendationStrategy", "✓ 周计划解析成功: ${weeklyPlan.dailyPlans.size}天")
            Log.d("RemoteRecommendationStrategy", "========== 云端 AI 推荐服务调用完成 ==========")
            
            weeklyPlan
        } catch (e: Exception) {
            Log.e("RemoteRecommendationStrategy", "❌ 云端 API 调用异常: ${e.message}")
            Log.e("RemoteRecommendationStrategy", "异常堆栈: ", e)
            throw e
        }
    }
    
    /**
     * 解析周计划
     * 将云端返回的 DTO 转换为领域模型
     */
    private suspend fun parseWeeklyPlan(dto: WeeklyPlanDto): WeeklyMealPlan {
        Log.d("RemoteRecommendationStrategy", "开始解析周计划...")
        
        // 获取所有食谱，用于根据 recipeId 查找 Recipe 对象
        val allRecipes = recipeRepository.getAllRecipes().first()
        val recipeMap = allRecipes.associateBy { it.id }
        
        Log.d("RemoteRecommendationStrategy", "本地食谱数: ${allRecipes.size}")
        
        // 解析每日计划
        val dailyPlans = dto.dailyPlans.map { dailyPlanDto ->
            Log.d("RemoteRecommendationStrategy", "  解析日期: ${dailyPlanDto.date}")
            
            // 解析每餐
            val meals = dailyPlanDto.meals.map { mealDto ->
                Log.d("RemoteRecommendationStrategy", "    解析餐食: ${mealDto.mealPeriod}, recipeId=${mealDto.recipeId}")
                
                // 根据 recipeId 查找 Recipe 对象
                val recipe = recipeMap[mealDto.recipeId]?.let { foundRecipe ->
                    Log.d("RemoteRecommendationStrategy", "    ✓ 找到食谱: ${foundRecipe.name}")
                    foundRecipe
                } ?: run {
                    Log.w("RemoteRecommendationStrategy", "    ⚠️ 未找到食谱: recipeId=${mealDto.recipeId}，创建临时食谱")
                    // 创建一个临时 Recipe 对象，避免崩溃
                    Recipe(
                        id = mealDto.recipeId,
                        name = mealDto.recipeName,
                        minAgeMonths = 6,
                        maxAgeMonths = 36,
                        ingredients = emptyList(),
                        steps = emptyList(),
                        nutrition = com.example.babyfood.domain.model.Nutrition(
                            calories = 100f,
                            protein = 5f,
                            fat = 2f,
                            carbohydrates = 15f,
                            fiber = 1f,
                            calcium = 50f,
                            iron = 1f
                        ),
                        category = "未知",
                        isBuiltIn = false
                    )
                }
                
                // 解析餐段时间段
                val mealPeriod = try {
                    MealPeriod.valueOf(mealDto.mealPeriod)
                } catch (e: Exception) {
                    Log.w("RemoteRecommendationStrategy", "    ⚠️ 无效的餐段时间段: ${mealDto.mealPeriod}")
                    MealPeriod.SNACK  // 默认值
                }
                
                PlannedMeal(
                    mealPeriod = mealPeriod,
                    recipe = recipe,  // recipe 不可能为 null，因为上面已经处理了 null 情况
                    nutritionNotes = mealDto.nutritionNotes,
                    childFriendlyText = mealDto.childFriendlyText
                )
            }
            
            DomainDailyMealPlan(
                date = kotlinx.datetime.LocalDate.parse(dailyPlanDto.date),
                meals = meals
            )
        }
        
        // 解析营养摘要
        val nutritionSummary = NutritionSummary(
            weeklyCalories = dto.nutritionSummary.weeklyCalories,
            weeklyProtein = dto.nutritionSummary.weeklyProtein,
            weeklyCalcium = dto.nutritionSummary.weeklyCalcium,
            weeklyIron = dto.nutritionSummary.weeklyIron,
            dailyAverage = com.example.babyfood.domain.model.DailyNutritionAverage(
                calories = dto.nutritionSummary.dailyAverage.calories,
                protein = dto.nutritionSummary.dailyAverage.protein,
                calcium = dto.nutritionSummary.dailyAverage.calcium,
                iron = dto.nutritionSummary.dailyAverage.iron
            ),
            highlights = dto.nutritionSummary.highlights
        )
        
        Log.d("RemoteRecommendationStrategy", "✓ 周计划解析完成")
        
        return WeeklyMealPlan(
            startDate = kotlinx.datetime.LocalDate.parse(dto.startDate),
            endDate = kotlinx.datetime.LocalDate.parse(dto.endDate),
            dailyPlans = dailyPlans,
            nutritionSummary = nutritionSummary,
            alternativeOptions = emptyMap()  // 云端暂不支持替代方案
        )
    }
}