package com.example.babyfood.presentation.ui.recipes

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.presentation.ui.BaseViewModel
import com.example.babyfood.presentation.ui.clearError
import com.example.babyfood.presentation.ui.clearErrorAndSaved
import com.example.babyfood.presentation.ui.setError
import com.example.babyfood.presentation.ui.setSaved
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : BaseViewModel() {

    override val logTag: String = "RecipesViewModel"

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        logMethodStart("RecipesViewModel 初始化")
        loadRecipes()
    }

    fun loadRecipes() {
        logMethodStart("加载食谱列表")
        viewModelScope.launch {
            recipeRepository.getAllRecipes().collect { recipes ->
                _uiState.value = _uiState.value.copy(
                    recipes = recipes,
                    filteredRecipes = recipes,
                    isLoading = false
                )
                logSuccess("食谱列表加载完成，共 ${recipes.size} 个食谱")
                logMethodEnd("加载食谱列表")
            }
        }
    }

    fun filterByAge(ageMonths: Int) {
        logD("按年龄筛选: $ageMonths 个月")
        viewModelScope.launch {
            recipeRepository.getRecipesByAge(ageMonths).collect { recipes ->
                _uiState.value = _uiState.value.copy(
                    filteredRecipes = recipes,
                    selectedAge = ageMonths
                )
                logD("筛选结果: ${recipes.size} 个食谱")
            }
        }
    }

    fun searchRecipes(query: String) {
        logD("搜索食谱: $query")
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
        logD("搜索结果: ${filtered.size} 个食谱")
    }

    fun clearFilters() {
        logD("清除筛选")
        _uiState.value = _uiState.value.copy(
            filteredRecipes = _uiState.value.recipes,
            selectedAge = null,
            selectedCategory = null,
            searchQuery = ""
        )
    }

    fun filterByCategory(category: String) {
        logD("按分类筛选: $category")
        viewModelScope.launch {
            recipeRepository.getRecipesByCategory(category).collect { recipes ->
                _uiState.value = _uiState.value.copy(
                    filteredRecipes = recipes,
                    selectedCategory = category
                )
                logD("筛选结果: ${recipes.size} 个食谱")
            }
        }
    }

    fun getRecipeById(recipeId: Long): Recipe? {
        var recipe: Recipe? = null
        viewModelScope.launch {
            recipe = recipeRepository.getById(recipeId)
        }
        return recipe
    }

    suspend fun getRecipeByIdAsync(recipeId: Long): Recipe? {
        return recipeRepository.getById(recipeId)
    }

    fun addRecipe(recipe: Recipe) {
        logMethodStart("添加食谱")
        logD("食谱名称: ${recipe.name}")

        safeLaunch("添加食谱") {
            recipeRepository.insert(recipe)
            _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            logSuccess("食谱添加成功")
            logMethodEnd("添加食谱")
        }
    }

    fun updateRecipe(recipe: Recipe) {
        logMethodStart("更新食谱")
        logD("食谱 ID: ${recipe.id}, 名称: ${recipe.name}")

        safeLaunch("更新食谱") {
            recipeRepository.update(recipe)
            _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            logSuccess("食谱更新成功")
            logMethodEnd("更新食谱")
        }
    }

    fun deleteRecipe(recipeId: Long) {
        logMethodStart("删除食谱")
        logD("食谱 ID: $recipeId")

        safeLaunch("删除食谱") {
            recipeRepository.deleteRecipeById(recipeId)
            _uiState.clearErrorAndSaved { error, isSaved -> copy(error = error, isSaved = isSaved) }
            logSuccess("食谱删除成功")
            logMethodEnd("删除食谱")
        }
    }

    fun clearError() {
        _uiState.clearError { error -> copy(error = error) }
    }

    fun clearSavedFlag() {
        _uiState.setSaved(false) { isSaved -> copy(isSaved = isSaved) }
    }
}

data class RecipesUiState(
    val recipes: List<Recipe> = emptyList(),
    val filteredRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = true,
    val selectedAge: Int? = null,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val isSaved: Boolean = false,
    val error: String? = null
)
