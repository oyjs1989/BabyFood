package com.example.babyfood.presentation.ui.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            recipeRepository.getAllRecipes().collect { recipes ->
                _uiState.value = _uiState.value.copy(
                    recipes = recipes,
                    filteredRecipes = recipes,
                    isLoading = false
                )
            }
        }
    }

    fun filterByAge(ageMonths: Int) {
        viewModelScope.launch {
            recipeRepository.getRecipesByAge(ageMonths).collect { recipes ->
                _uiState.value = _uiState.value.copy(
                    filteredRecipes = recipes,
                    selectedAge = ageMonths
                )
            }
        }
    }

    fun searchRecipes(query: String) {
        val filtered = if (query.isEmpty()) {
            _uiState.value.recipes
        } else {
            _uiState.value.recipes.filter { recipe ->
                recipe.name.contains(query, ignoreCase = true) ||
                recipe.category.contains(query, ignoreCase = true) ||
                recipe.ingredients.any { it.name.contains(query, ignoreCase = true) }
            }
        }
        _uiState.value = _uiState.value.copy(
            filteredRecipes = filtered,
            searchQuery = query
        )
    }

    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            filteredRecipes = _uiState.value.recipes,
            selectedAge = null,
            selectedCategory = null,
            searchQuery = ""
        )
    }
}

data class RecipesUiState(
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true,
    val selectedAge: Int? = null,
    val selectedCategory: String? = null,
    val searchQuery: String = ""
)