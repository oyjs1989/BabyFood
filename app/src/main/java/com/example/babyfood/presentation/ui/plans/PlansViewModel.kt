package com.example.babyfood.presentation.ui.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.PlanRepository
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class PlansViewModel @Inject constructor(
    private val planRepository: PlanRepository,
    private val babyRepository: BabyRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlansUiState())
    val uiState: StateFlow<PlansUiState> = _uiState.asStateFlow()

    init {
        loadBabies()
        loadRecipes()
    }

    private fun loadBabies() {
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = babies.firstOrNull()
                )
                loadPlans()
            }
        }
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            recipeRepository.getAllRecipes().collect { recipes ->
                _uiState.value = _uiState.value.copy(recipes = recipes)
            }
        }
    }

    private fun loadPlans() {
        val selectedBaby = _uiState.value.selectedBaby
        if (selectedBaby != null) {
            viewModelScope.launch {
                planRepository.getPlansByBaby(selectedBaby.id).collect { plans ->
                    _uiState.value = _uiState.value.copy(
                        plans = plans,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectBaby(baby: Baby) {
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
        viewModelScope.launch {
            try {
                val plan = Plan(
                    babyId = babyId,
                    recipeId = recipeId,
                    plannedDate = plannedDate,
                    mealPeriod = mealPeriod,
                    status = PlanStatus.PLANNED,
                    notes = notes
                )
                planRepository.insertPlan(plan)
                _uiState.value = _uiState.value.copy(
                    isSaved = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun updatePlanStatus(planId: Long, status: PlanStatus) {
        viewModelScope.launch {
            try {
                val plan = _uiState.value.plans.find { it.id == planId }
                if (plan != null) {
                    planRepository.updatePlan(plan.copy(status = status))
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun deletePlan(plan: Plan) {
        viewModelScope.launch {
            try {
                planRepository.deletePlan(plan)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message
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
}

data class PlansUiState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val recipes: List<Recipe> = emptyList(),
    val plans: List<Plan> = emptyList(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val error: String? = null
)