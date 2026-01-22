package com.example.babyfood.presentation.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.presentation.ui.home.PlanWithRecipe

@Composable
fun MealTimeline(
    plans: List<PlanWithRecipe>,
    onShuffle: (MealPeriod) -> Unit,
    onSelectRecipe: (MealPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        MealPeriod.values().forEach { period ->
            MealPeriodCard(
                period = period,
                planWithRecipe = plans.find { it.plan.mealPeriod == period },
                onShuffle = { onShuffle(period) },
                onSelectRecipe = { onSelectRecipe(period) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun MealPeriodCard(
    period: MealPeriod,
    planWithRecipe: PlanWithRecipe?,
    onShuffle: () -> Unit,
    onSelectRecipe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 时间标签
            Text(
                text = period.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(60.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // 食谱信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (planWithRecipe != null && planWithRecipe.recipe != null) {
                    Text(
                        text = planWithRecipe.recipe.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${planWithRecipe.recipe.nutrition.calories?.toInt() ?: 0} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = "暂无计划",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            // 换一换按钮
            IconButton(onClick = onShuffle) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "换一换",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // 选择食谱按钮
            IconButton(onClick = onSelectRecipe) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "选择食谱",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}