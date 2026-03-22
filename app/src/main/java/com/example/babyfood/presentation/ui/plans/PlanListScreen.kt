package com.example.babyfood.presentation.ui.plans

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import com.example.babyfood.presentation.ui.home.components.RecipeSelectorDialog
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.res.stringResource
import com.example.babyfood.R
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.PlanStatus
import com.example.babyfood.presentation.ui.common.AppScaffold
import com.example.babyfood.presentation.ui.common.ExpandableFab
import com.example.babyfood.presentation.ui.common.FabAction
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.util.Locale
import android.widget.Toast

import com.example.babyfood.presentation.util.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanListScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToAdd: (Long) -> Unit = {},
    onNavigateToRecommendationEditor: (Long) -> Unit = {},
    onNavigateToPoints: () -> Unit = {},
    viewModel: PlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var currentMonth by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    AppScaffold(
        floatingActionButton = {
            ExpandableFab(
                actions = listOf(
                    FabAction(
                        icon = Icons.Default.Edit,
                        label = stringResource(R.string.plans_manual_add),
                        onClick = {
                            val baby = uiState.selectedBaby ?: uiState.babies.firstOrNull()
                            if (baby != null) {
                                android.util.Log.d("PlanListScreen", "导航至添加计划，宝宝ID: ${baby.id}")
                                onNavigateToAdd(baby.id)
                            } else {
                                android.util.Log.e("PlanListScreen", "无法添加计划：未找到宝宝信息")
                            }
                        }
                    ),
                    FabAction(
                        icon = Icons.Default.AutoAwesome,
                        label = stringResource(R.string.plans_ai_recommend),
                        onClick = {
                            android.util.Log.d("PlanListScreen", "显示日期范围选择器")
                            showDateRangePicker = true
                        }
                    )
                )
            )
        }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 宝宝选择器
            if (uiState.babies.size > 1) {
                BabySelector(
                    babies = uiState.babies,
                    selectedBaby = uiState.selectedBaby,
                    onBabySelected = { viewModel.selectBaby(it) }
                )
            }

            // 日历视图
            CalendarView(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                plans = uiState.plans,
                onPreviousMonth = {
                    currentMonth = currentMonth.minus(1, DateTimeUnit.MONTH)
                },
                onNextMonth = {
                    currentMonth = currentMonth.plus(1, DateTimeUnit.MONTH)
                },
                onDateSelected = { date ->
                    selectedDate = date
                }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // 选中日期和计划列表
            SelectedDatePlans(
                selectedDate = selectedDate,
                plansWithRecipe = uiState.plansWithRecipe,
                onPlanClick = onNavigateToDetail,
                onChangeRecipe = { planId -> viewModel.showRecipeSelector(planId) }
            )
        }
    }

    // 日期范围选择器对话框
    if (showDateRangePicker) {
        DateRangePickerDialog(
            onDismiss = { showDateRangePicker = false },
            onConfirm = { startDate, endDate ->
                showDateRangePicker = false
                val days = (endDate.toEpochDays() - startDate.toEpochDays() + 1).toInt()
                uiState.selectedBaby?.let { baby ->
                    scope.launch {
                        val recommendation = viewModel.generateWeeklyRecommendation(baby.id, startDate, days)
                        if (recommendation != null) {
                            onNavigateToRecommendationEditor(baby.id)
                        }
                    }
                }
            }
        )
    }
    
    // AI推荐加载对话框
    if (uiState.isGenerating) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(stringResource(R.string.plans_ai_analyzing)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.plans_ai_generating),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = { }
        )
    }
    
    // 错误对话框
    if (uiState.error != null) {
        val shouldShowPointsAction = uiState.error?.contains("积分", ignoreCase = false) == true
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text(stringResource(R.string.common_error)) },
            text = { Text(uiState.error ?: "") },
            dismissButton = {
                if (shouldShowPointsAction) {
                    TextButton(
                        onClick = {
                            viewModel.clearError()
                            onNavigateToPoints()
                        }
                    ) {
                        Text("去积分页")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
    }

    // 食谱选择对话框
    if (uiState.showRecipeSelector && uiState.selectedPlanId != null) {
        RecipeSelectorDialog(
            availableRecipes = uiState.recipes,
            onDismiss = { viewModel.dismissRecipeSelector() },
            onRecipeSelected = { newRecipeId ->
                viewModel.updatePlanRecipe(uiState.selectedPlanId!!, newRecipeId)
                viewModel.dismissRecipeSelector()
            }
        )
    }
}

@Composable
private fun BabySelector(
    babies: List<com.example.babyfood.domain.model.Baby>,
    selectedBaby: com.example.babyfood.domain.model.Baby?,
    onBabySelected: (com.example.babyfood.domain.model.Baby) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = selectedBaby?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.plans_select_baby)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            babies.forEach { baby ->
                DropdownMenuItem(
                    text = { Text(baby.name) },
                    onClick = {
                        onBabySelected(baby)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CalendarView(
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    plans: List<com.example.babyfood.domain.model.Plan>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 月份导航
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }

            Text(
                text = DateTimeUtils.formatYearMonth(currentMonth),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf(
                stringResource(R.string.day_sun),
                stringResource(R.string.day_mon),
                stringResource(R.string.day_tue),
                stringResource(R.string.day_wed),
                stringResource(R.string.day_thu),
                stringResource(R.string.day_fri),
                stringResource(R.string.day_sat)
            ).forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 日期网格
        val firstDayOfMonth = LocalDate(currentMonth.year, currentMonth.monthNumber, 1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val daysInMonth = getDaysInMonth(currentMonth.year, currentMonth.monthNumber)
        
        Column {
            var dayCounter = 1
            for (week in 0..5) {
                if (dayCounter > daysInMonth) break
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (dayOfWeek in 0..6) {
                        val date = if (week == 0 && dayOfWeek < firstDayOfWeek) {
                            null
                        } else if (dayCounter > daysInMonth) {
                            null
                        } else {
                            val date = LocalDate(currentMonth.year, currentMonth.monthNumber, dayCounter)
                            dayCounter++
                            date
                        }

                        DateCell(
                            date = date,
                            isSelected = date == selectedDate,
                            hasPlans = date?.let { d -> 
                                plans.any { it.plannedDate == d }
                            } ?: false,
                            onDateSelected = onDateSelected,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateCell(
    date: LocalDate?,
    isSelected: Boolean,
    hasPlans: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    date != null -> MaterialTheme.colorScheme.surface
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = date != null) {
                date?.let { onDateSelected(it) }
            },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
                if (hasPlans) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedDatePlans(
    selectedDate: LocalDate,
    plansWithRecipe: List<com.example.babyfood.presentation.ui.plans.PlanWithRecipe>,
    onPlanClick: (Long) -> Unit,
    onChangeRecipe: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = DateTimeUtils.formatDate(selectedDate),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val filteredPlans = plansWithRecipe.filter { it.plan.plannedDate == selectedDate }
        if (filteredPlans.isEmpty()) {
                com.example.babyfood.presentation.theme.EmptyState(
                    icon = Icons.Default.CalendarMonth,
                    title = stringResource(R.string.plans_empty_list),
                    description = stringResource(R.string.plans_empty_description)
                )
            } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredPlans.sortedBy { try { MealPeriod.valueOf(it.plan.mealPeriod).order } catch (e: Exception) { 0 } }) { planWithRecipe ->
                    PlanItem(
                        planWithRecipe = planWithRecipe,
                        onClick = { onPlanClick(planWithRecipe.plan.id) },
                        onChangeRecipe = { onChangeRecipe(planWithRecipe.plan.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanItem(
    planWithRecipe: com.example.babyfood.presentation.ui.plans.PlanWithRecipe,
    onClick: () -> Unit,
    onChangeRecipe: () -> Unit
) {
    val plan = planWithRecipe.plan
    val recipe = planWithRecipe.recipe
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onChangeRecipe),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outline
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图片显示在左侧
            if (recipe?.imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(recipe.imageUrl),
                    contentDescription = recipe.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe?.name ?: "未知食谱 (ID: ${plan.recipeId})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (recipe != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "食材: ${recipe.ingredients.joinToString(", ") { it.name }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (recipe.cookingTime != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.recipes_cooking_time_format, recipe.cookingTime!!),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (!plan.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plan.notes ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 状态标签在最右侧
            PlanStatusChip(status = plan.status)
        }
    }
}

@Composable
private fun PlanStatusChip(status: PlanStatus) {
    val (text, color) = when (status) {
        PlanStatus.PLANNED -> stringResource(R.string.plans_status_planned) to MaterialTheme.colorScheme.primary
        PlanStatus.TRIED -> stringResource(R.string.plans_status_tried) to MaterialTheme.colorScheme.tertiary
        PlanStatus.SKIPPED -> stringResource(R.string.plans_status_skipped) to MaterialTheme.colorScheme.error
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.border(
            width = 1.dp,
            color = color.copy(alpha = 0.3f),
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) 29 else 28
        else -> 31
    }
}
