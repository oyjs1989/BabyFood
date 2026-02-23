package com.example.babyfood.presentation.ui.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.babyfood.presentation.ui.common.AppScaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(
    onBack: () -> Unit,
    viewModel: RecommendationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 宝宝选择
            item {
                BabySelector(
                    babies = uiState.babies,
                    selectedBaby = uiState.selectedBaby,
                    onBabySelected = { viewModel.selectBaby(it) }
                )
            }

            // 可用食材输入
            item {
                AvailableIngredientsInput(
                    ingredients = uiState.availableIngredients.joinToString(", "),
                    onIngredientsChange = { viewModel.updateAvailableIngredients(it) },
                    useAvailableIngredientsOnly = uiState.useAvailableIngredientsOnly,
                    onUseAvailableIngredientsOnlyChange = { viewModel.updateUseAvailableIngredientsOnly(it) }
                )
            }

            // 约束条件
            item {
                ConstraintsCard(
                    constraints = uiState.constraints,
                    onConstraintsChange = { viewModel.updateConstraints(it) }
                )
            }

            // 生成按钮
            item {
                Button(
                    onClick = { viewModel.generateRecommendation() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.selectedBaby != null && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (uiState.isLoading) "生成中..." else "生成推荐")
                }
            }

            // 错误提示
            if (uiState.error != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.error!!,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // 推荐结果
            if (uiState.response?.success == true && uiState.response?.weeklyPlan != null) {
                item {
                    RecommendationResult(
                        weeklyPlan = uiState.response!!.weeklyPlan!!,
                        warnings = uiState.response!!.warnings
                    )
                }
            }
        }
    }
}

@Composable
private fun BabySelector(
    babies: List<com.example.babyfood.domain.model.Baby>,
    selectedBaby: com.example.babyfood.domain.model.Baby?,
    onBabySelected: (com.example.babyfood.domain.model.Baby) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "选择宝宝",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedBaby?.name ?: "请选择宝宝",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    babies.forEach { baby ->
                        DropdownMenuItem(
                            text = { Text("${baby.name} (${baby.ageInMonths}个月)") },
                            onClick = {
                                onBabySelected(baby)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailableIngredientsInput(
    ingredients: String,
    onIngredientsChange: (String) -> Unit,
    useAvailableIngredientsOnly: Boolean = false,
    onUseAvailableIngredientsOnlyChange: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "可用食材（可选）",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ingredients,
                onValueChange = onIngredientsChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("例如：胡萝卜, 鸡肉, 菠菜") },
                minLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Switch开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "只使用当前食材",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                    Text(
                        text = if (useAvailableIngredientsOnly) {
                            "只推荐包含以上食材的食谱"
                        } else {
                            "优先推荐包含以上食材的食谱，也可推荐其他食谱"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Switch(
                    checked = useAvailableIngredientsOnly,
                    onCheckedChange = onUseAvailableIngredientsOnlyChange
                )
            }
        }
    }
}

@Composable
private fun ConstraintsCard(
    constraints: com.example.babyfood.domain.model.RecommendationConstraints,
    onConstraintsChange: (com.example.babyfood.domain.model.RecommendationConstraints) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "推荐约束",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            ConstraintItem(
                label = "每周最多鱼类",
                value = "${constraints.maxFishPerWeek}次"
            )
            ConstraintItem(
                label = "每周最多蛋类",
                value = "${constraints.maxEggPerWeek}次"
            )
            ConstraintItem(
                label = "早餐复杂度",
                value = when (constraints.breakfastComplexity) {
                    com.example.babyfood.domain.model.ComplexityLevel.SIMPLE -> "简单"
                    com.example.babyfood.domain.model.ComplexityLevel.MODERATE -> "中等"
                    com.example.babyfood.domain.model.ComplexityLevel.COMPLEX -> "复杂"
                }
            )
            ConstraintItem(
                label = "每日最多餐数",
                value = "${constraints.maxDailyMeals}餐"
            )
        }
    }
}

@Composable
private fun ConstraintItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RecommendationResult(
    weeklyPlan: com.example.babyfood.domain.model.WeeklyMealPlan,
    warnings: List<String>
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "推荐结果",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 营养摘要
            NutritionSummaryCard(weeklyPlan.nutritionSummary)

            Spacer(modifier = Modifier.height(16.dp))

            // 警告信息
            if (warnings.isNotEmpty()) {
                warnings.forEach { warning ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = "⚠️ $warning",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // 每日计划
            weeklyPlan.dailyPlans.forEach { dailyPlan ->
                DailyPlanCard(dailyPlan)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(
    summary: com.example.babyfood.domain.model.NutritionSummary
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "营养摘要",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "每日平均：${summary.dailyAverage.calories.toInt()} kcal热量，" +
                        "${summary.dailyAverage.protein.toInt()}g蛋白质，" +
                        "${summary.dailyAverage.calcium.toInt()}mg钙，" +
                        "${summary.dailyAverage.iron.toInt()}mg铁",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (summary.highlights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                summary.highlights.forEach { highlight ->
                    Text(
                        text = "✓ $highlight",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyPlanCard(dailyPlan: com.example.babyfood.domain.model.DailyMealPlan) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dailyPlan.date.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            dailyPlan.meals.forEach { meal ->
                MealItem(meal)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MealItem(meal: com.example.babyfood.domain.model.PlannedMeal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${meal.mealPeriod.displayName} - ${meal.recipe.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "👶 ${meal.childFriendlyText}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "📋 ${meal.nutritionNotes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

private val com.example.babyfood.domain.model.MealPeriod.displayName: String
    get() = when (this) {
        com.example.babyfood.domain.model.MealPeriod.BREAKFAST -> "早餐"
        com.example.babyfood.domain.model.MealPeriod.LUNCH -> "午餐"
        com.example.babyfood.domain.model.MealPeriod.DINNER -> "晚餐"
        com.example.babyfood.domain.model.MealPeriod.SNACK -> "点心"
    }