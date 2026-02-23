package com.example.babyfood.presentation.ui.plans

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.domain.model.ConflictResolution
import com.example.babyfood.domain.model.DailyMealPlan
import com.example.babyfood.domain.model.PlanConflict
import com.example.babyfood.domain.model.PlannedMeal
import com.example.babyfood.domain.model.WeeklyMealPlan
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

/**
 * 推荐结果编辑器
 * @param babyId 宝宝ID
 * @param weeklyPlan 一周饮食计划
 * @param conflicts 冲突列表
 * @param onBack 返回回调
 * @param onSave 保存回调
 * @param viewModel 计划ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationEditorScreen(
    babyId: Long,
    weeklyPlan: WeeklyMealPlan,
    conflicts: List<PlanConflict>,
    onBack: () -> Unit,
    onSave: (ConflictResolution, List<PlannedMeal>) -> Unit,
    viewModel: PlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    var showConflictDialog by remember { mutableStateOf(false) }
    var editedMeals by remember { mutableStateOf<List<PlannedMeal>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.clearError()
    }

    // 保存函数
    val handleSave = {
        scope.launch {
            if (conflicts.isNotEmpty()) {
                showConflictDialog = true
            } else {
                onSave(ConflictResolution.OVERWRITE_ALL, editedMeals)
            }
        }
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Check,
                label = "保存",
                contentDescription = "保存推荐计划",
                enabled = !uiState.isSaving,
                onClick = { handleSave() }
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // 营养摘要卡片
            NutritionSummaryCard(weeklyPlan)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 每日计划列表
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(weeklyPlan.dailyPlans) { dailyPlan ->
                    DailyPlanEditorCard(
                        dailyPlan = dailyPlan,
                        onMealEdit = { meal ->
                            editedMeals = editedMeals.toMutableList().apply {
                                // 替换或添加编辑的餐食
                                val index = indexOfFirst { it.mealPeriod == meal.mealPeriod && it.recipe.id == meal.recipe.id }
                                if (index >= 0) {
                                    this[index] = meal
                                } else {
                                    add(meal)
                                }
                            }
                        }
                    )
                }
            }
            
            // 冲突提示
            if (conflicts.isNotEmpty()) {
                ConflictWarningCard(conflicts.size)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    // 冲突处理对话框
    if (showConflictDialog) {
        ConflictResolutionDialog(
            conflicts = conflicts,
            onDismiss = { showConflictDialog = false },
            onResolve = { resolution ->
                showConflictDialog = false
                onSave(resolution, editedMeals)
            }
        )
    }
}

/**
 * 营养摘要卡片
 */
@Composable
private fun NutritionSummaryCard(weeklyPlan: WeeklyMealPlan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "营养摘要",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "日均热量：${weeklyPlan.nutritionSummary.dailyAverage.calories.toInt()} kcal",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "日均蛋白质：${weeklyPlan.nutritionSummary.dailyAverage.protein.toInt()} g",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "日均钙：${weeklyPlan.nutritionSummary.dailyAverage.calcium.toInt()} mg",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "日均铁：${weeklyPlan.nutritionSummary.dailyAverage.iron.toInt()} mg",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 每日计划编辑器卡片
 */
@Composable
private fun DailyPlanEditorCard(
    dailyPlan: DailyMealPlan,
    onMealEdit: (PlannedMeal) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${dailyPlan.date.monthNumber}月${dailyPlan.date.dayOfMonth}日",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            dailyPlan.meals.forEach { meal ->
                MealItem(
                    meal = meal,
                    onEdit = { onMealEdit(meal) }
                )
            }
        }
    }
}

/**
 * 餐食项
 */
@Composable
private fun MealItem(
    meal: PlannedMeal,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${meal.mealPeriod.displayName} - ${meal.recipe.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = meal.nutritionNotes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onEdit) {
                Text("编辑")
            }
        }
    }
}

/**
 * 冲突警告卡片
 */
@Composable
private fun ConflictWarningCard(conflictCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "警告",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "检测到 $conflictCount 个冲突，保存时请选择处理方式",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}