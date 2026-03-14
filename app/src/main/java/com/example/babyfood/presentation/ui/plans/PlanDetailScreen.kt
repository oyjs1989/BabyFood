package com.example.babyfood.presentation.ui.plans

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.Plan
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.AppBottomAction
import androidx.compose.ui.res.stringResource
import com.example.babyfood.R
import com.example.babyfood.presentation.util.DateTimeUtils
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: Long,
    onBack: () -> Unit = {},
    onNavigateToEdit: (Long) -> Unit = {},
    viewModel: PlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var plan by remember { mutableStateOf<Plan?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 加载计划详情
    LaunchedEffect(planId) {
        plan = uiState.plans.find { it.id == planId }
    }

    // 监听计划列表变化
    LaunchedEffect(uiState.plans) {
        plan = uiState.plans.find { it.id == planId }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.plans_delete_title)) },
            text = { Text(stringResource(R.string.plans_delete_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        plan?.let {
                            viewModel.deletePlan(it)
                            showDeleteDialog = false
                            onBack()
                        }
                    }
                ) {
                    Text(stringResource(R.string.common_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    AppScaffold(
        bottomActions = listOf(
            AppBottomAction(
                icon = Icons.Default.Edit,
                label = stringResource(R.string.edit),
                contentDescription = stringResource(R.string.plans_edit_action_desc),
                onClick = { plan?.let { onNavigateToEdit(it.id) } }
            ),
            AppBottomAction(
                icon = Icons.Default.Delete,
                label = stringResource(R.string.common_delete),
                contentDescription = stringResource(R.string.plans_delete_title),
                onClick = { showDeleteDialog = true }
            )
        )
    ) {
        plan?.let { plan ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 基本信息卡片
                InfoCard(title = stringResource(R.string.plans_basic_info_title)) {
                    InfoRow(label = stringResource(R.string.plans_date_label), value = DateTimeUtils.formatDate(plan.plannedDate))
                    InfoRow(label = stringResource(R.string.plans_meal_period_label), value = try { MealPeriod.valueOf(plan.mealPeriod).displayName } catch (e: Exception) { plan.mealPeriod })
                    InfoRow(label = stringResource(R.string.plans_status_label), value = getStatusDisplayName(plan.status))
                    if (!plan.notes.isNullOrBlank()) {
                        InfoRow(label = stringResource(R.string.plans_notes_label), value = plan.notes ?: "")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 食谱信息卡片
                InfoCard(title = stringResource(R.string.plans_recipe_info_title)) {
                    // 这里显示食谱详情，需要从食谱列表中查找
                    val recipe = uiState.recipes.find { it.id == plan.recipeId }
                    if (recipe != null) {
                        InfoRow(label = stringResource(R.string.recipes_name_label), value = recipe.name)
                        InfoRow(label = stringResource(R.string.plans_suitable_age_label), value = "${recipe.minAgeMonths}-${recipe.maxAgeMonths}${stringResource(R.string.baby_months_unit)}")
                        InfoRow(label = stringResource(R.string.recipes_category_label), value = recipe.category)
                        if (recipe.ingredients.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(R.string.recipes_ingredients_label) + "：",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            recipe.ingredients.forEach { ingredient ->
                                Text(
                                    text = "• ${ingredient.name} ${ingredient.amount}",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                    } else {
                        InfoRow(label = stringResource(R.string.plans_select_recipe), value = "ID: ${plan.recipeId}")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 状态操作按钮
                StatusActionButtons(
                    currentStatus = plan.status,
                    onStatusChange = { newStatus ->
                        scope.launch {
                            viewModel.updatePlanStatus(plan.id, newStatus)
                        }
                    }
                )
            }
        } ?: run {
            // 加载中或计划不存在
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.plans_not_found))
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun StatusActionButtons(
    currentStatus: PlanStatus,
    onStatusChange: (PlanStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.plans_status_action_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when (currentStatus) {
                PlanStatus.PLANNED -> {
                    Button(
                        onClick = { onStatusChange(PlanStatus.TRIED) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.plans_mark_tried))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onStatusChange(PlanStatus.SKIPPED) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.plans_mark_skipped))
                    }
                }
                PlanStatus.TRIED -> {
                    Text(
                        text = stringResource(R.string.plans_completed_tried),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                PlanStatus.SKIPPED -> {
                    Text(
                        text = stringResource(R.string.plans_skipped_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun getStatusDisplayName(status: PlanStatus): String {
    return when (status) {
        PlanStatus.PLANNED -> stringResource(R.string.plans_status_planned)
        PlanStatus.TRIED -> stringResource(R.string.plans_status_tried)
        PlanStatus.SKIPPED -> stringResource(R.string.plans_status_skipped)
    }
}