package com.example.babyfood.presentation.ui.plans

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.ai.RecommendationService
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.PlanRepository
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.ConflictResolution
import com.example.babyfood.domain.model.DailyMealPlan
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanConflict
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.domain.model.PlannedMeal
import com.example.babyfood.domain.model.RecommendationConstraints
import com.example.babyfood.domain.model.RecommendationRequest
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.domain.model.SaveResult
import com.example.babyfood.domain.model.WeeklyMealPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class PlansViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val babyRepository: BabyRepository,
    private val recipeRepository: RecipeRepository,
    private val recommendationService: RecommendationService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_RECOMMENDATION_AVAILABLE = "recommendation_available"
        private const val KEY_RECOMMENDATION_BABY_ID = "recommendation_baby_id"
        private const val KEY_RECOMMENDATION_START_DATE = "recommendation_start_date"
        private const val KEY_RECOMMENDATION_DAYS = "recommendation_days"
    }

    private val _uiState = MutableStateFlow(PlansUiState())
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()

    init {
        android.util.Log.d("PlansViewModel", "========== PlansViewModel 初始化 ==========")
        loadBabies()
        loadRecipes()
    }

    private fun loadBabies() {
        android.util.Log.d("PlansViewModel", "开始加载宝宝列表")
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                android.util.Log.d("PlansViewModel", "✓ 宝宝列表加载完成: ${babies.size}个")
                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = babies.firstOrNull()
                )
                loadPlans()
            }
        }
    }

    private fun loadRecipes() {
        android.util.Log.d("PlansViewModel", "开始加载食谱列表")
        viewModelScope.launch {
            recipeRepository.getAllRecipes().collect { recipes ->
                android.util.Log.d("PlansViewModel", "✓ 食谱列表加载完成: ${recipes.size}个")
                _uiState.value = _uiState.value.copy(recipes = recipes)
            }
        }
    }

    /**
     * 从 SavedStateHandle 恢复推荐数据
     * 如果 SavedStateHandle 中有推荐标记，则重新生成推荐
     */
    suspend fun restoreRecommendationFromSavedState(): WeeklyMealPlan? {
        android.util.Log.d("PlansViewModel", "========== 开始恢复推荐数据 ==========")
        
        val hasRecommendation = savedStateHandle.get<Boolean>(KEY_RECOMMENDATION_AVAILABLE) ?: false
        if (!hasRecommendation) {
            android.util.Log.d("PlansViewModel", "⚠️ SavedStateHandle 中没有推荐标记")
            return null
        }
        
        val babyId = savedStateHandle.get<Long>(KEY_RECOMMENDATION_BABY_ID)
        val startDateStr = savedStateHandle.get<String>(KEY_RECOMMENDATION_START_DATE)
        val days = savedStateHandle.get<Int>(KEY_RECOMMENDATION_DAYS) ?: 7
        
        if (babyId == null || startDateStr == null) {
            android.util.Log.e("PlansViewModel", "❌ SavedStateHandle 中推荐数据不完整: babyId=$babyId, startDate=$startDateStr")
            clearRecommendation()
            return null
        }
        
        android.util.Log.d("PlansViewModel", "从 SavedStateHandle 恢复推荐: babyId=$babyId, startDate=$startDateStr, days=$days")
        
        val startDate = kotlinx.datetime.LocalDate.parse(startDateStr)
        
        // 重新生成推荐
        val result = generateRecommendation(babyId, startDate, days)
        
        // 清除标记，避免重复恢复
        savedStateHandle[KEY_RECOMMENDATION_AVAILABLE] = false
        android.util.Log.d("PlansViewModel", "✓ 推荐标记已清除")
        
        return result
    }

    private fun loadPlans() {
        val selectedBaby = _uiState.value.selectedBaby
        if (selectedBaby != null) {
            android.util.Log.d("PlansViewModel", "开始加载宝宝 ${selectedBaby.name} 的计划列表")
            viewModelScope.launch {
                planRepository.getPlansByBaby(selectedBaby.id).collect { plans ->
                    android.util.Log.d("PlansViewModel", "✓ 计划列表加载完成: ${plans.size}个")
                    _uiState.value = _uiState.value.copy(
                        plans = plans,
                        isLoading = false
                    )
                }
            }
        } else {
            android.util.Log.w("PlansViewModel", "⚠️ 未选择宝宝，跳过加载计划")
        }
    }

    fun selectBaby(baby: Baby) {
        android.util.Log.d("PlansViewModel", "选择宝宝: ${baby.name} (ID: ${baby.id})")
        _uiState.value = _uiState.value.copy(selectedBaby = baby)
        loadPlans()
    }

    fun createPlan(
        babyId: Long,
        recipeId: Long,
        plannedDate: kotlinx.datetime.LocalDate,
        mealPeriod: com.example.babyfood.domain.model.MealPeriod,
        notes: String?
    ) {
        android.util.Log.d("PlansViewModel", "创建计划: babyId=$babyId, recipeId=$recipeId, date=$plannedDate, period=$mealPeriod")
        viewModelScope.launch {
            try {
                val plan = Plan(
                    babyId = babyId,
                    recipeId = recipeId,
                    plannedDate = plannedDate,
                    mealPeriod = mealPeriod.name,
                    status = PlanStatus.PLANNED,
                    notes = notes
                )
                planRepository.insertPlan(plan)
                android.util.Log.d("PlansViewModel", "✓ 计划创建成功: ID=${plan.id}")
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                android.util.Log.e("PlansViewModel", "❌ 计划创建失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun updatePlan(plan: Plan) {
        android.util.Log.d("PlansViewModel", "更新计划: ID=${plan.id}, date=${plan.plannedDate}, period=${plan.mealPeriod}")
        viewModelScope.launch {
            try {
                planRepository.updatePlan(plan)
                android.util.Log.d("PlansViewModel", "✓ 计划更新成功")
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                android.util.Log.e("PlansViewModel", "❌ 计划更新失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun updatePlanStatus(planId: Long, status: PlanStatus) {
        android.util.Log.d("PlansViewModel", "更新计划状态: ID=$planId, status=$status")
        viewModelScope.launch {
            try {
                val plan = _uiState.value.plans.find { it.id == planId }
                if (plan != null) {
                    planRepository.updatePlan(plan.copy(status = status))
                    android.util.Log.d("PlansViewModel", "✓ 计划状态更新成功")
                } else {
                    android.util.Log.w("PlansViewModel", "⚠️ 未找到计划: ID=$planId")
                }
            } catch (e: Exception) {
                android.util.Log.e("PlansViewModel", "❌ 计划状态更新失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun deletePlan(plan: Plan) {
        android.util.Log.d("PlansViewModel", "删除计划: ID=${plan.id}, date=${plan.plannedDate}, period=${plan.mealPeriod}")
        viewModelScope.launch {
            try {
                planRepository.deletePlan(plan)
                android.util.Log.d("PlansViewModel", "✓ 计划删除成功")
            } catch (e: Exception) {
                android.util.Log.e("PlansViewModel", "❌ 计划删除失败: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    /**
     * 生成AI推荐（单日）
     */
    suspend fun generateDailyRecommendation(babyId: Long, date: kotlinx.datetime.LocalDate): WeeklyMealPlan? {
        android.util.Log.d("PlansViewModel", "========== 开始生成单日推荐 ==========")
        android.util.Log.d("PlansViewModel", "宝宝ID: $babyId")
        android.util.Log.d("PlansViewModel", "日期: $date")
        return generateRecommendation(babyId, date, 1)
    }

    /**
     * 生成AI推荐（多日）
     */
    suspend fun generateWeeklyRecommendation(babyId: Long, startDate: kotlinx.datetime.LocalDate, days: Int): WeeklyMealPlan? {
        android.util.Log.d("PlansViewModel", "========== 开始生成周推荐 ==========")
        android.util.Log.d("PlansViewModel", "宝宝ID: $babyId")
        android.util.Log.d("PlansViewModel", "开始日期: $startDate")
        android.util.Log.d("PlansViewModel", "计划天数: $days")
        return generateRecommendation(babyId, startDate, days)
    }

    private suspend fun generateRecommendation(babyId: Long, startDate: kotlinx.datetime.LocalDate, days: Int): WeeklyMealPlan? {
        android.util.Log.d("PlansViewModel", "========== 开始生成推荐 ==========")
        android.util.Log.d("PlansViewModel", "宝宝ID: $babyId")
        android.util.Log.d("PlansViewModel", "开始日期: $startDate")
        android.util.Log.d("PlansViewModel", "计划天数: $days")
        
        _uiState.value = _uiState.value.copy(isGenerating = true, error = null)
        
        return try {
            val baby = babyRepository.getBabyById(babyId)
                ?: throw Exception("宝宝不存在")
            
            android.util.Log.d("PlansViewModel", "宝宝信息: ${baby.name}, ${baby.ageInMonths}个月")
            
            val constraints = recommendationService.getRecommendedConstraints(babyId)
            android.util.Log.d("PlansViewModel", "约束条件: 每周最多鱼类${constraints.maxFishPerWeek}次, 每周最多蛋类${constraints.maxEggPerWeek}次")
            
            val request = RecommendationRequest(
                babyId = babyId,
                ageInMonths = baby.ageInMonths,
                allergies = baby.getEffectiveAllergies(),
                preferences = baby.getEffectivePreferences(),
                availableIngredients = emptyList(),
                constraints = constraints,
                startDate = startDate
            )
            
            android.util.Log.d("PlansViewModel", "调用 RecommendationService.generateRecommendation()...")
            val response = recommendationService.generateRecommendation(request)
            
            // 打印 AI 接口返回值的详细信息
            android.util.Log.d("PlansViewModel", "========== AI 接口返回值 ==========")
            android.util.Log.d("PlansViewModel", "success: ${response.success}")
            android.util.Log.d("PlansViewModel", "errorMessage: ${response.errorMessage}")
            if (response.weeklyPlan != null) {
                android.util.Log.d("PlansViewModel", "weeklyPlan.startDate: ${response.weeklyPlan.startDate}")
                android.util.Log.d("PlansViewModel", "weeklyPlan.endDate: ${response.weeklyPlan.endDate}")
                android.util.Log.d("PlansViewModel", "weeklyPlan.dailyPlans.size: ${response.weeklyPlan.dailyPlans.size}")
                response.weeklyPlan.dailyPlans.forEachIndexed { index, dailyPlan ->
                    android.util.Log.d("PlansViewModel", "  Day ${index + 1} (${dailyPlan.date}): ${dailyPlan.meals.size} meals")
                    dailyPlan.meals.forEach { meal ->
                        android.util.Log.d("PlansViewModel", "    - ${meal.mealPeriod.displayName}: ${meal.recipe.name} (ID: ${meal.recipe.id})")
                    }
                }
                android.util.Log.d("PlansViewModel", "nutritionSummary.dailyAverage:")
                android.util.Log.d("PlansViewModel", "  - calories: ${response.weeklyPlan.nutritionSummary.dailyAverage.calories}")
                android.util.Log.d("PlansViewModel", "  - protein: ${response.weeklyPlan.nutritionSummary.dailyAverage.protein}")
                android.util.Log.d("PlansViewModel", "  - calcium: ${response.weeklyPlan.nutritionSummary.dailyAverage.calcium}")
                android.util.Log.d("PlansViewModel", "  - iron: ${response.weeklyPlan.nutritionSummary.dailyAverage.iron}")
                android.util.Log.d("PlansViewModel", "warnings: ${response.warnings.joinToString("; ")}")
            }
            android.util.Log.d("PlansViewModel", "========== AI 接口返回值结束 ==========")
            
            if (response.success && response.weeklyPlan != null) {
                android.util.Log.d("PlansViewModel", "✓ 推荐生成成功")
                android.util.Log.d("PlansViewModel", "周计划天数: ${response.weeklyPlan.dailyPlans.size}")
                
                // 如果天数少于7天，截取前n天的计划
                val filteredDailyPlans = response.weeklyPlan.dailyPlans.take(days)
                val filteredPlan = response.weeklyPlan.copy(
                    dailyPlans = filteredDailyPlans,
                    endDate = kotlinx.datetime.LocalDate.fromEpochDays(startDate.toEpochDays() + (days - 1))
                )
                
                android.util.Log.d("PlansViewModel", "过滤后计划天数: ${filteredPlan.dailyPlans.size}")
                
                // 保存到 UI 状态
                _uiState.value = _uiState.value.copy(
                    recommendation = filteredPlan,
                    isGenerating = false
                )
                
                // 保存推荐标记和基本信息到 SavedStateHandle
                savedStateHandle[KEY_RECOMMENDATION_AVAILABLE] = true
                savedStateHandle[KEY_RECOMMENDATION_BABY_ID] = babyId
                savedStateHandle[KEY_RECOMMENDATION_START_DATE] = startDate.toString()
                savedStateHandle[KEY_RECOMMENDATION_DAYS] = days
                android.util.Log.d("PlansViewModel", "✓ 推荐标记已保存到 SavedStateHandle")
                
                android.util.Log.d("PlansViewModel", "✓ 推荐数据已设置到 UI 状态")
                android.util.Log.d("PlansViewModel", "========== 推荐生成完成 ==========")
                
                filteredPlan
            } else {
                android.util.Log.e("PlansViewModel", "❌ 推荐生成失败: ${response.errorMessage}")
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = response.errorMessage ?: "生成推荐失败"
                )
                android.util.Log.d("PlansViewModel", "========== 推荐生成失败 ==========")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("PlansViewModel", "❌ 推荐生成异常: ${e.message}")
            android.util.Log.e("PlansViewModel", "异常堆栈: ", e)
            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                error = e.message
            )
            android.util.Log.d("PlansViewModel", "========== 推荐生成异常 ==========")
            null
        }
    }

    /**
     * 检测推荐结果的冲突
     */
    suspend fun detectRecommendationConflicts(babyId: Long, weeklyPlan: WeeklyMealPlan): List<PlanConflict> {
        android.util.Log.d("PlansViewModel", "开始检测推荐冲突: babyId=$babyId")
        val newPlans = weeklyPlanToPlans(babyId, weeklyPlan)
        android.util.Log.d("PlansViewModel", "待检测计划数: ${newPlans.size}")
        val conflicts = planRepository.detectConflicts(babyId, newPlans)
        android.util.Log.d("PlansViewModel", "✓ 冲突检测完成: ${conflicts.size}个冲突")
        return conflicts
    }

    /**
     * 更新冲突状态
     */
    suspend fun updateConflicts(babyId: Long, weeklyPlan: WeeklyMealPlan) {
        android.util.Log.d("PlansViewModel", "更新冲突状态: babyId=$babyId")
        val conflicts = detectRecommendationConflicts(babyId, weeklyPlan)
        _uiState.value = _uiState.value.copy(conflicts = conflicts)
        android.util.Log.d("PlansViewModel", "✓ 冲突状态已更新: ${conflicts.size}个冲突")
    }

    /**
     * 保存推荐结果（带冲突处理）
     */
    suspend fun saveRecommendation(
        babyId: Long,
        weeklyPlan: WeeklyMealPlan,
        conflictResolution: ConflictResolution,
        editedPlans: List<PlannedMeal>? = null
    ): SaveResult {
        android.util.Log.d("PlansViewModel", "========== 开始保存推荐 ==========")
        android.util.Log.d("PlansViewModel", "宝宝ID: $babyId")
        android.util.Log.d("PlansViewModel", "冲突解决方式: $conflictResolution")
        android.util.Log.d("PlansViewModel", "编辑后的计划数: ${editedPlans?.size ?: 0}")
        
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        
        return try {
            // 使用编辑后的计划（如果有）
            val plansToSave = if (editedPlans != null) {
                android.util.Log.d("PlansViewModel", "使用编辑后的计划")
                val updatedDailyPlans = weeklyPlan.dailyPlans.map { dailyPlan ->
                    val editedMeals = editedPlans.filter { meal ->
                        meal.mealPeriod == dailyPlan.meals.firstOrNull()?.mealPeriod
                    }
                    if (editedMeals.isNotEmpty()) {
                        dailyPlan.copy(meals = editedMeals)
                    } else {
                        dailyPlan
                    }
                }
                weeklyPlanToPlans(babyId, weeklyPlan.copy(dailyPlans = updatedDailyPlans))
            } else {
                android.util.Log.d("PlansViewModel", "使用原始推荐计划")
                weeklyPlanToPlans(babyId, weeklyPlan)
            }
            
            android.util.Log.d("PlansViewModel", "待保存计划数: ${plansToSave.size}")
            val result = planRepository.saveRecommendation(babyId, plansToSave, conflictResolution)
            
            android.util.Log.d("PlansViewModel", "✓ 推荐保存完成: success=${result.success}")
            if (!result.success) {
                android.util.Log.e("PlansViewModel", "保存失败原因: ${result.error}")
            }
            
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                saveResult = result
            )
            
            android.util.Log.d("PlansViewModel", "========== 推荐保存完成 ==========")
            result
        } catch (e: Exception) {
            android.util.Log.e("PlansViewModel", "❌ 推荐保存异常: ${e.message}")
            android.util.Log.e("PlansViewModel", "异常堆栈: ", e)
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                error = e.message
            )
            android.util.Log.d("PlansViewModel", "========== 推荐保存异常 ==========")
            SaveResult(success = false, error = e.message)
        }
    }

    /**
     * 获取指定日期范围的计划
     */
    fun getPlansByDateRange(startDate: kotlinx.datetime.LocalDate, endDate: kotlinx.datetime.LocalDate): kotlinx.coroutines.flow.Flow<List<Plan>> {
        val selectedBaby = _uiState.value.selectedBaby
        return if (selectedBaby != null) {
            planRepository.getPlansByBabyAndDateRange(selectedBaby.id, startDate, endDate)
        } else {
            kotlinx.coroutines.flow.flowOf(emptyList())
        }
    }

    /**
     * 将WeeklyMealPlan转换为List<Plan>
     */
    private fun weeklyPlanToPlans(babyId: Long, weeklyPlan: WeeklyMealPlan): List<Plan> {
        val plans = mutableListOf<Plan>()
        
        for (dailyPlan in weeklyPlan.dailyPlans) {
            for (meal in dailyPlan.meals) {
                plans.add(
                    Plan(
                        babyId = babyId,
                        recipeId = meal.recipe.id,
                        plannedDate = dailyPlan.date,
                        mealPeriod = meal.mealPeriod.name,
                        status = PlanStatus.PLANNED,
                        notes = meal.nutritionNotes
                    )
                )
            }
        }
        
        return plans
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun clearRecommendation() {
        android.util.Log.d("PlansViewModel", "清除推荐数据")
        _uiState.value = _uiState.value.copy(
            recommendation = null,
            conflicts = emptyList(),
            saveResult = null
        )
        savedStateHandle.remove<Boolean>(KEY_RECOMMENDATION_AVAILABLE)
        savedStateHandle.remove<Long>(KEY_RECOMMENDATION_BABY_ID)
        savedStateHandle.remove<String>(KEY_RECOMMENDATION_START_DATE)
        savedStateHandle.remove<Int>(KEY_RECOMMENDATION_DAYS)
        android.util.Log.d("PlansViewModel", "✓ 推荐数据已清除")
        android.util.Log.d("PlansViewModel", "✓ SavedStateHandle 已清除")
    }
}

data class PlansUiState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val recipes: List<Recipe> = emptyList(),
    val plans: List<Plan> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isGenerating: Boolean = false,
    val recommendation: WeeklyMealPlan? = null,
    val conflicts: List<PlanConflict> = emptyList(),
    val isSaving: Boolean = false,
    val saveResult: SaveResult? = null
)