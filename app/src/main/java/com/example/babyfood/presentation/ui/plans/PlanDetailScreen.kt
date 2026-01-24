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
            title = { Text("删除计划") },
            text = { Text("确定要删除这个辅食计划吗？此操作无法撤销。") },
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
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("计划详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { plan?.let { onNavigateToEdit(it.id) } }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "删除")
                    }
                }
            )
        }
    ) { paddingValues ->
        plan?.let { plan ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // 基本信息卡片
                InfoCard(title = "基本信息") {
                    InfoRow(label = "日期", value = "${plan.plannedDate.year}年${plan.plannedDate.monthNumber}月${plan.plannedDate.dayOfMonth}日")
                    InfoRow(label = "餐段", value = try { MealPeriod.valueOf(plan.mealPeriod).displayName } catch (e: Exception) { plan.mealPeriod })
                    InfoRow(label = "状态", value = getStatusDisplayName(plan.status))
                    if (!plan.notes.isNullOrBlank()) {
                        InfoRow(label = "备注", value = plan.notes ?: "")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 食谱信息卡片
                InfoCard(title = "食谱信息") {
                    // 这里显示食谱详情，需要从食谱列表中查找
                    val recipe = uiState.recipes.find { it.id == plan.recipeId }
                    if (recipe != null) {
                        InfoRow(label = "食谱名称", value = recipe.name)
                        InfoRow(label = "适合月龄", value = "${recipe.minAgeMonths}-${recipe.maxAgeMonths}个月")
                        InfoRow(label = "分类", value = recipe.category)
                        if (recipe.ingredients.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "食材：",
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
                        InfoRow(label = "食谱", value = "食谱ID: ${plan.recipeId}")
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
                    Text("计划不存在或已删除")
                }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "状态操作",
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
                        Text("标记为已尝试")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onStatusChange(PlanStatus.SKIPPED) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("标记为已跳过")
                    }
                }
                PlanStatus.TRIED -> {
                    Text(
                        text = "✓ 已完成尝试",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                PlanStatus.SKIPPED -> {
                    Text(
                        text = "✗ 已跳过",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun getStatusDisplayName(status: PlanStatus): String {
    return when (status) {
        PlanStatus.PLANNED -> "已计划"
        PlanStatus.TRIED -> "已尝试"
        PlanStatus.SKIPPED -> "已跳过"
    }
}