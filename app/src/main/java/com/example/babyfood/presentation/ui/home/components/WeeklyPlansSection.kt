package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babyfood.presentation.ui.home.PlanWithRecipe
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

@Composable
fun WeeklyPlansSection(
    weeklyPlans: Map<LocalDate, List<PlanWithRecipe>>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 标题栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "未来一周计划",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "折叠" else "展开"
                )
            }

            // 展开内容
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (weeklyPlans.isEmpty()) {
                        Text(
                            text = "暂无未来计划",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        weeklyPlans.entries
                            .sortedBy { it.key }
                            .forEach { (date, plans) ->
                                DayPlanItem(
                                    date = date,
                                    plans = plans
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DayPlanItem(
    date: LocalDate,
    plans: List<PlanWithRecipe>
) {
    // 使用本地化日期格式
    val dateFormat = java.text.SimpleDateFormat("M月d日 EEEE", java.util.Locale.getDefault())
    val dateString = dateFormat.format(
        java.util.Date(
            date.toJavaLocalDate().atTime(0, 0)
                .atZone(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = dateString,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (plans.isEmpty()) {
            Text(
                text = "暂无计划",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            plans.forEach { planWithRecipe ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${planWithRecipe.plan.mealPeriod.displayName}: ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    if (planWithRecipe.recipe != null) {
                        Text(
                            text = planWithRecipe.recipe.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "未设置",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}