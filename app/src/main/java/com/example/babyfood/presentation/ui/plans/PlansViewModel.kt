package com.example.babyfood.presentation.ui.plans

import androidx.lifecycle.SavedStateHandle
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
import com.example.babyfood.domain.model.RecommendationRequest
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.domain.model.SaveResult
import com.example.babyfood.domain.model.WeeklyMealPlan
import com.example.babyfood.presentation.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import javax.inject.Inject

/**
 * 计划与食谱的合并数据类
 */
data class PlanWithRecipe(
    val plan: Plan,
    val recipe: Recipe?
)

@HiltViewModel
class PlansViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val babyRepository: BabyRepository,
    private val recipeRepository: RecipeRepository,
    private val recommendationService: RecommendationService,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    override val logTag: String = "PlansViewModel"

    companion object {
        private const val KEY_RECOMMENDATION_AVAILABLE = "recommendation_available"
        private const val KEY_RECOMMENDATION_BABY_ID = "recommendation_baby_id"
        private const val KEY_RECOMMENDATION_START_DATE = "recommendation_start_date"
        private const val KEY_RECOMMENDATION_DAYS = "recommendation_days"
    }

    private val _uiState = MutableStateFlow(PlansUiState())
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()

    init {
        logMethodStart("PlansViewModel 初始化")
        loadBabies()
        loadRecipes()
    }

    private fun loadBabies() {
        logMethodStart("加载宝宝列表")
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                logSuccess("宝宝列表加载完成: ${babies.size}个")
                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = babies.firstOrNull()
                )
                loadPlans()
            }
        }
    }

    private fun loadRecipes() {
        logMethodStart("加载食谱列表")
        viewModelScope.launch {
            recipeRepository.getAllRecipes().collect { recipes ->
                logSuccess("食谱列表加载完成: ${recipes.size}个")
                _uiState.value = _uiState.value.copy(recipes = recipes)
                updatePlansWithRecipe()
            }
        }
    }

    private fun updatePlansWithRecipe() {
        val plans = _uiState.value.plans
        val recipes = _uiState.value.recipes
        val plansWithRecipe = plans.map { plan ->
            PlanWithRecipe(
                plan = plan,
                recipe = recipes.find { it.id == plan.recipeId }
            )
        }
        _uiState.value = _uiState.value.copy(plansWithRecipe = plansWithRecipe)
        logD("plansWithRecipe 更新完成: ${plansWithRecipe.size}个")
    }

    suspend fun restoreRecommendationFromSavedState(): WeeklyMealPlan? {
        logMethodStart("恢复推荐数据")

        val hasRecommendation = savedStateHandle.get<Boolean>(KEY_RECOMMENDATION_AVAILABLE) ?: false
        if (!hasRecommendation) {
            logD("SavedStateHandle 中没有推荐标记")
            return null
        }

        val babyId = savedStateHandle.get<Long>(KEY_RECOMMENDATION_BABY_ID)
        val startDateStr = savedStateHandle.get<String>(KEY_RECOMMENDATION_START_DATE)
        val days = savedStateHandle.get<Int>(KEY_RECOMMENDATION_DAYS) ?: 7

        if (babyId == null || startDateStr == null) {
            logError("SavedStateHandle 中推荐数据不完整")
            clearRecommendation()
            return null
        }

        logD("从 SavedStateHandle 恢复推荐: babyId=$babyId, startDate=$startDateStr, days=$days")

        val startDate = LocalDate.parse(startDateStr)
        val result = generateRecommendation(babyId, startDate, days)

        savedStateHandle[KEY_RECOMMENDATION_AVAILABLE] = false
        logD("推荐标记已清除")

        return result
    }

    private fun loadPlans() {
        val selectedBaby = _uiState.value.selectedBaby ?: run {
            logWarning("未选择宝宝，跳过加载计划")
            return
        }

        logMethodStart("加载计划列表")
        viewModelScope.launch {
            planRepository.getPlansByBaby(selectedBaby.id).collect { plans ->
                logSuccess("计划列表加载完成: ${plans.size}个")
                _uiState.value = _uiState.value.copy(
                    plans = plans,
                    isLoading = false
                )
                updatePlansWithRecipe()
            }
        }
    }

    fun selectBaby(baby: Baby) {
        logD("选择宝宝: ${baby.name} (ID: ${baby.id})")
        _uiState.value = _uiState.value.copy(selectedBaby = baby)
        loadPlans()
    }

    fun createPlan(
        babyId: Long,
        recipeId: Long,
        plannedDate: LocalDate,
        mealPeriod: com.example.babyfood.domain.model.MealPeriod,
        notes: String?
    ) {
        logMethodStart("创建计划")
        logD("babyId=$babyId, recipeId=$recipeId, date=$plannedDate, period=$mealPeriod")

        safeLaunch("创建计划") {
            val plan = Plan(
                babyId = babyId,
                recipeId = recipeId,
                plannedDate = plannedDate,
                mealPeriod = mealPeriod.name,
                status = PlanStatus.PLANNED,
                notes = notes
            )
            planRepository.insert(plan)
            logSuccess("计划创建成功: ID=${plan.id}")
            _uiState.value = _uiState.value.copy(isSaved = true, error = null)
            logMethodEnd("创建计划")
        }
    }

    fun updatePlan(plan: Plan) {
        logMethodStart("更新计划")
        logD("ID=${plan.id}, date=${plan.plannedDate}, period=${plan.mealPeriod}")

        safeLaunch("更新计划") {
            planRepository.update(plan)
            logSuccess("计划更新成功")
            _uiState.value = _uiState.value.copy(isSaved = true, error = null)
            logMethodEnd("更新计划")
        }
    }

    fun updatePlanRecipe(planId: Long, newRecipeId: Long) {
        logMethodStart("更新计划食谱")
        logD("planId=$planId, newRecipeId=$newRecipeId")

        safeLaunch("更新计划食谱") {
            val plan = _uiState.value.plans.find { it.id == planId }
            plan?.let {
                planRepository.update(it.copy(recipeId = newRecipeId))
                logSuccess("计划食谱更新成功")
                _uiState.value = _uiState.value.copy(isSaved = true, error = null)
            } ?: logWarning("未找到计划: ID=$planId")
            logMethodEnd("更新计划食谱")
        }
    }

    fun updatePlanStatus(planId: Long, status: PlanStatus) {
        logD("更新计划状态: ID=$planId, status=$status")

        safeLaunch("更新计划状态") {
            val plan = _uiState.value.plans.find { it.id == planId }
            plan?.let {
                planRepository.update(it.copy(status = status))
                logSuccess("计划状态更新成功")
            } ?: logWarning("未找到计划: ID=$planId")
        }
    }

    fun deletePlan(plan: Plan) {
        logMethodStart("删除计划")
        logD("ID=${plan.id}, date=${plan.plannedDate}, period=${plan.mealPeriod}")

        safeLaunch("删除计划") {
            planRepository.delete(plan)
            logSuccess("计划删除成功")
            logMethodEnd("删除计划")
        }
    }

    suspend fun generateDailyRecommendation(babyId: Long, date: LocalDate): WeeklyMealPlan? {
        logMethodStart("生成单日推荐")
        logD("宝宝ID: $babyId, 日期: $date")
        return generateRecommendation(babyId, date, 1)
    }

    suspend fun generateWeeklyRecommendation(babyId: Long, startDate: LocalDate, days: Int): WeeklyMealPlan? {
        logMethodStart("生成周推荐")
        logD("宝宝ID: $babyId, 开始日期: $startDate, 计划天数: $days")
        return generateRecommendation(babyId, startDate, days)
    }

    private suspend fun generateRecommendation(babyId: Long, startDate: LocalDate, days: Int): WeeklyMealPlan? {
        _uiState.value = _uiState.value.copy(isGenerating = true, error = null)

        return try {
            val baby = babyRepository.getById(babyId)
                ?: throw Exception("宝宝不存在")

            logD("宝宝信息: ${baby.name}, ${baby.ageInMonths}个月")

            val constraints = recommendationService.getRecommendedConstraints(babyId)
            logD("约束条件: 每周最多鱼类${constraints.maxFishPerWeek}次, 每周最多蛋类${constraints.maxEggPerWeek}次")

            val request = RecommendationRequest(
                babyId = babyId,
                ageInMonths = baby.ageInMonths,
                allergies = baby.getEffectiveAllergies(),
                preferences = baby.getEffectivePreferences(),
                availableIngredients = emptyList(),
                constraints = constraints,
                startDate = startDate
            )

            logD("调用 RecommendationService.generateRecommendation()...")
            val response = recommendationService.generateRecommendation(request)

            if (response.success && response.weeklyPlan != null) {
                logSuccess("推荐生成成功，周计划天数: ${response.weeklyPlan.dailyPlans.size}")

                val filteredDailyPlans = response.weeklyPlan.dailyPlans.take(days)
                val filteredPlan = response.weeklyPlan.copy(
                    dailyPlans = filteredDailyPlans,
                    endDate = LocalDate.fromEpochDays(startDate.toEpochDays() + (days - 1))
                )

                _uiState.value = _uiState.value.copy(recommendation = filteredPlan, isGenerating = false)

                savedStateHandle[KEY_RECOMMENDATION_AVAILABLE] = true
                savedStateHandle[KEY_RECOMMENDATION_BABY_ID] = babyId
                savedStateHandle[KEY_RECOMMENDATION_START_DATE] = startDate.toString()
                savedStateHandle[KEY_RECOMMENDATION_DAYS] = days
                logD("推荐标记已保存到 SavedStateHandle")

                filteredPlan
            } else {
                logError("推荐生成失败: ${response.errorMessage}")
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    error = response.errorMessage ?: "生成推荐失败"
                )
                null
            }
        } catch (e: Exception) {
            logError("推荐生成异常: ${e.message}", e)
            _uiState.value = _uiState.value.copy(isGenerating = false, error = e.message)
            null
        }
    }

    suspend fun detectRecommendationConflicts(babyId: Long, weeklyPlan: WeeklyMealPlan): List<PlanConflict> {
        logD("开始检测推荐冲突: babyId=$babyId")
        val newPlans = weeklyPlanToPlans(babyId, weeklyPlan)
        logD("待检测计划数: ${newPlans.size}")
        val conflicts = planRepository.detectConflicts(babyId, newPlans)
        logSuccess("冲突检测完成: ${conflicts.size}个冲突")
        return conflicts
    }

    suspend fun updateConflicts(babyId: Long, weeklyPlan: WeeklyMealPlan) {
        logD("更新冲突状态: babyId=$babyId")
        val conflicts = detectRecommendationConflicts(babyId, weeklyPlan)
        _uiState.value = _uiState.value.copy(conflicts = conflicts)
        logSuccess("冲突状态已更新: ${conflicts.size}个冲突")
    }

    suspend fun saveRecommendation(
        babyId: Long,
        weeklyPlan: WeeklyMealPlan,
        conflictResolution: ConflictResolution,
        editedPlans: List<PlannedMeal>? = null
    ): SaveResult {
        logMethodStart("保存推荐")
        logD("宝宝ID: $babyId, 冲突解决方式: $conflictResolution, 编辑后的计划数: ${editedPlans?.size ?: 0}")

        _uiState.value = _uiState.value.copy(isSaving = true, error = null)

        return try {
            val plansToSave = if (editedPlans != null) {
                logD("使用编辑后的计划")
                val updatedDailyPlans = weeklyPlan.dailyPlans.map { dailyPlan ->
                    val editedMeals = editedPlans.filter { meal ->
                        meal.mealPeriod == dailyPlan.meals.firstOrNull()?.mealPeriod
                    }
                    if (editedMeals.isNotEmpty()) dailyPlan.copy(meals = editedMeals) else dailyPlan
                }
                weeklyPlanToPlans(babyId, weeklyPlan.copy(dailyPlans = updatedDailyPlans))
            } else {
                logD("使用原始推荐计划")
                weeklyPlanToPlans(babyId, weeklyPlan)
            }

            logD("待保存计划数: ${plansToSave.size}")
            val result = planRepository.saveRecommendation(babyId, plansToSave, conflictResolution)

            logSuccess("推荐保存完成: success=${result.success}")
            _uiState.value = _uiState.value.copy(isSaving = false, saveResult = result)
            logMethodEnd("保存推荐")
            result
        } catch (e: Exception) {
            logError("推荐保存异常: ${e.message}", e)
            _uiState.value = _uiState.value.copy(isSaving = false, error = e.message)
            SaveResult(success = false, error = e.message)
        }
    }

    fun getPlansByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Plan>> {
        val selectedBaby = _uiState.value.selectedBaby
        return selectedBaby?.let {
            planRepository.getPlansByBabyAndDateRange(it.id, startDate, endDate)
        } ?: flowOf(emptyList())
    }

    private fun weeklyPlanToPlans(babyId: Long, weeklyPlan: WeeklyMealPlan): List<Plan> {
        return weeklyPlan.dailyPlans.flatMap { dailyPlan ->
            dailyPlan.meals.map { meal ->
                Plan(
                    babyId = babyId,
                    recipeId = meal.recipe.id,
                    plannedDate = dailyPlan.date,
                    mealPeriod = meal.mealPeriod.name,
                    status = PlanStatus.PLANNED,
                    notes = meal.nutritionNotes
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSavedFlag() {
        _uiState.value = _uiState.value.copy(isSaved = false)
    }

    fun clearRecommendation() {
        logMethodStart("清除推荐数据")
        _uiState.value = _uiState.value.copy(
            recommendation = null,
            conflicts = emptyList(),
            saveResult = null
        )
        savedStateHandle.remove<Boolean>(KEY_RECOMMENDATION_AVAILABLE)
        savedStateHandle.remove<Long>(KEY_RECOMMENDATION_BABY_ID)
        savedStateHandle.remove<String>(KEY_RECOMMENDATION_START_DATE)
        savedStateHandle.remove<Int>(KEY_RECOMMENDATION_DAYS)
        logSuccess("推荐数据和 SavedStateHandle 已清除")
        logMethodEnd("清除推荐数据")
    }

    fun showRecipeSelector(planId: Long) {
        logD("显示食谱选择器: planId=$planId")
        _uiState.value = _uiState.value.copy(showRecipeSelector = true, selectedPlanId = planId)
    }

    fun dismissRecipeSelector() {
        logD("关闭食谱选择器")
        _uiState.value = _uiState.value.copy(showRecipeSelector = false, selectedPlanId = null)
    }
}

data class PlansUiState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val recipes: List<Recipe> = emptyList(),
    val plans: List<Plan> = emptyList(),
    val plansWithRecipe: List<PlanWithRecipe> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null,
    val isGenerating: Boolean = false,
    val recommendation: WeeklyMealPlan? = null,
    val conflicts: List<PlanConflict> = emptyList(),
    val isSaving: Boolean = false,
    val saveResult: SaveResult? = null,
    val showRecipeSelector: Boolean = false,
    val selectedPlanId: Long? = null
)
