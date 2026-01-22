package com.example.babyfood.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.BabyRepository
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

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val planRepository: PlanRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // 加载宝宝信息
            babyRepository.getAllBabies().collect { babies ->
                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = babies.firstOrNull()
                )
                loadTodayPlans()
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

                    _uiState.value = _uiState.value.copy(
                        todayPlans = plansWithRecipes,
                        weeklyPlans = weeklyPlans,
                        nutritionGoal = selectedBaby.getEffectiveNutritionGoal(),
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
        val weeklyPlans = mutableMapOf<LocalDate, List<PlanWithRecipe>>()

        // 加载未来7天的计划
        for (i in 1..7) {
            val date = startDate.plus(i, kotlinx.datetime.DateTimeUnit.DAY)
            val plans = planRepository.getPlansByBabyAndDate(babyId, date).first()
            val plansWithRecipes = plans.map { plan ->
                val recipe = recipeRepository.getRecipeById(plan.recipeId)
                PlanWithRecipe(plan, recipe)
            }
            weeklyPlans[date] = plansWithRecipes
        }

        return weeklyPlans
    }

    fun selectBaby(baby: Baby) {
        _uiState.value = _uiState.value.copy(selectedBaby = baby)
        loadTodayPlans()
    }

    // 换一换功能
    fun shuffleMealPeriod(period: MealPeriod) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        viewModelScope.launch {
            // 获取当前计划的食谱ID（用于排除）
            val currentPlan = _uiState.value.todayPlans.find { it.plan.mealPeriod == period }
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
        viewModelScope.launch {
            babyRepository.updateNutritionGoal(selectedBaby.id, goal)
        }
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
    val selectedDate: LocalDate? = null
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