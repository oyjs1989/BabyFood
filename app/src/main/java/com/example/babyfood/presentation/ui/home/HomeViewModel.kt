package com.example.babyfood.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.PlanRepository
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                loadPlans()
                loadRecommendedRecipes()
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

    private fun loadRecommendedRecipes() {
        val selectedBaby = _uiState.value.selectedBaby
        if (selectedBaby != null) {
            viewModelScope.launch {
                recipeRepository.getRecipesByAge(selectedBaby.ageInMonths).collect { recipes ->
                    _uiState.value = _uiState.value.copy(
                        recommendedRecipes = recipes.take(3)
                    )
                }
            }
        }
    }

    fun selectBaby(baby: Baby) {
        _uiState.value = _uiState.value.copy(selectedBaby = baby)
        loadPlans()
        loadRecommendedRecipes()
    }
}

data class HomeUiState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val plans: List<Plan> = emptyList(),
    val recommendedRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true
)