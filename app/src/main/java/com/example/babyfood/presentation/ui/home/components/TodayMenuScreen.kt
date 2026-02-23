package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.presentation.ui.home.HomeViewModel
import com.example.babyfood.presentation.ui.common.AppScaffold
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

@Composable
fun TodayMenuScreen(
    onViewRecipeDetail: (Long) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val dateString = today.date.toChineseDateString()

    // 控制未来一周计划的展开/折叠状态
    var isWeeklyExpanded by remember { mutableStateOf(false) }

    // 控制营养目标编辑对话框的显示
    var showNutritionGoalEdit by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    } else if (uiState.selectedBaby == null) {
        // 空状态：没有宝宝
        com.example.babyfood.presentation.theme.EmptyState(
            icon = Icons.Default.ChildCare,
            title = "还没有添加宝宝信息",
            description = "请先在「宝宝信息」页面添加宝宝",
            modifier = Modifier.fillMaxSize()
        )
    } else {
        androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 营养目标卡片
                uiState.nutritionGoal?.let { goal ->
                    NutritionGoalCard(
                        nutritionGoal = goal,
                        nutritionIntake = uiState.nutritionIntake,
                        onEdit = { showNutritionGoalEdit = true }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 餐单时间轴
                Text(
                    text = "今日餐单",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                MealTimeline(
                    plans = uiState.todayPlans,
                    onShuffle = { period -> viewModel.shuffleMealPeriod(period) },
                    onSelectRecipe = { period ->
                        viewModel.showRecipeSelector(period, kotlinx.datetime.LocalDate(today.year, today.monthNumber, today.dayOfMonth))
                    },
                    onViewRecipeDetail = onViewRecipeDetail,
                    onEditMealTime = { period, currentTime ->
                        viewModel.showMealTimePicker(period)
                    },
                    onFeedback = { period -> viewModel.showFeedbackDialog(period) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 未来一周计划
                WeeklyPlansSection(
                    weeklyPlans = uiState.weeklyPlans,
                    isExpanded = isWeeklyExpanded,
                    onToggleExpand = { isWeeklyExpanded = !isWeeklyExpanded }
                )
            }
    }
    // 食谱选择对话框
    if (uiState.showRecipeSelector) {
        RecipeSelectorDialog(
            availableRecipes = uiState.availableRecipes,
            onDismiss = { viewModel.hideRecipeSelector() },
            onRecipeSelected = { recipeId ->
                viewModel.selectRecipeForMealPeriod(
                    recipeId = recipeId,
                    period = uiState.selectedMealPeriod ?: com.example.babyfood.domain.model.MealPeriod.BREAKFAST,
                    date = kotlinx.datetime.LocalDate(today.year, today.monthNumber, today.dayOfMonth)
                )
                viewModel.hideRecipeSelector()
            }
        )
    }

    // 时间选择器对话框
    if (uiState.showMealTimePicker && uiState.currentMealTime != null) {
        MealTimePickerDialog(
            initialTime = uiState.currentMealTime!!,
            onDismiss = { viewModel.hideMealTimePicker() },
            onConfirm = { newTime ->
                viewModel.updateMealTime(
                    uiState.selectedMealPeriod ?: com.example.babyfood.domain.model.MealPeriod.BREAKFAST,
                    newTime
                )
                viewModel.hideMealTimePicker()
            }
        )
    }

    // 营养目标编辑对话框
    if (showNutritionGoalEdit) {
        val goal = uiState.nutritionGoal
        val baby = uiState.selectedBaby
        if (goal != null && baby != null) {
            NutritionGoalEditDialog(
                currentGoal = goal,
                ageInMonths = baby.ageInMonths,
                onDismiss = { showNutritionGoalEdit = false },
                onSave = { newGoal ->
                    viewModel.updateNutritionGoal(newGoal)
                    showNutritionGoalEdit = false
                },
                onRecommend = {
                    viewModel.generateNutritionGoalRecommendation()
                }
            )
        }
    }

    // 反馈对话框
    if (uiState.showFeedbackDialog) {
        // 将领域模型的反馈选项转换为 UI 枚举
        val uiFeedbackOption = uiState.selectedFeedback?.let { domainOption ->
            when (domainOption) {
                com.example.babyfood.domain.model.MealFeedbackOption.FINISHED ->
                    MealFeedbackOption.FINISHED
                com.example.babyfood.domain.model.MealFeedbackOption.HALF ->
                    MealFeedbackOption.HALF
                com.example.babyfood.domain.model.MealFeedbackOption.DISLIKED ->
                    MealFeedbackOption.DISLIKED
                com.example.babyfood.domain.model.MealFeedbackOption.ALLERGY ->
                    MealFeedbackOption.ALLERGY
            }
        }

        MealFeedbackDialog(
            selectedFeedback = uiFeedbackOption,
            onDismiss = { viewModel.hideFeedbackDialog() },
            onFeedbackSelected = { uiOption ->
                viewModel.selectFeedback(uiOption)
            },
            onConfirm = { viewModel.submitFeedback() },
            onConfirmWithIngredients = { ingredients ->
                viewModel.submitFeedbackWithIngredients(ingredients)
            },
            recipeIngredients = uiState.recipeIngredients
        )
    }
}

/**
 * Extension function to format LocalDate to Chinese date string (e.g., "1月27日")
 */
private fun kotlinx.datetime.LocalDate.toChineseDateString(): String {
    return "${this.monthNumber}月${this.dayOfMonth}日"
}