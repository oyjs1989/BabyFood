package com.example.babyfood.presentation.ui.plans

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.PlanStatus
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanFormScreen(
    babyId: Long,
    planId: Long? = null,
    selectedDate: kotlinx.datetime.LocalDate? = null,
    onBack: () -> Unit = {},
    onSave: () -> Unit = {},
    onNavigateToRecommendationEditor: (Long) -> Unit = {},
    viewModel: PlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // 表单状态
    var plannedDate by remember { 
        mutableStateOf(selectedDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
    }
    var selectedMealPeriod by remember { mutableStateOf(MealPeriod.BREAKFAST) }
    var selectedRecipeId by remember { mutableStateOf<Long?>(null) }
    var selectedStatus by remember { mutableStateOf(PlanStatus.PLANNED) }
    var notes by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // 餐段选项
    val mealPeriods = listOf(
        MealPeriod.BREAKFAST,
        MealPeriod.LUNCH,
        MealPeriod.DINNER,
        MealPeriod.SNACK
    )

    // 状态选项
    val statuses = listOf(
        PlanStatus.PLANNED to "已计划",
        PlanStatus.TRIED to "已尝试",
        PlanStatus.SKIPPED to "已跳过"
    )

    // 加载现有计划数据（编辑模式）
    LaunchedEffect(planId) {
        if (planId != null && planId > 0) {
            val plan = uiState.plans.find { it.id == planId }
            if (plan != null) {
                plannedDate = plan.plannedDate
                selectedMealPeriod = try {
                    MealPeriod.valueOf(plan.mealPeriod)
                } catch (e: Exception) {
                    MealPeriod.BREAKFAST  // 默认值
                }
                selectedRecipeId = plan.recipeId
                selectedStatus = plan.status
                notes = plan.notes ?: ""
            }
        }
    }

    // 错误对话框
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("错误") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("确定")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (planId != null && planId > 0) "编辑计划" else "添加计划") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions = {
                    // AI推荐按钮（仅在创建模式显示）
                    if (planId == null || planId == 0L) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val recommendation = viewModel.generateDailyRecommendation(babyId, plannedDate)
                                    if (recommendation != null) {
                                        onNavigateToRecommendationEditor(babyId)
                                    }
                                }
                            },
                            enabled = !uiState.isGenerating
                        ) {
                            if (uiState.isGenerating) {
                                androidx.compose.material3.CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    androidx.compose.material.icons.Icons.Default.AutoAwesome,
                                    contentDescription = "AI推荐"
                                )
                            }
                        }
                    }
                    
                    // 保存按钮
                    IconButton(
                        onClick = {
                            if (selectedRecipeId == null) {
                                errorMessage = "请选择食谱"
                                showError = true
                                return@IconButton
                            }

                            scope.launch {
                                try {
                                    if (planId != null && planId > 0) {
                                        // 编辑模式
                                        val existingPlan = uiState.plans.find { it.id == planId }
                                        if (existingPlan != null) {
                                            val updatedPlan = existingPlan.copy(
                                                recipeId = selectedRecipeId!!,
                                                plannedDate = plannedDate,
                                                mealPeriod = selectedMealPeriod.name,
                                                status = selectedStatus,
                                                notes = notes.ifBlank { null }
                                            )
                                            viewModel.updatePlan(updatedPlan)
                                        }
                                    } else {
                                        // 创建模式
                                        viewModel.createPlan(
                                            babyId = babyId,
                                            recipeId = selectedRecipeId!!,
                                            plannedDate = plannedDate,
                                            mealPeriod = selectedMealPeriod,
                                            notes = notes.ifBlank { null }
                                        )
                                    }
                                    onSave()
                                } catch (e: Exception) {
                                    errorMessage = e.message ?: "保存失败"
                                    showError = true
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 日期选择
            DatePicker(
                label = "日期",
                date = plannedDate,
                onDateChange = { plannedDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 餐段选择
            DropdownSelector(
                label = "餐段",
                selectedValue = selectedMealPeriod,
                options = mealPeriods,
                displayMapper = { it.displayName },
                onValueChange = { selectedMealPeriod = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 食谱选择
            RecipeSelector(
                recipes = uiState.recipes,
                selectedRecipeId = selectedRecipeId,
                onRecipeSelected = { selectedRecipeId = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 状态选择
            DropdownSelector(
                label = "状态",
                selectedValue = selectedStatus,
                options = statuses.map { it.first },
                displayMapper = { status -> statuses.find { it.first == status }?.second ?: "" },
                onValueChange = { selectedStatus = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 备注
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("备注（可选）") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}

@Composable
private fun DatePicker(
    label: String,
    date: kotlinx.datetime.LocalDate,
    onDateChange: (kotlinx.datetime.LocalDate) -> Unit
) {
    // 简化版日期显示（实际项目中可以使用日期选择器对话框）
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${date.year}年${date.monthNumber}月${date.dayOfMonth}日",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun <T> DropdownSelector(
    label: String,
    selectedValue: T,
    options: List<T>,
    displayMapper: (T) -> String,
    onValueChange: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = displayMapper(selectedValue),
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(displayMapper(option)) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun RecipeSelector(
    recipes: List<com.example.babyfood.domain.model.Recipe>,
    selectedRecipeId: Long?,
    onRecipeSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedRecipe = recipes.find { it.id == selectedRecipeId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedRecipe?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("食谱") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            isError = selectedRecipeId == null
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (recipes.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("暂无食谱，请先添加食谱") },
                    onClick = { },
                    enabled = false
                )
            } else {
                recipes.forEach { recipe ->
                    DropdownMenuItem(
                        text = { 
                            Column {
                                Text(recipe.name)
                                Text(
                                    text = "${recipe.minAgeMonths}-${recipe.maxAgeMonths}个月 | ${recipe.category}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        onClick = {
                            onRecipeSelected(recipe.id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}