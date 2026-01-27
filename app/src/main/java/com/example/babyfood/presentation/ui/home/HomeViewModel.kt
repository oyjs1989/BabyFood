package com.example.babyfood.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.preferences.PreferencesManager
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.data.repository.PlanRepository
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.plus
import javax.inject.Inject
import kotlin.random.Random
import android.util.Log

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val planRepository: PlanRepository,
    private val recipeRepository: RecipeRepository,
    private val healthRecordRepository: HealthRecordRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    companion object {
        private const val TAG = "HomeViewModel"
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        Log.d(TAG, "========== 开始加载数据 ==========")
        viewModelScope.launch {
            // 加载宝宝信息
            babyRepository.getAllBabies().collect { babies ->
                Log.d(TAG, "宝宝列表加载完成，共 ${babies.size} 个宝宝")

                // 从 SharedPreferences 加载之前选中的宝宝 ID
                val savedBabyId = preferencesManager.getSelectedBabyId()
                Log.d(TAG, "保存的宝宝 ID: $savedBabyId")

                // 尝试找到保存的宝宝
                val selectedBaby = if (savedBabyId != -1L) {
                    babies.find { it.id == savedBabyId } ?: babies.firstOrNull()
                } else {
                    babies.firstOrNull()
                }

                if (selectedBaby != null) {
                    Log.d(TAG, "选中的宝宝: ${selectedBaby.name} (ID: ${selectedBaby.id})")
                } else {
                    Log.d(TAG, "未选中任何宝宝")
                }

                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = selectedBaby
                )
                loadTodayPlans()
                Log.d(TAG, "✓ 数据加载完成 ==========")
            }
        }
    }

    private fun loadTodayPlans() {
        val selectedBaby = _uiState.value.selectedBaby
        if (selectedBaby != null) {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            viewModelScope.launch {
                // 加载今日计划
                planRepository.getPlansByBabyAndDate(selectedBaby.id, today).collect { plans ->
                    // 加载食谱详情
                    val plansWithRecipes = plans.map { plan ->
                        val recipe = recipeRepository.getRecipeById(plan.recipeId)
                        PlanWithRecipe(plan, recipe)
                    }

                    // 加载未来一周的计划
                    val weeklyPlans = loadWeeklyPlans(selectedBaby.id, today)

                    // 加载最新的体检数据
                    val latestRecord = healthRecordRepository.getLatestHealthRecord(selectedBaby.id)

                    _uiState.value = _uiState.value.copy(
                        todayPlans = plansWithRecipes,
                        weeklyPlans = weeklyPlans,
                        nutritionGoal = selectedBaby.getEffectiveNutritionGoal(),
                        latestHealthRecord = latestRecord,
                        isLoading = false
                    )
                }
            }
        } else {
            // 没有宝宝时，设置加载完成
            _uiState.value = _uiState.value.copy(
                isLoading = false
            )
        }
    }

    private suspend fun loadWeeklyPlans(babyId: Long, startDate: LocalDate): Map<LocalDate, List<PlanWithRecipe>> {
        return (1..7).associate { dayOffset ->
            val date = startDate.plus(dayOffset, kotlinx.datetime.DateTimeUnit.DAY)
            val plans = planRepository.getPlansByBabyAndDate(babyId, date).first()
            val plansWithRecipes = plans.map { plan ->
                val recipe = recipeRepository.getRecipeById(plan.recipeId)
                PlanWithRecipe(plan, recipe)
            }
            date to plansWithRecipes
        }
    }

    fun selectBaby(baby: Baby) {
        Log.d(TAG, "========== 切换宝宝 ==========")
        Log.d(TAG, "切换到宝宝: ${baby.name} (ID: ${baby.id})")

        _uiState.value = _uiState.value.copy(selectedBaby = baby)

        // 保存到 SharedPreferences
        preferencesManager.saveSelectedBabyId(baby.id)

        loadTodayPlans()
        Log.d(TAG, "✓ 切换完成 ==========")
    }

    // 换一换功能
    fun shuffleMealPeriod(period: MealPeriod) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        viewModelScope.launch {
            // 获取当前计划的食谱ID（用于排除）
            val currentPlan = _uiState.value.todayPlans.find { it.plan.mealPeriod == period.name }
            val excludeRecipeIds = currentPlan?.recipe?.id?.let { listOf(it) } ?: emptyList()

            // 获取适合该月龄的所有食谱
            val recipes = recipeRepository.getRecipesByAge(selectedBaby.ageInMonths).first()

            // 排除过敏原
            val filteredRecipes = recipes.filter { recipe ->
                recipe.ingredients.none { ingredient ->
                    selectedBaby.getEffectiveAllergies().contains(ingredient.name)
                }
            }

            // 排除已使用的食谱
            val availableRecipes = filteredRecipes.filter { recipe ->
                recipe.id !in excludeRecipeIds
            }

            // 随机选择一个食谱
            if (availableRecipes.isNotEmpty()) {
                val newRecipe = availableRecipes.random()

                // 替换该餐段的计划
                planRepository.replacePlanForPeriod(
                    babyId = selectedBaby.id,
                    date = today,
                    period = period,
                    newRecipeId = newRecipe.id
                )

                // 重新加载计划
                loadTodayPlans()
            }
        }
    }

    // 更新营养目标
    fun updateNutritionGoal(goal: NutritionGoal) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        android.util.Log.d(TAG, "========== 更新营养目标 ==========")
        android.util.Log.d(TAG, "宝宝 ID: ${selectedBaby.id}")
        android.util.Log.d(TAG, "营养目标: $goal")
        viewModelScope.launch {
            babyRepository.updateNutritionGoal(selectedBaby.id, goal)
            android.util.Log.d(TAG, "✓ 营养目标更新完成 ==========")
        }
    }

    // 生成营养目标推荐（AI 推荐标准值 + 体检数据微调）
    fun generateNutritionGoalRecommendation(): NutritionGoal? {
        val selectedBaby = _uiState.value.selectedBaby ?: return null
        val latestHealthRecord = _uiState.value.latestHealthRecord

        android.util.Log.d(TAG, "========== 开始生成营养目标智能推荐 ==========")
        android.util.Log.d(TAG, "宝宝 ID: ${selectedBaby.id}")
        android.util.Log.d(TAG, "宝宝月龄: ${selectedBaby.ageInMonths} 个月")
        android.util.Log.d(TAG, "当前体重: ${selectedBaby.weight ?: "无数据"} kg")
        android.util.Log.d(TAG, "当前身高: ${selectedBaby.height ?: "无数据"} cm")
        android.util.Log.d(TAG, "最新体检数据: ${latestHealthRecord?.recordDate ?: "无数据"}")

        // 使用最新体检数据进行智能推荐
        val recommended = NutritionGoal.calculateWithHealthData(
            ageInMonths = selectedBaby.ageInMonths,
            currentWeight = selectedBaby.weight,
            currentHeight = selectedBaby.height,
            hemoglobin = latestHealthRecord?.hemoglobin,
            ironLevel = latestHealthRecord?.ironLevel,
            calciumLevel = latestHealthRecord?.calciumLevel
        )

        android.util.Log.d(TAG, "✓ 智能推荐生成完成 ==========")
        return recommended
    }

    // 获取适合的食谱列表（用于选择）
    suspend fun getAvailableRecipes(): List<Recipe> {
        val selectedBaby = _uiState.value.selectedBaby ?: return emptyList()

        // 获取适合该月龄的所有食谱
        val recipes = recipeRepository.getRecipesByAge(selectedBaby.ageInMonths).first()

        // 排除过敏原
        val filteredRecipes = recipes.filter { recipe ->
            recipe.ingredients.none { ingredient ->
                selectedBaby.getEffectiveAllergies().contains(ingredient.name)
            }
        }

        return filteredRecipes
    }

    // 选择食谱并添加到计划
    fun selectRecipeForMealPeriod(recipeId: Long, period: MealPeriod, date: LocalDate) {
        val selectedBaby = _uiState.value.selectedBaby ?: return

        viewModelScope.launch {
            planRepository.replacePlanForPeriod(
                babyId = selectedBaby.id,
                date = date,
                period = period,
                newRecipeId = recipeId
            )

            // 重新加载计划
            loadTodayPlans()
        }
    }

    // 显示食谱选择器
    fun showRecipeSelector(period: MealPeriod, date: LocalDate) {
        viewModelScope.launch {
            val recipes = getAvailableRecipes()
            _uiState.value = _uiState.value.copy(
                showRecipeSelector = true,
                selectedMealPeriod = period,
                availableRecipes = recipes,
                selectedDate = date
            )
        }
    }

    // 隐藏食谱选择器
    fun hideRecipeSelector() {
        _uiState.value = _uiState.value.copy(
            showRecipeSelector = false,
            selectedMealPeriod = null,
            selectedDate = null
        )
    }

    // 更新餐食时间
    fun updateMealTime(period: MealPeriod, newTime: String) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        viewModelScope.launch {
            // 获取当前计划
            val currentPlan = planRepository.getPlansByBabyDateAndPeriod(
                selectedBaby.id,
                today,
                period
            )

            if (currentPlan != null) {
                // 更新计划的用餐时间
                val updatedPlan = currentPlan.copy(mealTime = newTime)
                planRepository.updatePlan(updatedPlan)

                // 重新加载计划
                loadTodayPlans()
            }
        }
    }

    // 显示时间选择器
    fun showMealTimePicker(period: MealPeriod) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        viewModelScope.launch {
            // 获取当前计划的时间
            val currentPlan = planRepository.getPlansByBabyDateAndPeriod(
                _uiState.value.selectedBaby?.id ?: return@launch,
                today,
                period
            )

            val currentTime = currentPlan?.mealTime ?: getMealTime(period)

            _uiState.value = _uiState.value.copy(
                showMealTimePicker = true,
                selectedMealPeriod = period,
                currentMealTime = currentTime
            )
        }
    }

    // 隐藏时间选择器
    fun hideMealTimePicker() {
        _uiState.value = _uiState.value.copy(
            showMealTimePicker = false,
            selectedMealPeriod = null,
            currentMealTime = null
        )
    }

    // 获取餐段默认时间
    private fun getMealTime(period: MealPeriod): String = when (period) {
        MealPeriod.BREAKFAST -> "08:00"
        MealPeriod.LUNCH -> "12:00"
        MealPeriod.DINNER -> "18:00"
        MealPeriod.SNACK -> "15:00"
    }
}

data class HomeUiState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val todayPlans: List<PlanWithRecipe> = emptyList(),
    val weeklyPlans: Map<LocalDate, List<PlanWithRecipe>> = emptyMap(),
    val nutritionGoal: NutritionGoal? = null,
    val isLoading: Boolean = true,
    val showRecipeSelector: Boolean = false,
    val selectedMealPeriod: MealPeriod? = null,
    val availableRecipes: List<Recipe> = emptyList(),
    val selectedDate: LocalDate? = null,
    val latestHealthRecord: com.example.babyfood.domain.model.HealthRecord? = null,
    val showMealTimePicker: Boolean = false,
    val currentMealTime: String? = null
)

data class PlanWithRecipe(
    val plan: Plan,
    val recipe: Recipe?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlanWithRecipe

        if (plan != other.plan) return false
        if (recipe?.id != other.recipe?.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = plan.hashCode()
        result = 31 * result + (recipe?.id?.hashCode() ?: 0)
        return result
    }
}