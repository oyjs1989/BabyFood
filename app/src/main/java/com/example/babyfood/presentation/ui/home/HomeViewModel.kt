package com.example.babyfood.presentation.ui.home

import androidx.lifecycle.viewModelScope
import com.example.babyfood.data.preferences.PreferencesManager
import com.example.babyfood.data.repository.BabyRepository
import com.example.babyfood.data.repository.HealthRecordRepository
import com.example.babyfood.data.repository.PlanRepository
import com.example.babyfood.data.repository.RecipeRepository
import com.example.babyfood.domain.model.Baby
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.NutritionGoal
import com.example.babyfood.domain.model.NutritionIntake
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.domain.model.Recipe
import com.example.babyfood.presentation.ui.BaseViewModel
import com.example.babyfood.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val babyRepository: BabyRepository,
    private val planRepository: PlanRepository,
    private val recipeRepository: RecipeRepository,
    private val healthRecordRepository: HealthRecordRepository,
    private val preferencesManager: PreferencesManager
) : BaseViewModel() {

    override val logTag: String = "HomeViewModel"

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        logMethodStart("加载数据")
        viewModelScope.launch {
            babyRepository.getAllBabies().collect { babies ->
                logD("宝宝列表加载完成，共 ${babies.size} 个宝宝")

                val savedBabyId = preferencesManager.getSelectedBabyId()
                logD("保存的宝宝 ID: $savedBabyId")

                val selectedBaby = if (savedBabyId != -1L) {
                    babies.find { it.id == savedBabyId } ?: babies.firstOrNull()
                } else {
                    babies.firstOrNull()
                }

                selectedBaby?.let { logD("选中的宝宝: ${it.name} (ID: ${it.id})") }
                    ?: logD("未选中任何宝宝")

                _uiState.value = _uiState.value.copy(
                    babies = babies,
                    selectedBaby = selectedBaby
                )
                loadTodayPlans()
                logSuccess("数据加载完成")
                logMethodEnd("加载数据")
            }
        }
    }

    private fun loadTodayPlans() {
        val selectedBaby = _uiState.value.selectedBaby ?: run {
            _uiState.value = _uiState.value.copy(isLoading = false)
            return
        }

        val today = DateTimeUtils.today()

        viewModelScope.launch {
            planRepository.getPlansByBabyAndDate(selectedBaby.id, today).collect { plans ->
                val plansWithRecipes = loadPlansWithRecipes(plans)
                val nutritionIntake = calculateNutritionIntake(plansWithRecipes)
                val weeklyPlans = loadWeeklyPlans(selectedBaby.id, today)
                val latestRecord = healthRecordRepository.getLatestHealthRecord(selectedBaby.id)

                _uiState.value = _uiState.value.copy(
                    todayPlans = plansWithRecipes,
                    weeklyPlans = weeklyPlans,
                    nutritionGoal = selectedBaby.getEffectiveNutritionGoal(),
                    nutritionIntake = nutritionIntake,
                    latestHealthRecord = latestRecord,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadPlansWithRecipes(plans: List<Plan>): List<PlanWithRecipe> {
        return plans.map { plan ->
            val recipe = recipeRepository.getById(plan.recipeId)
            PlanWithRecipe(plan, recipe)
        }
    }

    private suspend fun loadWeeklyPlans(babyId: Long, startDate: LocalDate): Map<LocalDate, List<PlanWithRecipe>> {
        return (1..7).associate { dayOffset ->
            val date = startDate.plus(dayOffset, DateTimeUnit.DAY)
            val plans = planRepository.getPlansByBabyAndDate(babyId, date).first()
            val plansWithRecipes = loadPlansWithRecipes(plans)
            date to plansWithRecipes
        }
    }

    /**
     * 计算今日营养摄入
     */
    private fun calculateNutritionIntake(plans: List<PlanWithRecipe>): NutritionIntake {
        logMethodStart("计算营养摄入")

        val filteredPlans = plans
            .filter { it.plan.feedbackStatus != null && it.recipe != null }
            .mapNotNull { planWithRecipe ->
                val ratio = com.example.babyfood.domain.model.FeedbackRatio
                    .getRatio(planWithRecipe.plan.feedbackStatus!!)
                if (ratio > 0f) {
                    logD("餐次: ${planWithRecipe.plan.mealPeriod}, 食谱: ${planWithRecipe.recipe!!.name}, 反馈: ${planWithRecipe.plan.feedbackStatus}, 比例: ${ratio * 100}%")
                    Pair(planWithRecipe.recipe!!.nutrition, ratio)
                } else {
                    null
                }
            }

        val (nutritionTotals, feedbackCount) = filteredPlans.fold(
            Pair(NutritionTotals(), 0)
        ) { (totals, count), (nutrition, ratio) ->
            Pair(
                NutritionTotals(
                    calories = totals.calories + (nutrition.calories ?: 0f) * ratio,
                    protein = totals.protein + (nutrition.protein ?: 0f) * ratio,
                    calcium = totals.calcium + (nutrition.calcium ?: 0f) * ratio,
                    iron = totals.iron + (nutrition.iron ?: 0f) * ratio
                ),
                count + 1
            )
        }

        val intake = NutritionIntake(
            calories = nutritionTotals.calories,
            protein = nutritionTotals.protein,
            calcium = nutritionTotals.calcium,
            iron = nutritionTotals.iron,
            feedbackCount = feedbackCount
        )

        logD("营养摄入计算完成: $intake")
        logMethodEnd("计算营养摄入")
        return intake
    }

    private data class NutritionTotals(
        val calories: Float = 0f,
        val protein: Float = 0f,
        val calcium: Float = 0f,
        val iron: Float = 0f
    )

    fun selectBaby(baby: Baby) {
        logMethodStart("切换宝宝")
        logD("切换到宝宝: ${baby.name} (ID: ${baby.id})")

        _uiState.value = _uiState.value.copy(selectedBaby = baby)
        preferencesManager.saveSelectedBabyId(baby.id)
        loadTodayPlans()

        logSuccess("切换完成")
        logMethodEnd("切换宝宝")
    }

    /**
     * 换一换功能
     */
    fun shuffleMealPeriod(period: MealPeriod) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        val today = DateTimeUtils.today()

        viewModelScope.launch {
            val currentPlan = _uiState.value.todayPlans.find { it.plan.mealPeriod == period.name }
            val excludeRecipeIds = currentPlan?.recipe?.id?.let { listOf(it) } ?: emptyList()

            val recipes = recipeRepository.getRecipesByAge(selectedBaby.ageInMonths).first()
            val filteredRecipes = filterRecipesByAllergies(recipes, selectedBaby)
            val availableRecipes = filteredRecipes.filter { it.id !in excludeRecipeIds }

            if (availableRecipes.isNotEmpty()) {
                val newRecipe = availableRecipes.random()
                planRepository.replacePlanForPeriod(
                    babyId = selectedBaby.id,
                    date = today,
                    period = period,
                    newRecipeId = newRecipe.id
                )
                loadTodayPlans()
                logD("换一换成功: ${period.name} -> ${newRecipe.name}")
            }
        }
    }

    private fun filterRecipesByAllergies(recipes: List<Recipe>, baby: Baby): List<Recipe> {
        val allergies = baby.getEffectiveAllergies()
        return recipes.filter { recipe ->
            recipe.ingredients.none { ingredient ->
                allergies.contains(ingredient.name)
            }
        }
    }

    /**
     * 更新营养目标
     */
    fun updateNutritionGoal(goal: NutritionGoal) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        logMethodStart("更新营养目标")
        logD("宝宝 ID: ${selectedBaby.id}, 营养目标: $goal")

        viewModelScope.launch {
            babyRepository.updateNutritionGoal(selectedBaby.id, goal)
            logSuccess("营养目标更新完成")
            logMethodEnd("更新营养目标")
        }
    }

    /**
     * 生成营养目标智能推荐
     */
    fun generateNutritionGoalRecommendation(): NutritionGoal? {
        val selectedBaby = _uiState.value.selectedBaby ?: return null
        val latestHealthRecord = _uiState.value.latestHealthRecord

        logMethodStart("生成营养目标智能推荐")
        logD("宝宝 ID: ${selectedBaby.id}, 月龄: ${selectedBaby.ageInMonths} 个月")
        logD("当前体重: ${selectedBaby.weight ?: "无数据"} kg, 身高: ${selectedBaby.height ?: "无数据"} cm")
        logD("最新体检数据: ${latestHealthRecord?.recordDate ?: "无数据"}")

        val recommended = NutritionGoal.calculateWithHealthData(
            ageInMonths = selectedBaby.ageInMonths,
            currentWeight = selectedBaby.weight,
            currentHeight = selectedBaby.height,
            hemoglobin = latestHealthRecord?.hemoglobin,
            ironLevel = latestHealthRecord?.ironLevel,
            calciumLevel = latestHealthRecord?.calciumLevel
        )

        logSuccess("智能推荐生成完成: $recommended")
        logMethodEnd("生成营养目标智能推荐")
        return recommended
    }

    /**
     * 获取适合的食谱列表
     */
    suspend fun getAvailableRecipes(): List<Recipe> {
        val selectedBaby = _uiState.value.selectedBaby ?: return emptyList()
        val recipes = recipeRepository.getRecipesByAge(selectedBaby.ageInMonths).first()
        return filterRecipesByAllergies(recipes, selectedBaby)
    }

    /**
     * 选择食谱并添加到计划
     */
    fun selectRecipeForMealPeriod(recipeId: Long, period: MealPeriod, date: LocalDate) {
        val selectedBaby = _uiState.value.selectedBaby ?: return

        viewModelScope.launch {
            planRepository.replacePlanForPeriod(
                babyId = selectedBaby.id,
                date = date,
                period = period,
                newRecipeId = recipeId
            )
            loadTodayPlans()
        }
    }

    /**
     * 显示食谱选择器
     */
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

    /**
     * 隐藏食谱选择器
     */
    fun hideRecipeSelector() {
        _uiState.value = _uiState.value.copy(
            showRecipeSelector = false,
            selectedMealPeriod = null,
            selectedDate = null
        )
    }

    /**
     * 更新餐食时间
     */
    fun updateMealTime(period: MealPeriod, newTime: String) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        val today = DateTimeUtils.today()

        viewModelScope.launch {
            val currentPlan = planRepository.getPlansByBabyDateAndPeriod(
                selectedBaby.id,
                today,
                period
            )

            currentPlan?.let {
                val updatedPlan = it.copy(mealTime = newTime)
                planRepository.update(updatedPlan)
                loadTodayPlans()
            }
        }
    }

    /**
     * 显示时间选择器
     */
    fun showMealTimePicker(period: MealPeriod) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        val today = DateTimeUtils.today()

        viewModelScope.launch {
            val currentPlan = planRepository.getPlansByBabyDateAndPeriod(
                selectedBaby.id,
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

    /**
     * 隐藏时间选择器
     */
    fun hideMealTimePicker() {
        _uiState.value = _uiState.value.copy(
            showMealTimePicker = false,
            selectedMealPeriod = null,
            currentMealTime = null
        )
    }

    /**
     * 显示反馈对话框
     */
    fun showFeedbackDialog(period: MealPeriod) {
        val selectedBaby = _uiState.value.selectedBaby ?: return
        val today = DateTimeUtils.today()

        viewModelScope.launch {
            val currentPlan = planRepository.getPlansByBabyDateAndPeriod(
                selectedBaby.id,
                today,
                period
            )

            currentPlan?.let { plan ->
                val existingFeedback = plan.feedbackStatus?.let { feedbackValue ->
                    com.example.babyfood.domain.model.MealFeedbackOption.fromValue(feedbackValue)
                }
                val recipe = recipeRepository.getById(plan.recipeId)
                val ingredients = recipe?.ingredients ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    showFeedbackDialog = true,
                    selectedMealPeriod = period,
                    selectedFeedback = existingFeedback,
                    selectedPlanId = plan.id,
                    recipeIngredients = ingredients
                )
            }
        }
    }

    /**
     * 选择反馈选项
     */
    fun selectFeedback(option: com.example.babyfood.presentation.ui.home.components.MealFeedbackOption) {
        val domainOption = when (option) {
            com.example.babyfood.presentation.ui.home.components.MealFeedbackOption.FINISHED ->
                com.example.babyfood.domain.model.MealFeedbackOption.FINISHED
            com.example.babyfood.presentation.ui.home.components.MealFeedbackOption.HALF ->
                com.example.babyfood.domain.model.MealFeedbackOption.HALF
            com.example.babyfood.presentation.ui.home.components.MealFeedbackOption.DISLIKED ->
                com.example.babyfood.domain.model.MealFeedbackOption.DISLIKED
            com.example.babyfood.presentation.ui.home.components.MealFeedbackOption.ALLERGY ->
                com.example.babyfood.domain.model.MealFeedbackOption.ALLERGY
        }
        _uiState.value = _uiState.value.copy(selectedFeedback = domainOption)
    }

    /**
     * 提交反馈
     */
    fun submitFeedback() {
        val planId = _uiState.value.selectedPlanId ?: return
        val feedback = _uiState.value.selectedFeedback ?: return

        viewModelScope.launch {
            val currentPlan = planRepository.getById(planId) ?: return@launch

            val updatedPlan = currentPlan.copy(
                feedbackStatus = feedback.value,
                feedbackTime = DateTimeUtils.currentTimestamp().toString(),
                status = PlanStatus.TRIED
            )

            planRepository.update(updatedPlan)
            loadTodayPlans()
            hideFeedbackDialog()
        }
    }

    /**
     * 提交反馈并添加食材到过敏或偏好列表
     */
    fun submitFeedbackWithIngredients(ingredientNames: Set<String>) {
        val planId = _uiState.value.selectedPlanId ?: return
        val feedback = _uiState.value.selectedFeedback ?: return
        val selectedBaby = _uiState.value.selectedBaby ?: return

        viewModelScope.launch {
            val currentPlan = planRepository.getById(planId) ?: return@launch

            val updatedPlan = currentPlan.copy(
                feedbackStatus = feedback.value,
                feedbackTime = DateTimeUtils.currentTimestamp().toString(),
                status = PlanStatus.TRIED
            )

            planRepository.update(updatedPlan)

            // 根据反馈类型添加到过敏或偏好列表
            when (feedback) {
                com.example.babyfood.domain.model.MealFeedbackOption.DISLIKED -> {
                    val newPreferences = ingredientNames.map { ingredientName ->
                        com.example.babyfood.domain.model.PreferenceItem.create(
                            ingredient = ingredientName,
                            expiryDays = 90
                        )
                    }
                    babyRepository.addPreferences(selectedBaby.id, newPreferences)
                }
                com.example.babyfood.domain.model.MealFeedbackOption.ALLERGY -> {
                    val newAllergies = ingredientNames.map { ingredientName ->
                        com.example.babyfood.domain.model.AllergyItem.create(
                            ingredient = ingredientName,
                            expiryDays = null
                        )
                    }
                    babyRepository.addAllergies(selectedBaby.id, newAllergies)
                }
                else -> { /* 其他情况不添加 */ }
            }

            loadTodayPlans()
            hideFeedbackDialog()
        }
    }

    /**
     * 隐藏反馈对话框
     */
    fun hideFeedbackDialog() {
        _uiState.value = _uiState.value.copy(
            showFeedbackDialog = false,
            selectedMealPeriod = null,
            selectedFeedback = null,
            selectedPlanId = null
        )
    }

    /**
     * 获取餐段默认时间
     */
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
    val nutritionIntake: NutritionIntake = NutritionIntake.empty(),
    val isLoading: Boolean = true,
    val showRecipeSelector: Boolean = false,
    val selectedMealPeriod: MealPeriod? = null,
    val availableRecipes: List<Recipe> = emptyList(),
    val selectedDate: LocalDate? = null,
    val latestHealthRecord: com.example.babyfood.domain.model.HealthRecord? = null,
    val showMealTimePicker: Boolean = false,
    val currentMealTime: String? = null,
    val showFeedbackDialog: Boolean = false,
    val selectedFeedback: com.example.babyfood.domain.model.MealFeedbackOption? = null,
    val selectedPlanId: Long? = null,
    val recipeIngredients: List<com.example.babyfood.domain.model.Ingredient> = emptyList()
)

data class PlanWithRecipe(
    val plan: Plan,
    val recipe: Recipe?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PlanWithRecipe
        return plan == other.plan && recipe?.id == other.recipe?.id
    }

    override fun hashCode(): Int {
        var result = plan.hashCode()
        result = 31 * result + (recipe?.id?.hashCode() ?: 0)
        return result
    }
}
