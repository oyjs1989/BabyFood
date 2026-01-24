package com.example.babyfood.presentation.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.ai.RecommendationService
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.ComplexityLevel
import com.example.babyfood.domain.model.RecommendationConstraints
import com.example.babyfood.domain.model.RecommendationRequest
import com.example.babyfood.domain.model.RecommendationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recommendationService: RecommendationService,
    private val babyRepository: BabyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    init {
        loadBabies()
    }

    private fun loadBabies() {
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = babies.firstOrNull()
                )
            }
        }
    }

    fun selectBaby(baby: Baby) {
        _uiState.value = _uiState.value.copy(
            selectedBaby = baby,
            ageInMonths = baby.ageInMonths,
            allergies = baby.getEffectiveAllergies(),
            preferences = baby.getEffectivePreferences()
        )
        loadRecommendedConstraints()
    }

    private fun loadRecommendedConstraints() {
        viewModelScope.launch {
            val babyId = _uiState.value.selectedBaby?.id ?: return@launch
            val constraints = recommendationService.getRecommendedConstraints(babyId)
            _uiState.value = _uiState.value.copy(
                constraints = constraints
            )
        }
    }

    fun updateConstraints(constraints: RecommendationConstraints) {
        _uiState.value = _uiState.value.copy(constraints = constraints)
    }

    fun updateAvailableIngredients(ingredients: String) {
        _uiState.value = _uiState.value.copy(
            availableIngredients = ingredients.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        )
    }

    fun updateUseAvailableIngredientsOnly(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            useAvailableIngredientsOnly = enabled
        )
    }

    fun generateRecommendation() {
        val selectedBaby = _uiState.value.selectedBaby ?: return

        // 验证请求
        val request = RecommendationRequest(
            babyId = selectedBaby.id,
            ageInMonths = _uiState.value.ageInMonths,
            allergies = _uiState.value.allergies,
            preferences = _uiState.value.preferences,
            availableIngredients = _uiState.value.availableIngredients,
            useAvailableIngredientsOnly = _uiState.value.useAvailableIngredientsOnly,
            constraints = _uiState.value.constraints,
            startDate = kotlinx.datetime.LocalDate.fromEpochDays((kotlinx.datetime.Clock.System.now().toEpochMilliseconds() / 86400000).toInt())
        )

        android.util.Log.d("AI推荐", "========== 开始生成辅食推荐 ==========")
        android.util.Log.d("AI推荐", "宝宝信息: ID=${selectedBaby.id}, 名称=${selectedBaby.name}, 年龄=${request.ageInMonths}个月")
        android.util.Log.d("AI推荐", "过敏食材: ${request.allergies.joinToString(", ") { it }}")
        android.util.Log.d("AI推荐", "偏好食材: ${request.preferences.joinToString(", ") { it }}")
        android.util.Log.d("AI推荐", "可用食材: ${request.availableIngredients.joinToString(", ") { it }}")
        android.util.Log.d("AI推荐", "仅使用可用食材: ${request.useAvailableIngredientsOnly}")
        android.util.Log.d("AI推荐", "约束条件: 每周最多鱼类${request.constraints.maxFishPerWeek}次, 每周最多蛋类${request.constraints.maxEggPerWeek}次")

        val (isValid, errors) = recommendationService.validateRequest(request)
        if (!isValid) {
            android.util.Log.e("AI推荐", "❌ 请求验证失败: ${errors.joinToString("; ")}")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = errors.firstOrNull()
            )
            return
        }

        android.util.Log.d("AI推荐", "✓ 请求验证通过，开始生成推荐...")
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            android.util.Log.d("AI推荐", "调用 RecommendationService.generateRecommendation()...")
            
            val response = recommendationService.generateRecommendation(request)
            
            val duration = System.currentTimeMillis() - startTime
            android.util.Log.d("AI推荐", "推荐生成完成，耗时: ${duration}ms")
            
            if (response.success) {
                android.util.Log.d("AI推荐", "✓ 推荐生成成功")
                android.util.Log.d("AI推荐", "周计划天数: ${response.weeklyPlan?.dailyPlans?.size ?: 0}")
                response.weeklyPlan?.dailyPlans?.forEachIndexed { index, dailyPlan ->
                    android.util.Log.d("AI推荐", "  第${index + 1}天(${dailyPlan.date}): ${dailyPlan.meals.size}餐")
                }
                android.util.Log.d("AI推荐", "警告信息: ${response.warnings.joinToString("; ")}")
            } else {
                android.util.Log.e("AI推荐", "❌ 推荐生成失败: ${response.errorMessage}")
            }
            
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                response = response,
                error = if (!response.success) response.errorMessage else null
            )
            android.util.Log.d("AI推荐", "========== 辅食推荐流程结束 ==========")
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class RecommendationUiState(
    val babies: List<Baby> = emptyList(),
    val selectedBaby: Baby? = null,
    val ageInMonths: Int = 12,
    val allergies: List<String> = emptyList(),
    val preferences: List<String> = emptyList(),
    val availableIngredients: List<String> = emptyList(),
    val useAvailableIngredientsOnly: Boolean = false,
    val constraints: RecommendationConstraints = RecommendationConstraints(),
    val isLoading: Boolean = false,
    val response: RecommendationResponse? = null,
    val error: String? = null
)
