package com.example.babyfood.presentation.ui.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.BreakfastDining
import androidx.compose.material.icons.outlined.LunchDining
import androidx.compose.material.icons.outlined.DinnerDining
import androidx.compose.material.icons.outlined.Cookie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.babyfood.domain.model.MealPeriod
import com.example.babyfood.presentation.theme.SecondaryContainer
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
                planWithRecipe = plans.find { it.plan.mealPeriod == period.name },
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
    val hasRecipe = planWithRecipe != null && planWithRecipe.recipe != null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectRecipe() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasRecipe) {
                MaterialTheme.colorScheme.surface
            } else {
                SecondaryContainer
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (hasRecipe) {
            null
        } else {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 时间图标和标签
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getMealIcon(period),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 时间标签
            Column(
                modifier = Modifier.width(60.dp)
            ) {
                Text(
                    text = period.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 食谱信息
            Column(
                modifier = Modifier.weight(1f)
            ) {
                if (hasRecipe) {
                    val recipe = planWithRecipe!!.recipe!!
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BreakfastDining,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${recipe.nutrition.calories?.toInt() ?: 0} kcal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    Text(
                        text = "点击添加食谱",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // 操作按钮组
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 换一换按钮
                IconButton(onClick = onShuffle) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "换一换",
                        tint = if (hasRecipe) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        }
                    )
                }

                // 分隔线
                Spacer(modifier = Modifier.width(8.dp))

                // 选择食谱按钮
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        )
                        .clickable { onSelectRecipe() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "选择食谱",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun getMealIcon(period: MealPeriod) = when (period) {
    MealPeriod.BREAKFAST -> Icons.Outlined.BreakfastDining
    MealPeriod.LUNCH -> Icons.Outlined.LunchDining
    MealPeriod.DINNER -> Icons.Outlined.DinnerDining
    MealPeriod.SNACK -> Icons.Outlined.Cookie
}