package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
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
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

@Composable
fun TodayMenuScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    // 使用本地化日期格式
    val dateFormat = java.text.SimpleDateFormat("M月d日", java.util.Locale.getDefault())
    val dateString = dateFormat.format(
        java.util.Date(
            today.toJavaLocalDateTime().atZone(
                java.time.ZoneId.systemDefault()
            ).toInstant().toEpochMilli()
        )
    )

    // 控制未来一周计划的展开/折叠状态
    var isWeeklyExpanded by remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ChildCare,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "还没有添加宝宝信息",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "请先在「宝宝信息」页面添加宝宝",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 日期标题
            Text(
                text = dateString,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // 营养目标卡片
            uiState.nutritionGoal?.let { goal ->
                NutritionGoalCard(nutritionGoal = goal)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 餐单时间轴
            Text(
                text = "今日餐单",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            MealTimeline(
                plans = uiState.todayPlans,
                onShuffle = { period -> viewModel.shuffleMealPeriod(period) },
                onSelectRecipe = { period ->
                    viewModel.showRecipeSelector(period, kotlinx.datetime.LocalDate(today.year, today.monthNumber, today.dayOfMonth))
                }
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
}