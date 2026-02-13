package com.example.babyfood.presentation.ui.ai

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.ai.RecommendationService
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.ComplexityLevel
import com.example.babyfood.domain.model.RecommendationConstraints
import com.example.babyfood.domain.model.RecommendationRequest
import com.example.babyfood.domain.model.RecommendationResponse
import com.example.babyfood.presentation.ui.BaseViewModel
import com.example.babyfood.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val recommendationService: RecommendationService,
    private val babyRepository: BabyRepository
) : BaseViewModel() {

    override val logTag: String = "RecommendationViewModel"

    private val _uiState = MutableStateFlow(RecommendationUiState())
    val uiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()

    init {
        logMethodStart("RecommendationViewModel 初始化")
        loadBabies()
    }

    private fun loadBabies() {
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = babies.firstOrNull()
                )
                logD("宝宝列表加载完成，共 ${babies.size} 个宝宝")
            }
        }
    }

    fun selectBaby(baby: Baby) {
        logD("选择宝宝: ${baby.name} (ID: ${baby.id})")
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
            _uiState.value = _uiState.value.copy(constraints = constraints)
            logD("加载推荐约束: $constraints")
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
        val selectedBaby = _uiState.value.selectedBaby ?: run {
            logError("未选择宝宝")
            return
        }

        // 构建请求
        val request = buildRecommendationRequest(selectedBaby)

        logMethodStart("生成辅食推荐")
        logRequestDetails(request, selectedBaby)

        // 验证请求
        val (isValid, errors) = recommendationService.validateRequest(request)
        if (!isValid) {
            logError("请求验证失败: ${errors.joinToString("; ")}")
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = errors.firstOrNull()
            )
            return
        }

        logSuccess("请求验证通过，开始生成推荐...")
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            executeRecommendation(request)
        }
    }

    /**
     * 构建推荐请求
     */
    private fun buildRecommendationRequest(baby: Baby): RecommendationRequest {
        return RecommendationRequest(
            babyId = baby.id,
            ageInMonths = _uiState.value.ageInMonths,
            allergies = _uiState.value.allergies,
            preferences = _uiState.value.preferences,
            availableIngredients = _uiState.value.availableIngredients,
            useAvailableIngredientsOnly = _uiState.value.useAvailableIngredientsOnly,
            constraints = _uiState.value.constraints,
            startDate = DateTimeUtils.today()
        )
    }

    /**
     * 记录请求详情
     */
    private fun logRequestDetails(request: RecommendationRequest, baby: Baby) {
        logD("宝宝信息: ID=${baby.id}, 名称=${baby.name}, 年龄=${request.ageInMonths}个月")
        logD("过敏食材: ${request.allergies.joinToString(", ")}")
        logD("偏好食材: ${request.preferences.joinToString(", ")}")
        logD("可用食材: ${request.availableIngredients.joinToString(", ")}")
        logD("仅使用可用食材: ${request.useAvailableIngredientsOnly}")
        logD("约束条件: 每周最多鱼类${request.constraints.maxFishPerWeek}次, 每周最多蛋类${request.constraints.maxEggPerWeek}次")
    }

    /**
     * 执行推荐请求
     */
    private suspend fun executeRecommendation(request: RecommendationRequest) {
        val startTime = System.currentTimeMillis()
        logD("调用 RecommendationService.generateRecommendation()...")

        val response = recommendationService.generateRecommendation(request)

        val duration = System.currentTimeMillis() - startTime
        logD("推荐生成完成，耗时: ${duration}ms")

        handleRecommendationResponse(response)
        logMethodEnd("生成辅食推荐")
    }

    /**
     * 处理推荐响应
     */
    private fun handleRecommendationResponse(response: RecommendationResponse) {
        if (response.success) {
            logSuccess("推荐生成成功")
            logD("周计划天数: ${response.weeklyPlan?.dailyPlans?.size ?: 0}")
            response.weeklyPlan?.dailyPlans?.forEachIndexed { index, dailyPlan ->
                logD("  第${index + 1}天(${dailyPlan.date}): ${dailyPlan.meals.size}餐")
            }
            if (response.warnings.isNotEmpty()) {
                logWarning("警告信息: ${response.warnings.joinToString("; ")}")
            }
        } else {
            logError("推荐生成失败: ${response.errorMessage}")
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            response = response,
            error = if (!response.success) response.errorMessage else null
        )
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