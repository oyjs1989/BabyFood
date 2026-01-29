package com.example.babyfood.presentation.ui.plans

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.domain.model.PlanStatus
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanListScreen(
    onNavigateToDetail: (Long) -> Unit = {},
    onNavigateToAdd: (Long) -> Unit = {},
    onNavigateToRecommendationEditor: (Long) -> Unit = {},
    viewModel: PlansViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var selectedDate by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var currentMonth by remember { mutableStateOf(Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date) }
    var showDateRangePicker by remember { mutableStateOf(false) }
    var showAddMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("辅食计划") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            Box {
                // 主悬浮按钮
                FloatingActionButton(
                    onClick = { showAddMenu = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加")
                }
                
                // 选项菜单
                DropdownMenu(
                    expanded = showAddMenu,
                    onDismissRequest = { showAddMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("手动添加") },
                        leadingIcon = {
                            Icon(Icons.Default.Add, contentDescription = "手动添加")
                        },
                        onClick = {
                            showAddMenu = false
                            uiState.selectedBaby?.let { baby ->
                                onNavigateToAdd(baby.id)
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("AI推荐") },
                        leadingIcon = {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI推荐")
                        },
                        onClick = {
                            showAddMenu = false
                            showDateRangePicker = true
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // 选中日期和计划列表
            SelectedDatePlans(
                selectedDate = selectedDate,
                plans = uiState.plans.filter { it.plannedDate == selectedDate },
                onPlanClick = onNavigateToDetail
            )
        }
    }
    
    // 日期范围选择器对话框
    if (showDateRangePicker) {
        DateRangePickerDialog(
            onDismiss = { showDateRangePicker = false },
            onConfirm = { startDate, days ->
                showDateRangePicker = false
                // 生成AI推荐并跳转到编辑器
                uiState.selectedBaby?.let { baby ->
                    scope.launch {
                        val recommendation = viewModel.generateWeeklyRecommendation(baby.id, startDate, days)
                        if (recommendation != null) {
                            onNavigateToRecommendationEditor(baby.id)
                        }
                        // 如果recommendation为null，错误信息已经在ViewModel中设置了
                        // 错误对话框会自动显示
                    }
                }
            }
        )
    }
    
    // AI推荐加载对话框
    if (uiState.isGenerating) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("AI分析中") },
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
                        text = "正在为您生成个性化辅食计划...",
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
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("错误") },
            text = { Text(uiState.error ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("确定")
                }
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
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedBaby?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("选择宝宝") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                Icon(Icons.Default.ArrowBack, contentDescription = "上个月")
            }

            Text(
                text = "${currentMonth.year}年${currentMonth.monthNumber}月",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onNextMonth) {
                Icon(Icons.Default.ArrowForward, contentDescription = "下个月")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
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
    plans: List<com.example.babyfood.domain.model.Plan>,
    onPlanClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "${selectedDate.year}年${selectedDate.monthNumber}月${selectedDate.dayOfMonth}日",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (plans.isEmpty()) {
            Text(
                text = "暂无计划",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            plans.sortedBy { try { MealPeriod.valueOf(it.mealPeriod).order } catch (e: Exception) { 0 } }.forEach { plan ->
                PlanItem(
                    plan = plan,
                    onClick = { onPlanClick(plan.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PlanItem(
    plan: com.example.babyfood.domain.model.Plan,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = try { MealPeriod.valueOf(plan.mealPeriod).displayName } catch (e: Exception) { plan.mealPeriod },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "食谱ID: ${plan.recipeId}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (!plan.notes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plan.notes ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            PlanStatusChip(status = plan.status)
        }
    }
}

@Composable
private fun PlanStatusChip(status: PlanStatus) {
    val (text, color) = when (status) {
        PlanStatus.PLANNED -> "已计划" to MaterialTheme.colorScheme.primary
        PlanStatus.TRIED -> "已尝试" to MaterialTheme.colorScheme.tertiary
        PlanStatus.SKIPPED -> "已跳过" to MaterialTheme.colorScheme.error
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