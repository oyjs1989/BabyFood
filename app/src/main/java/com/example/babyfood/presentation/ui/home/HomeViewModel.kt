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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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

                    _uiState.value = _uiState.value.copy(
                        todayPlans = plansWithRecipes,
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
}

data class HomeUiState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val todayPlans: List<PlanWithRecipe> = emptyList(),
    val nutritionGoal: NutritionGoal? = null,
    val isLoading: Boolean = true
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